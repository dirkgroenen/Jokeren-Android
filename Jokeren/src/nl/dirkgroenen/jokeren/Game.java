package nl.dirkgroenen.jokeren;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class Game extends Activity{

	Deck deck = new Deck();
	ImageView ivimage, ivdeck;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.gamescreen);
		
		ivimage = (ImageView) findViewById(R.id.ivDeckOpen);
		ivdeck = (ImageView) findViewById(R.id.ivDeck);
		
		ivdeck.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				nextDeckCard();
			}
		});
	}
	
	private void nextDeckCard(){
		Log.i("CARD",deck.peek().getPngName());
		ivimage.setImageResource(deck.peek().getImageResourceId());
		deck.pop();
	}
}
