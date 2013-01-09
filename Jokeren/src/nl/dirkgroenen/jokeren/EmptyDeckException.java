package nl.dirkgroenen.jokeren;

import android.util.Log;

public class EmptyDeckException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmptyDeckException(){
		Log.w("DECK","There are no more cards left");
	}
}
