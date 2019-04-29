package bscardgameserver;

import java.util.List;
import java.util.ArrayList;

public class DiscardPile 
{
	ArrayList<Integer> pile;
	ArrayList<Integer> topCard;
	
	public DiscardPile()
	{
	    pile = new ArrayList<>();
	    topCard = new ArrayList<>();
	}
	public void addCards(ArrayList cards) 
	{
		pile.addAll(cards);
		topCard.clear();
		topCard.addAll(cards);
	}
	
	public int size() {
		return pile.size();
	}
	
	public List empty() {
		List<Integer> copy;
                copy = new ArrayList<>(pile);
                pile.clear();
		//topCard = null;
                return copy;
	}

}
