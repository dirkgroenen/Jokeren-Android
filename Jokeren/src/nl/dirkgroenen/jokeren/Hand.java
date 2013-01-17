package nl.dirkgroenen.jokeren;

import java.io.Serializable;
import java.util.ArrayList;

import android.util.Log;
import android.widget.ImageView;

public class Hand implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected PlayingCard[] cards;
	private int firstFreeLocation,lastShowedCard;
	private String playerName;

	public Hand(ImageView[] playerCards, String name){
		this.cards = new PlayingCard[14];
		this.playerName = name;
		firstFreeLocation = 0;
	}

	public boolean isCardSelected(int cardIndex) {
		return cards[cardIndex].getSelected();
	}
	
	public int addCard(PlayingCard card){
		Log.i("ADD", "Card ("+card.getPngName()+") given to "+playerName+" at position: "+firstFreeLocation);
		lastShowedCard = firstFreeLocation;
		cards[firstFreeLocation] = card;
		return firstFreeLocation++;
	}

	public void changeCardSelectedState(int cardIndex) {
		if(cards[cardIndex].getSelected()){
			cards[cardIndex].setSelected(false);
		}
		else{
			cards[cardIndex].setSelected(true);
		}
	}

	public PlayingCard getCardByPosition(int index) {
		return cards[index];
	}
	
	public ArrayList<PlayingCard> dropSelectedCards(){
		ArrayList<PlayingCard> tempCards = new ArrayList<PlayingCard>();
		for(int index = 0;index < cards.length;index++){
			if(cards[index] != null && cards[index].getSelected() == true){
				tempCards.add(cards[index]);
				cards[index] = null;
			}
		}
		
		compactHand();
		resetSelected();
		
		return tempCards;
	}

	public PlayingCard throwSelectedCardToDeck(){
		int countSelected = 0;
		int cardPosition = 0;
		for(int index = 0;index < cards.length;index++){
			if(cards[index] != null && cards[index].getSelected() == true){
				countSelected++;
				cardPosition = index;
			}
		}
		
		if(countSelected > 1 && countSelected != 0){
			return null;
		}
		else{
			PlayingCard temp = cards[cardPosition];
			cards[cardPosition] = null;
			
			compactHand();
			resetSelected();
			
			return temp;
		}
	}
	
	private void compactHand() {
		PlayingCard[] compactedArr = new PlayingCard[14];
        int idxInCompactedArr=0;
        for (int idxInCards = 0; idxInCards < cards.length; idxInCards++) {
                if (cards[idxInCards]!=null) {
                        compactedArr[idxInCompactedArr++] = cards[idxInCards];
                }
        }
        this.cards = compactedArr;
        this.firstFreeLocation = idxInCompactedArr;
        Log.i("COMPACT","First free location at position: "+idxInCompactedArr);
	}
	
	private void resetSelected() {
		for(PlayingCard card : cards){
			if(card != null && card.getSelected() == true){
				card.setSelected(false);
			}
		}
	}
	
	public int getFirstFreeLocation(){
		Log.i("LOCATION", "Last showed card: "+lastShowedCard);
		return lastShowedCard;
	}

}
