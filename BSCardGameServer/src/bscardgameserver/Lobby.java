package bscardgameserver;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import com.esotericsoftware.kryonet.Server;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Lobby extends Game
{
    ArrayList<Connection> connections;
    Queue<Integer> Players;
    DiscardPile pile;
    int Turn;
    int LastTurn;
    int CurrentCard;
    ArrayList<Integer> lastCard;
    int numPlayers;
    Integer Lobby;
    Integer Winners[];
    int winners;
    int port;
    boolean startcheck;
    boolean testconnection;
    
    BSServerCommunication comms;
    Server server = new Server();
    Kryo kryo = server.getKryo(); 

    public Lobby(BSServerCommunication lobbyCreated) 
    {
	comms = lobbyCreated;
	Lobby = comms.lobby;
	Players = new LinkedList<>();
	pile = new DiscardPile();
	comms.emptyPile = true;
	startcheck = false;
	testconnection = false;
	winners =0;
        connections = new ArrayList<>();
        port = 54000 + Lobby;
	
	kryo.register(BSServerCommunication.class);
        kryo.register(java.util.ArrayList.class);
	//comms = new BSServerCommunication(Lobby);
	new Thread(server).start();
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
	    @Override
	    public void received (Connection connection, Object object) 
	    {
		if (object instanceof BSServerCommunication) 
		{
		BSServerCommunication request = (BSServerCommunication)object;
		System.out.println(request.confirmR);
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
			break;
		    case 1: //challenged
			Challenged();
			break;
		    case 2: //winner claimed; decide for serverside or client side checking
			Winners[winners] = comms.actor;
			break;
		    default:	//error message
			System.out.println("Inproper action recieved by client");
		}
		 
		//connection.sendTCP(comms);
		PushComms();
	      }
	    }
	});
    }

    public void Challenged()
    {
	ArrayList challengeDeck = (ArrayList)pile.empty();
	if (pile.topCard == lastCard) //challenger wrong if condition is met
	    comms.PlayerHands.get(comms.actor).addAll(challengeDeck);
	else
	    comms.PlayerHands.get(comms.actor).addAll(challengeDeck);
	comms.emptyPile = true;
    }

    public void StartGame()
    {
	numPlayers = comms.numPlayers;
	for(int count = 1; count <= numPlayers; count++)
	{
	    Players.add(count);
	}
	comms.currentTurn = 1;
	CurrentCard = 0;
	Winners = new Integer[numPlayers - 2];
	distributeCards();
	//NextPlayer();
	PushComms();
    }
    
    public void distributeCards()
    {
	ArrayList<Integer> deck = new ArrayList<>();
	for(int counter = 0; counter < 52; counter++)
	{
	    deck.add(counter);
	}
	Collections.shuffle(deck);
	
	//split the cards evenly between the players
	int each = 52 - (52 % numPlayers);
	for(int i = 0; i < numPlayers; i++)
	{
	    for(int j = 0; j < each; j++)
	    {
		comms.PlayerHands.get(comms.actor).add(deck.remove(0));
	    }
	}
	//the remaining cards seed the discard pile
	pile.addCards(deck);
    }

    public void NextPlayer()
    {
	LastTurn = comms.currentTurn;
	comms.currentTurn = Players.poll();
	Players.add(Turn);
        PushComms();
    }
    
    public void PushComms()
    {
	//server.sendToAllTCP(comms);
	/*Iterator clients = connections.iterator();
        while(clients.hasNext())
        {
            ((Connection)clients.next()).sendTCP(comms);
        }*/
    }
}
