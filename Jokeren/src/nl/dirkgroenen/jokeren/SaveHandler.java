package nl.dirkgroenen.jokeren;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import android.app.Activity;
import android.content.Context;

public class SaveHandler {

	private static final String SAVE_FILENAME = "jokerengame.ser";

	private GameData saveData;

	private static SaveHandler saveService;
	private Activity context;

	private FileOutputStream fOut = null;
	private ObjectOutputStream osw = null;

	private FileInputStream fin = null;
	private ObjectInputStream sin = null;

	public SaveHandler(Context context) {
		this.context = (Activity) context;
	}
	
	public static SaveHandler getInstance(Context context) {
		if (saveService == null) {
			saveService = new SaveHandler(context);
		}
		return saveService;
	}

	public boolean checkSaveExists(){
		File file = context.getFileStreamPath(SAVE_FILENAME);
		return file.exists();
	}
	
	public void save(GameData object) {
		try {
			fOut = context.openFileOutput(SAVE_FILENAME, Activity.MODE_PRIVATE);
			osw = new ObjectOutputStream(fOut);
			osw.writeObject(object);
			osw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public GameData readLastState() {
		try {
			fin = context.openFileInput(SAVE_FILENAME);
			sin = new ObjectInputStream(fin);
			saveData = (GameData) sin.readObject();
			sin.close();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return saveData;
	}
}
