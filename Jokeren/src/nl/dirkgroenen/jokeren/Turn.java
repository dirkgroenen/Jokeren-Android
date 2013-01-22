package nl.dirkgroenen.jokeren;

import java.io.Serializable;
import java.util.ArrayList;

import android.util.Log;

public class Turn implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ArrayList<Hand> players;
	private int currentPlayer;
	private int turns = 0;
	
	public Turn(){
		throw new UnsupportedOperationException("cannot init without players");
	}
	
	public Turn(ArrayList<Hand> players, int currentPlayer){
		this.players = players;
		this.currentPlayer = currentPlayer;
		
		Log.i("TURN", "This game will be played with "+players.size()+" players.");
		Log.i("TURN", "This game will start with player: "+players.get(currentPlayer).getPlayerName());
	}
	
	public Hand nextTurn(){
		turns++;
		currentPlayer++;
		currentPlayer = (currentPlayer == players.size()) ? 0 : currentPlayer ;
		return players.get(currentPlayer);
	}
	
	public Hand getTurn(){
		return players.get(currentPlayer);
	}
	
	public int getTurnCount(){
		return turns;
	}
	
	public int getCurrentPlayer(){
		return currentPlayer;
	}
}
