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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainscreen);
		
		bStartGame = (Button) findViewById(R.id.bStartGame);
		bResumeGame = (Button) findViewById(R.id.bResumeGame);
		
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
		GameData gameData = GameData.getInstance();
		//Create new intent for game
		Intent intent = new Intent(MainScreen.this, Game.class);
		intent.putExtra(Game.GAME_STATE, Game.STATES.start);
		intent.putExtra(Game.GAME_DATA, gameData);
		startActivityForResult(intent, Game.START_CODE);
	}
	
	protected void resumeGame(){
		Log.i("MAIN", "Resume game");
		//Create new intent for game
		Intent intent = new Intent(MainScreen.this, Game.class);
		intent.putExtra(Game.GAME_STATE, Game.STATES.resume);
		startActivityForResult(intent, Game.RESUME_CODE);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// TODO NEEDS TO BE DONE
	}
}
