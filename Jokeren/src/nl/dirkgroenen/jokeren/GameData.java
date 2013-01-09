package nl.dirkgroenen.jokeren;

import java.util.ArrayList;

public class GameData {

	private Hand playerHand, oppHand;
	private ArrayList<Hand> playersInOrder;
	
	public void init(Hand playerHand, Hand oppHand, ArrayList<Hand> playersInOrder) {
		this.playerHand = playerHand;
		this.oppHand = oppHand;
	}

	
	public Hand getPlayerHand(){
		return playerHand;
	}
	public Hand getOppHand(){
		return oppHand;
	}
	public ArrayList<Hand> getPlayersInOrder(){
		return playersInOrder;
	}
}
