package nl.dirkgroenen.jokeren;

import java.io.Serializable;
import java.util.ArrayList;

import android.util.Log;

public class PlayedSet implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ArrayList<PlayingCard> set;
	
	// You can use this class to create a new set, or view a already existing set.
	public PlayedSet(){
		set = new ArrayList<PlayingCard>(13);
		Log.i("SET","New set created");
	}
	
	public PlayedSet(ArrayList<PlayingCard> set){
		this.set = set;
	}
	
	// Add a new card to the set
	public void addCardToSet(PlayingCard card){
		set.add(card);
	}
	
	// Remove a card from the set based on the given position
	public void removeCardByPosition(int pos){
		set.remove(pos);
	}
	
	// View a card in the set based on the given position
	public PlayingCard getCardByPosition(int pos){
		return set.get(pos);
	}
	
	// Get all cards in set
	public ArrayList<PlayingCard> getAllCards(){
		return set;
	}
}
