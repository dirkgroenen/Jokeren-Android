package nl.dirkgroenen.jokeren;

import android.util.Log;
import android.widget.ImageView;

public class Hand {
	
	protected PlayingCard[] cards;
	private int firstFreeLocation;

	public Hand(ImageView[] playerCards, String name){
		this.cards = new PlayingCard[7];
		firstFreeLocation = 0;
	}

	public boolean isCardSelected(int cardIndex) {
		return cards[cardIndex].getSelected();
	}
	
	public int addCard(PlayingCard card){
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

}
