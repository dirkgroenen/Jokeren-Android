package nl.dirkgroenen.jokeren;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainScreen extends Activity {

	Button bStartGame,bResumeGame;
	GameData gameData;
	SaveHandler savehandler; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainscreen);
		
		bStartGame = (Button) findViewById(R.id.bStartGame);
		bResumeGame = (Button) findViewById(R.id.bResumeGame);
		
		savehandler = SaveHandler.getInstance(this);
		if(savehandler.checkSaveExists()){
			bResumeGame.setVisibility(View.VISIBLE);
		}
		
		bStartGame.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				startGame();
			}
		});
		bResumeGame.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				resumeGame();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mainscreen, menu);
		return true;
	}

	
	protected void startGame(){
		Log.i("MAIN", "Start new game");
		gameData = GameData.getInstance();
		savehandler.save(gameData);
		//Create new intent for game
		Intent intent = new Intent(MainScreen.this, Game.class);
		intent.putExtra(Game.GAME_STATE, Game.STATES.start);
		intent.putExtra(Game.GAME_DATA, savehandler.readLastState());
		startActivityForResult(intent, Game.START_CODE);
	}

	protected void resumeGame(){
		Log.i("MAIN", "Resume game");
		//Create new intent for game
		Intent intent = new Intent(MainScreen.this, Game.class);
		intent.putExtra(Game.GAME_STATE, Game.STATES.resume);
		intent.putExtra(Game.GAME_DATA, savehandler.readLastState());
		startActivityForResult(intent, Game.RESUME_CODE);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// TODO NEEDS TO BE DONE
		Log.i("RESULT","Got something back to mainscreen");
		gameData = (GameData) data.getExtras().getSerializable(Game.GAME_DATA);
		
		Log.d("PUKI", "gameData.isGameInProgress() = " + gameData.isGameInProgress());
		Log.d("PUKI", "GameData.getInstance().isGameInProgress() = " + GameData.getInstance().isGameInProgress());		
		
		switch (requestCode) {
        case Game.START_CODE:
        	System.out.println("returned from a new game");
        	bResumeGame.setVisibility(View.VISIBLE);
            break;
        case Game.RESUME_CODE:
            System.out.println("returned from a resumed game");
            bResumeGame.setVisibility(View.VISIBLE);
            break;
        default:
        	break;
		}
	}
}
