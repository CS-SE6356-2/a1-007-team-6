package bscardgameclient;

import java.util.ArrayList;

public class BSServerCommunication 
{
    public Integer lobby;
    public int numPlayers;
    public int numWinners;
    public boolean started;
    public String confirmR = "Recieved"; //reciept confirmation
    public String previousActionLog = "";
    public String currentActionLog = "";
    
    public int currentTurn;
    public int previousTurn;
    public int CurrentCard;
    public ArrayList<ArrayList<Integer>>  PlayerHands;
    public boolean emptyPile;
    
    public int actor;//player number
    public int action;//what they did: 0 is play a card, 1 is challenge, 2 is win
    public ArrayList<Integer> cardsPlayed;
    
    
    
    public BSServerCommunication()
    {
	CurrentCard = 0;    //ranges form 0-12 to represent ace-king
	cardsPlayed = new ArrayList<>();
	started = false;
	numWinners = 0;
	numPlayers = 0;
    }
}
