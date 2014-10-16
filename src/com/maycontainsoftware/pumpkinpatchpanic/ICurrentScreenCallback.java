package com.maycontainsoftware.pumpkinpatchpanic;

/**
 * Interface for notifying the host application of the current state of the game, specifically what screen is current
 * displayed. The intended use for this is hiding/showing advertising panels as required on mobile platforms.
 * 
 * @author Charlie
 */
public interface ICurrentScreenCallback {
	/**
	 * An enumeration of all the known screens.
	 * 
	 * @author Charlie
	 */
	public static enum Screen {
		LOADING,
		MAIN_MENU,
		HELP,
		SETTINGS,
		GAME,
	};

	/**
	 * Notify the callback of the currently-visible screen. This should be called whenever the screen changes.
	 * 
	 * @param screen
	 *            The screen that is current visible.
	 */
	public void notifyScreenVisible(Screen screen);
}
