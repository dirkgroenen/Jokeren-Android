package nl.dirkgroenen.jokeren;

import java.io.Serializable;
import java.util.Collections;
import java.util.Stack;

import android.util.Log;

public class Deck implements Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//PlayingCard collection
	protected Stack<PlayingCard> cards = new Stack<PlayingCard>();
	protected Stack<PlayingCard> throwncards = new Stack<PlayingCard>();
	
	private static final char[] suits = {PlayingCard.DIAMOND,PlayingCard.CLUBS,PlayingCard.HEARTS,PlayingCard.SPADES};
	private static final char[] values = {PlayingCard.ACE,'2','3','4','5','6','7','8','9',PlayingCard.TEN,PlayingCard.JACK,PlayingCard.QUEEN,PlayingCard.KING};
	
	
	// Constructor that will fill the carddeck
	public Deck(){
		for(char suit : suits){
			for(char value : values){
				cards.add(new PlayingCard(suit, value));
			}
		}
		
		//Add two jokers
		cards.add(new PlayingCard(PlayingCard.RED_SUITS,PlayingCard.JOKER));
		cards.add(new PlayingCard(PlayingCard.BLACK_SUITS,PlayingCard.JOKER));
		
		//Shuffle the cards
		Collections.shuffle(cards);
	}
	
	public int size(){
		return cards.size();
	}
	
	public PlayingCard peek(){
		return cards.peek();
	}
	
	public void getThrownCards(){
		// Puts all the throwncards in the new deck and shuffle them
		cards.addAll(throwncards);
		Collections.shuffle(cards);
		
		// Clear the throwncards for new usage
		throwncards.clear();
	}
	
	public PlayingCard getNextCard(){
		if(cards.size() == 0){
			getThrownCards();
		}
		PlayingCard card = cards.peek();
		cards.pop();
		return card;
	}
	
	public PlayingCard openTopCard(){
		Log.i("DECK", "Cards: "+throwncards.size());
		PlayingCard card = cards.peek();
		throwncards.add(peek());
		cards.pop();
		Log.i("DECK", "Cards: "+throwncards.size());
		return card;
	}
	
	public int getThrownDeckSize(){
		return throwncards.size();
	}
	
	public int getThrownCardsCount(){
		return throwncards.size();
	}
	
	public PlayingCard getTopThrownCard(){
		PlayingCard card =  throwncards.peek();
		throwncards.pop();
		return card;
	}
	
	public PlayingCard peekThrownCards(){
		PlayingCard top = throwncards.peek();
		return top;
	}
	
	public void addThrownCardToDeck(PlayingCard card){		
		throwncards.add(card);
		Log.i("DECK", "Cards: "+throwncards.size());
	}
}
