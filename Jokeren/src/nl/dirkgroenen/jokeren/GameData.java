package nl.dirkgroenen.jokeren;

import java.util.ArrayList;

public class GameData {

	private Hand playerHand, oppHand;
	private ArrayList<Hand> playersInOrder;
	private Deck deck;
	private boolean gameInProgress;
	private boolean grabbedCard;
	
	public void init(Hand playerHand, Hand oppHand, ArrayList<Hand> playersInOrder, Deck deck) {
		this.playerHand = playerHand;
		this.playersInOrder = playersInOrder;
		this.oppHand = oppHand;
		this.deck = deck;
		this.grabbedCard = false;
	}

	
	public Hand getPlayerHand(){
		return playerHand;
	}
	public Hand getOppHand(){
		return oppHand;
	}
	public Deck getDeck(){
		return deck;
	}
	public ArrayList<Hand> getPlayersInOrder(){
		return playersInOrder;
	}
	public void setGrabbedCard(boolean set){
		this.grabbedCard = set;
	}
	public boolean getGrabbedCard(){
		return grabbedCard;
	}

	public void setGameInProgress(boolean progress) {
		this.gameInProgress = progress;
	}
}