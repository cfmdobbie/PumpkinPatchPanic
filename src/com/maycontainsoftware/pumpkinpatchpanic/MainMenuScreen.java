package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * The main menu screen of the game.
 * 
 * @author Charlie
 */
public class MainMenuScreen extends PumpkinScreen {

	/** Whether debug output should be logged. */
	private static final boolean DEBUG = false;

	/** Tag, for logging purposes. */
	private static final String TAG = MainMenuScreen.class.getSimpleName();

	/**
	 * Construct a new MainMenuScreen object.
	 * 
	 * @param game
	 *            The Game instance.
	 */
	public MainMenuScreen(final PumpkinGame game) {
		super(game);
	}

	@Override
	public void show() {
		if (DEBUG) {
			Gdx.app.log(TAG, "show()");
		}

		// Call superclass to create the Stage
		super.show();

		// The only custom parts of the main menu are the three buttons for Help, Play and Settings
		// Note that all pumpkin-buttons are 230x150px

		// The "Help" pumpkin-button
		final Button btnHelp = new PumpkinButton(atlas, "help", game);
		btnHelp.setPosition(1280 / 2 - 300 - 170 / 2, 150);
		btnHelp.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				game.setScreen(new HelpScreen(game));
				MainMenuScreen.this.dispose();
			}
		});
		stage.addActor(btnHelp);

		// The "Play" pumpkin-button
		final Button btnPlay = new PumpkinButton(atlas, "play", game);
		btnPlay.setPosition(1280 / 2 - 170 / 2, 95);
		btnPlay.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				game.setScreen(new GameScreen(game));
				MainMenuScreen.this.dispose();
			}
		});
		stage.addActor(btnPlay);

		// The "Settings" pumpkin-button
		final Button btnSettings = new PumpkinButton(atlas, "cog", game);
		btnSettings.setPosition(1280 / 2 + 300 - 170 / 2, 150);
		btnSettings.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				game.setScreen(new SettingsScreen(game));
				MainMenuScreen.this.dispose();
			}
		});
		stage.addActor(btnSettings);

		// And the logo!
		final Image logo = new Image(atlas.findRegion("logo"));
		logo.setPosition(1280 / 2 - logo.getWidth() / 2, 720 - 422);
		logo.setTouchable(Touchable.disabled);
		stage.addActor(logo);

		// HUD to display highest round beaten
		stage.addActor(new MenuHud(game));

		// Start or stop music as required
		game.updateMusic();

		// Update sound settings as required
		game.updateSound();

		// Don't catch back button
		Gdx.input.setCatchBackKey(false);

		game.currentScreenCallback.notifyScreenVisible(ICurrentScreenCallback.Screen.MAIN_MENU);
	}
}
