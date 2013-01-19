package nl.dirkgroenen.jokeren;

import android.widget.ImageView;

public class OppHand extends Hand{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OppHand(GameStrategy strategy, ImageView[] playerCards, String name) {
		super(playerCards, name);
		this.strategy = strategy;
	}
	
	public void selectCardsToDrop(){
		// Here comes the strategy that needs to decide what to do with his cards
	}

	@Override
	public boolean isAwaitingInput() {
		return false;
	}
	
	@Override
	public void selectCardsToDropToExisting(PlayedSet set) throws InvalidDropException {
		// TODO Auto-generated method stub
		
	}

}
