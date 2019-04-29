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
    ArrayList<Integer> lastCard;
    int numPlayers;
    Integer Lobby;
    Integer Winners[];
    public int winners;
    int port;
    boolean startcheck;
    boolean testconnection;
    public static volatile BSServerCommunication comms;
    Server server = new Server();
    Kryo kryo = server.getKryo(); 

    public Lobby(BSServerCommunication lobbyCreated) 
    {
	synchronized(server) {
	comms = lobbyCreated;
	Lobby = comms.lobby;
	Players = new LinkedList<>();
	pile = new DiscardPile();
	comms.emptyPile = true;
	startcheck = false;
	winners = 0;
        connections = new ArrayList<>();
        port = 54000 + Lobby;
	
	kryo.register(BSServerCommunication.class);
        kryo.register(java.util.ArrayList.class);
	//comms = new BSServerCommunication(Lobby);
	server.start();
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
		synchronized(server) {
		if (!connections.contains(connection))
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
		    comms.numPlayers = connections.size();//server.getConnections().length;
		}
		PushComms();
		}
            }
	    @Override
	    public void received (Connection connection, Object object) 
	    {
		synchronized(server) {
		if (object instanceof BSServerCommunication) 
		{
		setcomm((BSServerCommunication)object);
		    //BSServerCommunication comms = (BSServerCommunication)object;
		if (!startcheck && comms.started)
		{
		    startcheck = true;
		    StartGame();
		}
		switch(comms.action)
		{
		    //switch cases for playing a card, challenging, and winning
		    case 0: //card(s) played
			pile.addCards(comms.cardsPlayed);
			comms.PlayerHands.get(comms.actor).removeAll(comms.cardsPlayed);
			comms.cardsPlayed.clear();
			comms.emptyPile = false;
			NextPlayer();
			break;
		    case 1: //challenged
			Challenged();
			break;
		    case 2: //winner claimed; decide for serverside or client side checking
			Winners[winners] = comms.actor;
                        comms.currentActionLog = "Player " + (comms.actor) + " has won!"; 
			break;
		    default:	//error message
			System.out.println("Inproper action recieved by client");
		}
		 
		//connection.sendTCP(comms);
		PushComms();
	      }
	    }
	    }
	});
	}
    }

    public void Challenged()
    {
	synchronized(server) 
        {
            ArrayList challengeDeck = (ArrayList)pile.empty();
            if (pile.topCard == lastCard)
            {
                //challenger wrong if condition is met
                comms.PlayerHands.get(comms.actor - 1).addAll(challengeDeck);
                comms.currentActionLog = "Player " + (comms.actor) + " has called BS on " + (comms.actor - 1) + " and was wrong"; 
            }
            else
            {
                comms.PlayerHands.get(comms.actor - 1).addAll(challengeDeck);
                comms.emptyPile = true;
                comms.currentActionLog = "Player " + (comms.actor) + " has called BS on " + (comms.actor - 1) + " and was correct"; 
            }
        }
    }

    public void StartGame()
    {
	synchronized(server) {
	numPlayers = comms.numPlayers;
	for(int count = 1; count <= numPlayers; count++)
	{
	    Players.add(count);
	}
	comms.currentTurn = 1;
	CurrentCard = 0;
	if(numPlayers > 2)
	    Winners = new Integer[numPlayers - 2];
	comms.PlayerHands = new ArrayList<>();
	distributeCards();
	//NextPlayer();
	//PushComms();
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
		comms.PlayerHands.get(i).add(deck.remove(0));
	    }
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
	    comms.currentTurn = Turn;
	    Players.add(Turn);
	    if(CurrentCard == 12)
		CurrentCard = 0;
	    else
		CurrentCard++;
	    comms.CurrentCard = CurrentCard;
	    PushComms();
	}
    }
    
    public void PushComms()
    {
	synchronized(server) {
        //server.sendToAllTCP(comms);
	Iterator clients = connections.iterator();
        while(clients.hasNext())
        {
            ((Connection)clients.next()).sendTCP(comms);
        }
	}
    }
    public void setcomm(BSServerCommunication com)
    {
	comms = com;
    }
    public void getcomm(BSServerCommunication com)
    {
	com = comms;
    }
}
