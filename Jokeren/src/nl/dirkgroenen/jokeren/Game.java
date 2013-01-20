package nl.dirkgroenen.jokeren;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Game extends Activity implements OnTouchListener{

	private Deck deck;
	private GameData gameData;
	Hand playerHand, oppHand;
	private ImageView ivDeckClosed, ivDeckOpen, ivPlayerCard1, ivPlayerCard2,ivPlayerCard3, ivPlayerCard4, ivPlayerCard5, ivPlayerCard6,ivPlayerCard7, ivPlayerCard8, ivPlayerCard9, ivPlayerCard10,ivPlayerCard11, ivPlayerCard12, ivPlayerCard13, ivPlayerCard14;
	private ImageView[] playerCards;
	private TextView tvOpp1;
	private ArrayList<Hand> playersInOrder;
	private LinearLayout llPlayGround,llPlayGroundRow1,llPlayGroundRow2,llCardDeck;
	private ArrayList<PlayedSet> playedSets;
	public static final String SAVE_FILENAME = "jokerensave.ser";
	private SaveHandler savehandler;
	private Hand currentHand;
	private int defaultStartingPlayer = 0;
	
	public static enum STATES {
		start, resume, end
	};

	public static final int START_CODE = 0;
	public static final int RESUME_CODE = 1;
	public static final String GAME_STATE = "STATE";
	public static final String GAME_DATA = "GameData";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("PUKI","onCreate");
		
		// Get save game
		savehandler = SaveHandler.getInstance(this);
		gameData = savehandler.readLastState();

		setContentView(R.layout.gamescreen);

		// Load which state was given by the mainscreen
		switch ((STATES) getIntent().getExtras().get(GAME_STATE)) {
		case start:
			gameData.setFirstRun(true);
			Log.i("ONCREATE", "Received state: start");
			break;
		case resume:
			gameData.setFirstRun(false);
			Log.i("ONCREATE", "Received state: resume");
			break;
		default:
			gameData.setFirstRun(true);
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
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d("PUKI","onStart");
		Log.i("FIRSTRUN", "Firstrun = "+gameData.getFirstRun());
		
		init(gameData.getFirstRun());

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
				if(gameData.getPlayerHand().countSelectedCards() == 0){
					// Do nothing
				}
				if(gameData.getPlayerHand().countSelectedCards() < 3){
					Toast.makeText(	getApplicationContext(),getResources().getString(R.string.emptyNewSetError),Toast.LENGTH_SHORT).show();
				}
				else if(!gameData.getGrabbedCard()){
					Toast.makeText(	getApplicationContext(),getResources().getString(R.string.grabCardFirstError),Toast.LENGTH_SHORT).show();
				}
				else if((gameData.getPlayerHand().getCardsCount()-gameData.getPlayerHand().countSelectedCards()) < 1){
					Toast.makeText(	getApplicationContext(),getResources().getString(R.string.noCardsLeftError),Toast.LENGTH_SHORT).show();
				}
				else{
					createNewPlaySet();
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
			
			card.setOnTouchListener(this);
		}
		
		gameData.getTurn().addOnTurnEndedListener(new Turn.OnTurnEndedListener<Hand>() {
			@Override
			public void onTurnEnded(Hand hand) {
				turnEndedHandler(hand);
			}
		});
	}

	private void init(boolean first) {
		initGraphics(first);
		Log.i("INIT", "Game init graphics");
		if (first) {
			Log.i("INIT", "Game init core");
			initGameCore();
		}
	}

	private void initGraphics(boolean first) {
		ivDeckOpen = (ImageView) findViewById(R.id.ivDeckOpen);
		ivDeckClosed = (ImageView) findViewById(R.id.ivDeck);

		// Opponents textview
		tvOpp1 = (TextView) findViewById(R.id.tvOpp1);
		
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
		llPlayGroundRow1 = (LinearLayout) findViewById(R.id.llplaygroundRow1);
		llPlayGroundRow2 = (LinearLayout) findViewById(R.id.llplaygroundRow2);
		
		//Deck
		llCardDeck = (LinearLayout) findViewById(R.id.llCardDeck);
		
		if(!first){
			redrawHand();
			redrawDeck();
			redrawPlayGround();
		}
	}

	private void initGameCore() {
		deck = new Deck();
		playedSets = new ArrayList<PlayedSet>();

		// Create array with players and their hand
		playersInOrder = new ArrayList<Hand>();
		playerHand = new PlayerHand(playerCards, "Player name");
		playersInOrder.add(playerHand);
		oppHand = new OppHand(new GameStrategy(), null, "Opponent");
		playersInOrder.add(oppHand);

		// Push all data to gamedata class
		gameData.init(playerHand, oppHand, playersInOrder, deck, playedSets, new Turn<Hand>(playersInOrder,defaultStartingPlayer));
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
		int leftmargin = (card.getId() != R.id.ivcard0_1) ? -convertDpToXp(25) : 0;

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
		redrawDeck();
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
		currentHand = gameData.getTurn().peek();
		
		if(currentHand.isAwaitingInput()){
			PlayingCard thrownCard = gameData.getPlayerHand().throwSelectedCardToDeck();
			if(thrownCard != null){
				gameData.getDeck().addThrownCardToDeck(thrownCard);
				gameData.setPlayerCanThrow(false);
				gameData.setGrabbedCard(false);
				redrawHand();
				redrawDeck();
				gameData.getTurn().next();
			}
			else{
				Toast.makeText(	getApplicationContext(),getResources().getString(R.string.throwOneCardError),Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	// Create new play set
	private void createNewPlaySet(){
		currentHand = gameData.getTurn().peek();
		
		if(currentHand.isAwaitingInput()){
			ArrayList<PlayingCard> setcards;
			try {
				setcards = gameData.getPlayerHand().dropSelectedCards();
				
				PlayedSet newSet = new PlayedSet();
				for(PlayingCard card : setcards){
					newSet.addCardToSet(card);
				}
				gameData.createNewPlaySet(newSet);
				redrawHand();
				redrawPlayGround();
				
			} catch (InvalidDropException e) {
				// TODO SHOW DIALOG
				Toast.makeText(	getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
			}
			
		}
	}
	
	// Add cards to played set
	protected void changePlayedSet(int set) {
		currentHand = gameData.getTurn().peek();
		
		if(currentHand.isAwaitingInput()){
			if((gameData.getPlayerHand().getCardsCount()-gameData.getPlayerHand().countSelectedCards()) == 0){
				Toast.makeText(	getApplicationContext(),getResources().getString(R.string.noCardsLeftError),Toast.LENGTH_SHORT).show();
			}
			else{
				ArrayList<PlayingCard> setcards;
				try {					
					ArrayList<PlayedSet> playedSets = gameData.getAllPlayedSets();
					PlayedSet newSet = playedSets.get(set);
					setcards = gameData.getPlayerHand().dropSelectedCardsToExisting(newSet);
					for(PlayingCard card : setcards){
						newSet.addCardToSet(card);
					}
					
					redrawHand();
					redrawPlayGround();
				} catch (InvalidDropException e) {
					// TODO SHOW DIALOG
					Toast.makeText(	getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
	
	private void orderPlayedSets() {
		ArrayList<PlayedSet> playedSets = gameData.getAllPlayedSets();
		for(PlayedSet set : playedSets){
			Collections.sort(set.getAllCards());
		}
	}
	
	//TODO
	protected void turnEndedHandler(final Hand hand) {
		if(hand.isAwaitingInput()){
			// This means the turn is for a human player, so do nothing.
			Log.i("TURN", "The turn is for the human player: "+hand.getPlayerName());
		}
		else{
			// This means the turn is for a AI. Decide!
			Log.i("TURN", "The turn is for the AI player: "+hand.getPlayerName());
			gameData.getTurn().next();
			
			// Update players hand size for human player
			this.updateOppScore();
		}
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

				// Convert DP to XP				
				int leftmargin = (image.getId() != R.id.ivcard0_1) ? -convertDpToXp(25) : 0;

				// Make sure that the card is down or up again
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(image.getLayoutParams().width,	image.getLayoutParams().height, 1);
				params.gravity = (card.getSelected()) ? Gravity.TOP : Gravity.BOTTOM;
				params.setMargins(leftmargin, 0, 0, 0);
				image.setLayoutParams(params);
				
			} else {
				// Hide because card is empty
				playerCards[index].setVisibility(View.INVISIBLE);
			}
		}
		
		// Count the opponents cards and place in textview
		tvOpp1.setText(getApplicationContext().getResources().getString(R.string.opp1)+": "+Integer.toString(gameData.getOppHand().getCardsCount()));
	}

	private void redrawPlayGround(){
		//TODO: Check on which playground row the sets need to be layed down
		Log.i("REDRAW", "Redraw playground");
		
		orderPlayedSets();
		ArrayList<PlayedSet> playedSets = gameData.getAllPlayedSets();
		
		
		if(((LinearLayout) llPlayGroundRow1).getChildCount() > 0){
			((LinearLayout) llPlayGroundRow1).removeAllViews();
		}
		if(((LinearLayout) llPlayGroundRow2).getChildCount() > 0){
			((LinearLayout) llPlayGroundRow2).removeAllViews();
		}
		
		for(int k = 0; k < playedSets.size();k++){
			final int currentLL = k;
			
			LinearLayout ll = new LinearLayout(getApplicationContext());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, convertDpToXp(70));
			params.setMargins(convertDpToXp(5), convertDpToXp(5), convertDpToXp(5), convertDpToXp(5));
			ll.setLayoutParams(params);
			for(int i = 0; i < playedSets.get(k).getAllCards().size(); i++){
				ImageView image = new ImageView(getApplicationContext());
				
				LinearLayout.LayoutParams imageparams = new LinearLayout.LayoutParams(convertDpToXp(50),LayoutParams.WRAP_CONTENT, 1);
				imageparams.gravity = Gravity.BOTTOM;
				if(i != 0) imageparams.setMargins(-convertDpToXp(35), 0,0,0);
				image.setLayoutParams(imageparams);
				
				image.setImageResource(playedSets.get(k).getAllCards().get(i).getImageResourceId());
				ll.addView(image);
			}
			llPlayGroundRow1.addView(ll);
			ll.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					changePlayedSet(currentLL);
				}
			});
		}
	}

	protected void redrawDeck() {
		Log.i("REDRAW", "Redraw deck");
		Deck deck = gameData.getDeck();
		
		if(deck.getThrownDeckSize() != 0){
			ivDeckOpen.setImageResource(gameData.getDeck().peekThrownCards().getImageResourceId());
			ivDeckOpen.setVisibility(View.VISIBLE);
		}
	}
	
	private int convertDpToXp(int DP){
		// Convert DP to PX for margin
		float density = getApplicationContext().getResources().getDisplayMetrics().density;
		return Math.round((float) DP * density);
	}

	
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		// TODO TOUCH drag and drop for cards
		int eventAction = event.getAction();
		
		switch(eventAction){
		case MotionEvent.ACTION_DOWN: 
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP: 
			break;
		}
		
		return false;
	}
	
	///////////////// //////////////// /////////////
	//////////////// On [..] Handlers /////////////
	/////////////// ///////////////// ////////////
	
	@Override
	protected void onPause() {
		// TODO SAVE INSTANCE
		super.onPause();
		Log.d("PUKI","onPause");
		savehandler.save(gameData);
	}

	@Override
	protected void onResume() {
		// TODO RESTORE GAME
		super.onResume();
		Log.d("PUKI","onResume");
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.d("PUKI","onRestart");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d("PUKI","onStop");
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("PUKI","onDestroy");
		
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
	}
	
	/////////// //////////// ///////////////
	/////////// EDIT SCORE ////////////////
	////////// /////////// ///////////////
	
	public void updateOppScore(){
		tvOpp1.setText(getApplicationContext().getResources().getString(R.string.opp1)+": "+Integer.toString(gameData.getOppHand().getCardsCount()));
	}
}
