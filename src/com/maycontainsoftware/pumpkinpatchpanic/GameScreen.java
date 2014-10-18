package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.Gdx;

/**
 * Game screen.
 * 
 * @author Charlie
 */
public class GameScreen extends PumpkinScreen {

	/** Whether debug output should be logged. */
	private static final boolean DEBUG = false;

	/** Tag, for logging purposes. */
	private static final String TAG = GameScreen.class.getSimpleName();

	/**
	 * Construct a new GameScreen.
	 * 
	 * @param game
	 *            The Game instance.
	 */
	public GameScreen(final PumpkinGame game) {
		super(game);
	}

	@Override
	public void show() {
		if (DEBUG) {
			Gdx.app.log(TAG, "show()");
		}

		super.show();
		
		
		// TODO: Create game elements 

		game.currentScreenCallback.notifyScreenVisible(ICurrentScreenCallback.Screen.GAME);
	}
}
