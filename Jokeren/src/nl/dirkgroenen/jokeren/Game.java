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

public class Game extends Activity {

	private Deck deck;
	private GameData gameData;
	Hand playerHand, oppHand;
	private ImageView ivDeckClosed, ivDeckOpen, ivPlayerCard1, ivPlayerCard2,ivPlayerCard3, ivPlayerCard4, ivPlayerCard5, ivPlayerCard6,ivPlayerCard7, ivPlayerCard8, ivPlayerCard9, ivPlayerCard10,ivPlayerCard11, ivPlayerCard12, ivPlayerCard13, ivPlayerCard14;
	private ImageView[] playerCards;
	private ArrayList<Hand> playersInOrder;
	private LinearLayout llPlayGround,llCardDeck;
	private ArrayList<PlayedSet> playedSets;

	public static enum STATES {
		start, resume, end
	};

	private boolean firstRun;
	public static final int START_CODE = 0;
	public static final int RESUME_CODE = 1;
	public static final String GAME_STATE = "STATE";
	public static final String GAME_DATA = "GameData";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get passed gamedata from intent
		if (getIntent().getExtras() != null) {
			gameData = (GameData) getIntent().getExtras().getSerializable(GAME_DATA);
			Log.i("CORE","Gamedata gets set with data received from mainscreen");
		} else {
			Log.i("CORE", "Empty intent, not good!");
		}

		setContentView(R.layout.gamescreen);

		// Load which state was given by the mainscreen
		switch ((STATES) getIntent().getExtras().get(GAME_STATE)) {
		case start:
			firstRun = true;
			Log.i("ONCREATE", "Received state: start");
			break;
		case resume:
			firstRun = false;
			Log.i("ONCREATE", "Received state: resume");
			break;
		default:
			firstRun = true;
			Log.i("ONCREATE", "Received state: none");
			break;
		}

		// Transferring game data to MainScreen
		Bundle b = new Bundle();
		b.putInt("int", 5);
		b.putSerializable(GAME_DATA, gameData);
		Intent i = new Intent();
		i.putExtras(b);
		setResult(0, i);

		// Init the game
		init(firstRun);
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (firstRun) {
			init(false);
			firstRun = false;
		}

		// Init click listeners
		llCardDeck.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("CLICK","Card Deck clicked");
				
