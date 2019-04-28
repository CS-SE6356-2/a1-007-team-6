package bscardgameclient;

import java.util.ArrayList;

public class BSServerCommunication {
    public Integer lobby;
    int numPlayers;
    int numWinners;
    boolean started;
    String confirmR = "Recieved"; //reciept confirmation
    
    int currentTurn;
    int previousTurn;
    int CurrentCard;
    ArrayList<ArrayList<Integer>>  PlayerHands;
    boolean emptyPile;
    
    int actor;//player number
    int action;//what they did: 0 is play a card, 1 is challenge, 2 is win
    ArrayList<Integer> cardsPlayed;
    
    
    
    public BSServerCommunication()
    {
	CurrentCard = 0;    //ranges form 0-12 to represent ace-king
	cardsPlayed = new ArrayList<>();
	started = false;
	numWinners = 0;
    }
}
