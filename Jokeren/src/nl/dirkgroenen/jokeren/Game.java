package nl.dirkgroenen.jokeren;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Game extends Activity{

	Deck deck = new Deck();
	GameData gameData = new GameData();
	Hand playerHand, oppHand;
	private ImageView ivDeckClosed, ivDeckOpen, ivPlayerCard1,ivPlayerCard2,ivPlayerCard3,ivPlayerCard4,ivPlayerCard5,ivPlayerCard6,ivPlayerCard7, ivGrabbedCard;
	private LinearLayout llPlayerHand;
	private ImageView[] playerCards;
	private ArrayList<Hand> playersInOrder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.gamescreen);
		
		ivDeckOpen = (ImageView) findViewById(R.id.ivDeckOpen);
		ivDeckClosed = (ImageView) findViewById(R.id.ivDeck);
		ivGrabbedCard = (ImageView) findViewById(R.id.ivgrabbedcard);
		
		//Player's cards
		llPlayerHand = (LinearLayout) findViewById(R.id.llbottomcards);
		ivPlayerCard1 = (ImageView) findViewById(R.id.ivcard0_1);
		ivPlayerCard2 = (ImageView) findViewById(R.id.ivcard0_2);
		ivPlayerCard3 = (ImageView) findViewById(R.id.ivcard0_3);
		ivPlayerCard4 = (ImageView) findViewById(R.id.ivcard0_4);
		ivPlayerCard5 = (ImageView) findViewById(R.id.ivcard0_5);
		ivPlayerCard6 = (ImageView) findViewById(R.id.ivcard0_6);
		ivPlayerCard7 = (ImageView) findViewById(R.id.ivcard0_7);
		playerCards = new ImageView[] {ivPlayerCard1,ivPlayerCard2,ivPlayerCard3,ivPlayerCard4,ivPlayerCard5,ivPlayerCard6,ivPlayerCard7,ivGrabbedCard};
		
		//Create array with players and their hand
		playersInOrder = new ArrayList<Hand>();
		playerHand = new Hand(playerCards,"Player name");
		playersInOrder.add(playerHand);
		oppHand = new Hand(null,"Opponent");
		playersInOrder.add(oppHand);		
		
		ivDeckOpen.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(!gameData.getGrabbedCard()){
					grabOpenDeckCard();
				}
				else{
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.grabbedCardError), Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		ivDeckClosed.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(!gameData.getGrabbedCard()){
					grabClosedDeckCard();
				}
				else{
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.grabbedCardError), Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		for(int cardIndex = 0;cardIndex < playerCards.length;cardIndex++){
			final ImageView card = playerCards[cardIndex];
			final int finalIndex = cardIndex;
			
			card.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					markPlayerCardSelected(finalIndex);
				}
			});
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		startGame();
	}
	

	private void startGame(){
		// Push all data to gamedata class
		gameData.init(playerHand,oppHand,playersInOrder,deck);	
		gameData.setGameInProgress(true);
		
		// Deal cards to players
		dealCards();
		
	}
	
	private void dealCards(){
		for(int c = 0;c < 7;c++){
			for(Hand hand : playersInOrder){
				PlayingCard card = gameData.getDeck().getNextCard();
				hand.addCard(card);
				playerCards[c].setImageResource(card.getImageResourceId());
			}
		}
		
		// Open top deck card (aka, peek)
		ivDeckOpen.setImageResource(gameData.getDeck().openTopCard().getImageResourceId());
		ivDeckOpen.setVisibility(View.VISIBLE);
	}
	
	//Mark card selected/unselected when user click it
	private void markPlayerCardSelected(int cardIndex) {
		ImageView card = playerCards[cardIndex];
		boolean selected = gameData.getPlayerHand().isCardSelected(cardIndex);
		
		Log.d("CARD","Card ["+cardIndex+"] = "+selected);
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(card.getLayoutParams().width, card.getLayoutParams().height, 1);
		params.gravity = !selected ? Gravity.TOP : Gravity.BOTTOM;
		card.setLayoutParams(params);
		
		gameData.getPlayerHand().changeCardSelectedState(cardIndex);
	}
	
	// Grab a card from the open stack
	private void grabOpenDeckCard(){
		ivGrabbedCard.setVisibility(View.VISIBLE);
		gameData.setGrabbedCard(true);
		
		PlayingCard card = gameData.getDeck().getTopThrownCard();
		
		if(gameData.getDeck().getThrownDeckSize() < 2){
			ivDeckOpen.setVisibility(View.INVISIBLE);
		}
		else{
			ivDeckOpen.setVisibility(View.VISIBLE);
		}
		
		gameData.getPlayerHand().addCard(card);
		ivGrabbedCard.setImageResource(card.getImageResourceId());
	}
	
	// Grab a card from the open stack
	private void grabClosedDeckCard(){
		ivGrabbedCard.setVisibility(View.VISIBLE);
		gameData.setGrabbedCard(true);
		
		PlayingCard card = gameData.getDeck().getNextCard();
		
		gameData.getPlayerHand().addCard(card);
		ivGrabbedCard.setImageResource(card.getImageResourceId());
	}
}
