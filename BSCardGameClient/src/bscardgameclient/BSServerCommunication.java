package bscardgameclient;

import java.util.ArrayList;

public class BSServerCommunication {
    Integer lobby;
    int numPlayers;
    boolean started;
    String confirmR = "Recieved"; //reciept confirmation
    
    int currentTurn;
    int previousTurn;
    ArrayList[] PlayerHands;
    boolean emptyPile;
    
    int actor;//player number
    int action;//what they did: 0 is play a card, 1 is challenge, 2 is win
    ArrayList<Integer> cardsPlayed;
    
    
    
    BSServerCommunication(Integer lobbycode)
    {
	lobby = lobbycode;
	PlayerHands = new ArrayList[6];
	cardsPlayed = new ArrayList<>();
	started = false;
    }
}
