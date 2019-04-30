package bscardgameserver;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Lobby extends Game
{
    public ArrayList<Connection> connections;
    public Queue<Integer> Players;
    public DiscardPile pile;
    int Turn;
    int LastTurn;
    int CurrentCard;
    int lastCard;
    int numPlayers;
    Integer Lobby;
    Integer Winners[];
    public int winners;
    int port;
    boolean startcheck;
    boolean testconnection;
    public static volatile BSServerCommunication comms;
    Server server= new Server();
    Kryo kryo = server.getKryo(); 


    public Lobby(BSServerCommunication lobbyCreated) 
    {
	synchronized(server) {
	kryo.register(BSServerCommunication.class);
	kryo.register(java.util.ArrayList.class);
	server.start();
	comms = lobbyCreated;
	Lobby = comms.lobby;
	Players = new LinkedList<>();
	pile = new DiscardPile();
	comms.emptyPile = true;
	startcheck = false;
	winners = 0;
        connections = new ArrayList<>();
        port = 54000 + Lobby;
	
	try {
	    server.bind(port, port);
	} catch (IOException ex) {
	    Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
	}
	
	server.addListener(new Listener() 
	{
            @Override
            public void connected (Connection connection) 
            {
		System.out.println("connection");
		synchronized(server) {
		/*if (!connections.contains(connection))
		{
		    if (connections.isEmpty())
		    {
			connections.add(connection);
			testconnection = true;
		    }
		    else if (testconnection)
		    {
			connections.add(connection);
			testconnection = false;
		    }
		    else
		    {
			connections.remove(connections.size() - 1);
			connections.add(connection);
			testconnection = true;
		    }
		    //comms.numPlayers = connections.size();//server.getConnections().length;
		}*/
		PushComms();
		}
            }
	    @Override
	    public void received (Connection connection, Object object) 
	    {
	    synchronized(server) {
	    if (object instanceof BSServerCommunication) 
	    {
		System.out.println("recieved something");
		setcomm((BSServerCommunication)object);
		if (!startcheck && comms.started)
		{
		    startcheck = true;
		    StartGame();
		    System.out.println("game started");
		}
		else
		{
		    switch(comms.action)
		    {
			//switch cases for playing a card, challenging, and winning
			case -1:
			    System.out.println("new client incremented playernum");
			    break;
			case 0: //card(s) played
			    if(!comms.cardsPlayed.isEmpty())
			    {
			    pile.addCards(comms.cardsPlayed);
			    System.out.println("Someone is playing a card");
			    comms.PlayerHands.get(comms.actor).removeAll(comms.cardsPlayed);
			    int numcards = comms.cardsPlayed.size();
			    newMSG("Player " + Integer.toString(comms.actor + 1) + " played " + numcards + " " + toCard(CurrentCard) + (numcards > 1 ? "s" : ""));
			    comms.cardsPlayed.clear();
			    comms.emptyPile = false;
			    NextPlayer();
			    }
			    else
				System.out.println("nothingplayed");
			    break;
			case 1: //challenged
			    Challenged();
			    break;
			case 2: //winner claimed; decide for serverside or client side checking
			    Winners[winners] = comms.actor + 1;
			    newMSG("Player " + (comms.actor + 1) + " has won!"); 
			    comms.numWinners = ++winners;
                            if(winners == numPlayers - 1)
                            {
                                String places = "";
                                comms.isGameOver = true;
                                for(int winningPlayers : Winners)
                                {
                                    places = places + winningPlayers + ",";
                                }
                                newMSG("Game is over");
                                places = "Winners in order are Players: " + places;
                                newMSG(places.substring(0, places.length() - 1));
                            }
                            NextPlayer();
                            Players.remove(comms.actor + 1);
			    break;
			default:	//error message
			    System.out.println("Inproper action recieved by client");
		    }   
		}

		//connection.sendTCP(comms);
		PushComms();
	    }
	    }
	    }
	});
	}
    }

    public boolean Challenged()
    {
	synchronized(server) 
        {
            ArrayList challengeDeck = (ArrayList)pile.empty();
	    ArrayList<Integer> anticipated = new ArrayList<>();
	    anticipated.add(lastCard);
	    pile.topCard.removeAll(anticipated);
            if (pile.topCard.isEmpty())
            {
                //challenger wrong if condition is met
                comms.PlayerHands.get(comms.actor).addAll(challengeDeck);
                Collections.sort(comms.PlayerHands.get(comms.actor));
                newMSG("Player " + (comms.actor + 1) + " has called BS on " + (comms.previousTurn) + " and was wrong"); 
		return false;
            }
            else
            {
                comms.PlayerHands.get(comms.previousTurn-1).addAll(challengeDeck);
                Collections.sort(comms.PlayerHands.get(comms.previousTurn-1));
                comms.emptyPile = true;
                newMSG("Player " + (comms.actor + 1) + " has called BS on " + (comms.previousTurn) + " and was correct"); 
		return true;
            }
        }
    }

    public void StartGame()
    {
	synchronized(server) {
	numPlayers = comms.numPlayers;
	CurrentCard = 0;
        Winners = new Integer[numPlayers - 1];
	comms.PlayerHands = new ArrayList<>();
	distributeCards();
        comms.currentTurn = Turn;
        for(int count = Turn + 1; count <= numPlayers; count++)
	{
            Players.add(count);
	}
        for(int count = 1; count <= Turn; count++)
        {
            Players.add(count);
        }
        
    }
    }
    
    public void distributeCards()
    {
	synchronized(server) {
	ArrayList<Integer> deck = new ArrayList<>(Arrays.asList(0, 0));
	deck.clear();
	for(int counter = 0; counter < 52; counter++)
	{
	    deck.add(counter);
	}
	Collections.shuffle(deck);
	
	//split the cards evenly between the players
	int each = (52 - (52 % numPlayers))/numPlayers;
	for(int i = 0; i < numPlayers; i++)
	{
	    comms.PlayerHands.add(new ArrayList<Integer>(Arrays.asList(0, 0)));
	    comms.PlayerHands.get(i).clear();
	    for(int j = 0; j < each; j++)
	    {
                if(deck.get(0) == 0)
                    Turn = i+1;
		comms.PlayerHands.get(i).add(deck.remove(0));
	    }
            Collections.sort(comms.PlayerHands.get(i));
	}
	//the remaining cards seed the discard pile
	pile.addCards(deck);
	}
    }

    public void NextPlayer()
    {
	synchronized(server) {
	    LastTurn = comms.currentTurn;
	    comms.previousTurn = LastTurn;
	    Turn = Players.poll();
            System.out.println("Current turn afer players.poll(): " + comms.currentTurn);
	    comms.currentTurn = Turn;
            System.out.println("Current turn afer assignment to Turn: " + comms.currentTurn);
	    Players.add(Turn);
            lastCard = CurrentCard;
	    if(CurrentCard == 12)
		CurrentCard = 0;
	    else
		CurrentCard++;
	    comms.CurrentCard = CurrentCard;
	    //PushComms();
	}
    }
    
    public void PushComms()
    {
	synchronized(server) {
	System.out.println("pushing");
        server.sendToAllTCP(comms);
	}
    }
    
    public void newMSG(String str)
    {
        if(comms.currentActionLog != str)
        {
            comms.previousActionLog = comms.currentActionLog;
            comms.currentActionLog = str;
        }
    }
    
    public String toCard(int c)
    {
	switch(c)
	{
	    case 0:
		return "Ace";
	    case 1:
		return "Two";
	    case 2:
		return "Three";
	    case 3:
		return "Four";
	    case 4:
		return "Five";
	    case 5:
		return "Six";
	    case 6:
		return "Seven";
	    case 7:
		return "Eight";
	    case 8:
		return "Nine";
	    case 9:
		return "Ten";
	    case 10:
		return "Jack";
	    case 11:
		return "Queen";
	    case 12:
		return "King";
	    default:
		return "Invalid Card";
	}
    }
    public void setcomm(BSServerCommunication com)
    {
	comms = com;
    }
    public BSServerCommunication getcomm()
    {
	return comms;
    }
}
