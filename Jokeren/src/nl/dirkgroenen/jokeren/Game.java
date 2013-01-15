package nl.dirkgroenen.jokeren;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Game extends Activity{

	private Deck deck;
	private GameData gameData;
	Hand playerHand, oppHand;
	private ImageView ivDeckClosed, ivDeckOpen, ivPlayerCard1,ivPlayerCard2,ivPlayerCard3,ivPlayerCard4,ivPlayerCard5,ivPlayerCard6,ivPlayerCard7, ivGrabbedCard;
	private ImageView[] playerCards;
	private ArrayList<Hand> playersInOrder;
	public static enum STATES{start,resume,end};
	private boolean firstRun;
	public static final int START_CODE = 0;
	public static final int RESUME_CODE = 1;
	public static final String GAME_STATE = "STATE";
	public static final String GAME_DATA = "GameData";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Get passed gamedata from intent
		if(getIntent().getExtras() != null){
			gameData = (GameData)getIntent().getExtras().getSerializable(GAME_DATA);
			Log.i("CORE","Gamedata gets set with data received from mainscreen");
		}
		else{
			Log.i("CORE","Empty intent, not good!");
		}
		
		setContentView(R.layout.gamescreen);
		
		// Load which state was given by the mainscreen
		switch ((STATES)getIntent().getExtras().get(GAME_STATE)){
		case start:
			firstRun = true;
			Log.i("ONCREATE","Received state: start");
			break;
		case resume:
			firstRun = false;
			Log.i("ONCREATE","Received state: resume");
			break;
		default:
			firstRun = true;
			Log.i("ONCREATE","Received state: none");
			break;
		}
		
		// Transferring game data to MainScreen 
        Bundle b = new Bundle();
        b.putInt("int", 5);
        b.putSerializable(GAME_DATA, gameData);
        Intent i = new Intent();
        i.putExtras(b);
        setResult(0, i);
		
		//Init the game
		init(firstRun);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		if(firstRun){
			init(false);
			firstRun = false;
		}
		
		// Init click listeners
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
	
	private void init(boolean first){
		initGraphics();
		Log.i("INIT","Game init graphics");
		if(first){
			Log.i("INIT","Game init core");
			initGameCore();
		}
	}
	
	private void initGraphics(){
		ivDeckOpen = (ImageView) findViewById(R.id.ivDeckOpen);
		ivDeckClosed = (ImageView) findViewById(R.id.ivDeck);
		ivGrabbedCard = (ImageView) findViewById(R.id.ivgrabbedcard);
		
		//Player's cards
		ivPlayerCard1 = (ImageView) findViewById(R.id.ivcard0_1);
		ivPlayerCard2 = (ImageView) findViewById(R.id.ivcard0_2);
		ivPlayerCard3 = (ImageView) findViewById(R.id.ivcard0_3);
		ivPlayerCard4 = (ImageView) findViewById(R.id.ivcard0_4);
		ivPlayerCard5 = (ImageView) findViewById(R.id.ivcard0_5);
		ivPlayerCard6 = (ImageView) findViewById(R.id.ivcard0_6);
		ivPlayerCard7 = (ImageView) findViewById(R.id.ivcard0_7);
		playerCards = new ImageView[] {ivPlayerCard1,ivPlayerCard2,ivPlayerCard3,ivPlayerCard4,ivPlayerCard5,ivPlayerCard6,ivPlayerCard7,ivGrabbedCard};
	}
	
	private void initGameCore(){
		deck = new Deck();
		
		//Create array with players and their hand
		playersInOrder = new ArrayList<Hand>();
		playerHand = new Hand(playerCards,"Player name");
		playersInOrder.add(playerHand);
		oppHand = new Hand(null,"Opponent");
		playersInOrder.add(oppHand);		
		
		// Push all data to gamedata class
		gameData.init(playerHand,oppHand,playersInOrder,deck);	
		gameData.setGameInProgress(true);
				
		// Deal cards to players
		dealCards();
	}
	
	/////////////// ////////////////////// ////////////////////
	////////////// END OF STANDARD CHECKS /////////////////////
	///////////// //////////////////////// ///////////////////
	
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