				if(gameData.canPlayerThrow()){
					throwCardToDeck();
				}
			}
		});
		
		ivDeckOpen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Log.i("CLICK","Open card deck clicked");
				if(gameData.canPlayerThrow()){
					throwCardToDeck();
				}
				else if (gameData.getGrabbedCard()) {
					Toast.makeText(	getApplicationContext(),getResources().getString(R.string.grabbedCardError),Toast.LENGTH_SHORT).show();
				} else if(gameData.canPlayerThrow()){
					Toast.makeText(	getApplicationContext(),getResources().getString(R.string.throwFirstCardError),Toast.LENGTH_SHORT).show();
				} else {
					grabOpenDeckCard();
				}
			}
		});

		ivDeckClosed.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Log.i("CLICK","Closed card deck clicked");
				if (gameData.getGrabbedCard()) {
					Toast.makeText(	getApplicationContext(),getResources().getString(R.string.grabbedCardError),Toast.LENGTH_SHORT).show();
				} else if(gameData.canPlayerThrow()){
					Toast.makeText(	getApplicationContext(),getResources().getString(R.string.throwFirstCardError),Toast.LENGTH_SHORT).show();
				} else {
					grabClosedDeckCard();
				}
			}
		});
		
		llPlayGround.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("CLICK","Play ground clicked");
				if(gameData.getGrabbedCard()){
					createNewPlaySet();
				}
				else{
					Toast.makeText(	getApplicationContext(),getResources().getString(R.string.grabCardFirstError),Toast.LENGTH_SHORT).show();
				}
				
			}
		});

		for (int cardIndex = 0; cardIndex < playerCards.length; cardIndex++) {
			final ImageView card = playerCards[cardIndex];
			final int finalIndex = cardIndex;

			card.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					markPlayerCardSelected(finalIndex);
				}
			});
		}
	}

	private void init(boolean first) {
		initGraphics();
		Log.i("INIT", "Game init graphics");
		if (first) {
			Log.i("INIT", "Game init core");
			initGameCore();
		}
	}

	private void initGraphics() {
		ivDeckOpen = (ImageView) findViewById(R.id.ivDeckOpen);
		ivDeckClosed = (ImageView) findViewById(R.id.ivDeck);

		// Player's cards
		ivPlayerCard1 = (ImageView) findViewById(R.id.ivcard0_1);
		ivPlayerCard2 = (ImageView) findViewById(R.id.ivcard0_2);
		ivPlayerCard3 = (ImageView) findViewById(R.id.ivcard0_3);
		ivPlayerCard4 = (ImageView) findViewById(R.id.ivcard0_4);
		ivPlayerCard5 = (ImageView) findViewById(R.id.ivcard0_5);
		ivPlayerCard6 = (ImageView) findViewById(R.id.ivcard0_6);
		ivPlayerCard7 = (ImageView) findViewById(R.id.ivcard0_7);
		ivPlayerCard8 = (ImageView) findViewById(R.id.ivcard0_8);
		ivPlayerCard9 = (ImageView) findViewById(R.id.ivcard0_9);
		ivPlayerCard10 = (ImageView) findViewById(R.id.ivcard0_10);
		ivPlayerCard11 = (ImageView) findViewById(R.id.ivcard0_11);
		ivPlayerCard12 = (ImageView) findViewById(R.id.ivcard0_12);
		ivPlayerCard13 = (ImageView) findViewById(R.id.ivcard0_13);
		ivPlayerCard14 = (ImageView) findViewById(R.id.ivcard0_14);
		playerCards = new ImageView[] {ivPlayerCard1, ivPlayerCard2, ivPlayerCard3, ivPlayerCard4, ivPlayerCard5, ivPlayerCard6,ivPlayerCard7, ivPlayerCard8, ivPlayerCard9, ivPlayerCard10,ivPlayerCard11, ivPlayerCard12, ivPlayerCard13,ivPlayerCard14};
		
		// Playground
		llPlayGround = (LinearLayout) findViewById(R.id.llplayground);
		
		//Deck
		llCardDeck = (LinearLayout) findViewById(R.id.llCardDeck);
	}

	private void initGameCore() {
		deck = new Deck();
		playedSets = new ArrayList<PlayedSet>();

		// Create array with players and their hand
		playersInOrder = new ArrayList<Hand>();
		playerHand = new Hand(playerCards, "Player name");
		playersInOrder.add(playerHand);
		oppHand = new Hand(null, "Opponent");
		playersInOrder.add(oppHand);

		// Push all data to gamedata class
		gameData.init(playerHand, oppHand, playersInOrder, deck, playedSets);
		gameData.setGameInProgress(true);

		// Deal cards to players
		dealCards();
	}

	// ///////////// ////////////////////// ////////////////////
	// //////////// END OF STANDARD CHECKS /////////////////////
	// /////////// //////////////////////// ///////////////////

	private void dealCards() {
		for (int c = 0; c < 13; c++) {
			for (Hand hand : playersInOrder) {
				PlayingCard card = gameData.getDeck().getNextCard();
				hand.addCard(card);
			}
		}

		//Draw the hand
		redrawHand();
		
		// Open top deck card (aka, peek)
		ivDeckOpen.setImageResource(gameData.getDeck().openTopCard().getImageResourceId());
		ivDeckOpen.setVisibility(View.VISIBLE);
	}

	// Mark card selected/unselected when user click it
	private void markPlayerCardSelected(int cardIndex) {
		Log.i("MARK", "Card "+cardIndex+" gets marked");
		
		ImageView card = playerCards[cardIndex];
		boolean selected = gameData.getPlayerHand().isCardSelected(cardIndex);

		// Convert DP to PX for margin
		float density = getApplicationContext().getResources().getDisplayMetrics().density;
		int leftmargin = (card.getId() != R.id.ivcard0_1) ? -Math.round((float) 25 * density) : 0;

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(card.getLayoutParams().width, card.getLayoutParams().height, 1);
		params.gravity = !selected ? Gravity.TOP : Gravity.BOTTOM;
		params.setMargins(leftmargin, 0, 0, 0);
		card.setLayoutParams(params);

		gameData.getPlayerHand().changeCardSelectedState(cardIndex);
	}

	// Grab a card from the open stack
	private void grabOpenDeckCard() {
		ivPlayerCard14.setVisibility(View.VISIBLE);
		gameData.setGrabbedCard(true);
		gameData.setPlayerCanThrow(true);

		PlayingCard card = gameData.getDeck().getTopThrownCard();

		if (gameData.getDeck().getThrownDeckSize() < 2) {
			ivDeckOpen.setVisibility(View.INVISIBLE);
		} else {
			ivDeckOpen.setVisibility(View.VISIBLE);
		}

		gameData.getPlayerHand().addCard(card);
		redrawHand();
	}

	// Grab a card from the open stack
	private void grabClosedDeckCard() {
		ivPlayerCard14.setVisibility(View.VISIBLE);
		gameData.setGrabbedCard(true);
		gameData.setPlayerCanThrow(true);

		PlayingCard card = gameData.getDeck().getNextCard();

		gameData.getPlayerHand().addCard(card);
		redrawHand();
	}
	
	//Throw card to deck
	private void throwCardToDeck(){
		PlayingCard thrownCard = gameData.getPlayerHand().throwSelectedCardToDeck();
		if(thrownCard != null){
			gameData.getDeck().addThrownCardToDeck(thrownCard);
			gameData.setPlayerCanThrow(false);
			gameData.setGrabbedCard(false);
			redrawHand();
			redrawDeck();
		}
		else{
			Toast.makeText(	getApplicationContext(),getResources().getString(R.string.throwOneCardError),Toast.LENGTH_SHORT).show();
		}
	}
	
	// Create new play set
	private void createNewPlaySet(){
		ArrayList<PlayingCard> setcards = gameData.getPlayerHand().dropSelectedCards();
		PlayedSet newSet = new PlayedSet();
		for(PlayingCard card : setcards){
			newSet.addCardToSet(card);
		}
		gameData.createNewPlaySet(newSet);
		gameData.setGrabbedCard(false);
		redrawHand();
		redrawPlayGround();
	}
	
	// ///////////// ////////////////////// ////////////
	// //////////// REDRAW METHODS /////////////////////
	// /////////// //////////////////////// ////////////
	private void redrawHand(){
		Log.i("REDRAW", "Redraw hand");
		
		Hand hand = gameData.getPlayerHand();
		for (int index = 0; index < 14; index++) {
			PlayingCard card = hand.getCardByPosition(index);
			ImageView image = playerCards[index];
			if (card != null) {
				// Show new card
				playerCards[index].setVisibility(View.VISIBLE);
				playerCards[index].setImageResource(card.getImageResourceId());

				// Convert DP to PX for margin
				float density = getApplicationContext().getResources().getDisplayMetrics().density;
				int leftmargin = (image.getId() != R.id.ivcard0_1) ? -Math.round((float) 25 * density) : 0;

				// Make sure that the card is down again
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(image.getLayoutParams().width,	image.getLayoutParams().height, 1);
				params.gravity = Gravity.BOTTOM;
				params.setMargins(leftmargin, 0, 0, 0);
				image.setLayoutParams(params);
			} else {
				// Hide because card is empty
				playerCards[index].setVisibility(View.INVISIBLE);
			}
		}
	}

	private void redrawPlayGround(){
		//TODO
		Log.i("REDRAW", "Redraw playground");
	}
	
	protected void redrawDeck() {
		// TODO
		Log.i("REDRAW", "Redraw deck");
		ivDeckOpen.setImageResource(gameData.getDeck().peekThrownCards().getImageResourceId());
		ivDeckOpen.setVisibility(View.VISIBLE);
	}
}
