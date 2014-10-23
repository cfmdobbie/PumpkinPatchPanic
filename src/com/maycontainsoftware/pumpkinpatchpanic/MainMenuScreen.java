package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

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
		final Button btnHelp = new Button(new TextureRegionDrawable(atlas.findRegion("btn_help")));
		btnHelp.setPosition(1280 / 2 - 300 - 230 / 2, 150);
		btnHelp.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				game.setScreen(new HelpScreen(game));
				MainMenuScreen.this.dispose();
			}
		});
		stage.addActor(getPlantForPumpkinButton(btnHelp));
		stage.addActor(btnHelp);

		// The "Play" pumpkin-button
		final Button btnPlay = new Button(new TextureRegionDrawable(atlas.findRegion("btn_play")));
		btnPlay.setPosition(1280 / 2 - 230 / 2, 95);
		btnPlay.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				game.setScreen(new GameScreen(game));
				MainMenuScreen.this.dispose();
			}
		});
		stage.addActor(getPlantForPumpkinButton(btnPlay));
		stage.addActor(btnPlay);

		// The "Settings" pumpkin-button
		final Button btnSettings = new Button(new TextureRegionDrawable(atlas.findRegion("btn_settings")));
		btnSettings.setPosition(1280 / 2 + 300 - 230 / 2, 150);
		btnSettings.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				game.setScreen(new SettingsScreen(game));
				MainMenuScreen.this.dispose();
			}
		});
		stage.addActor(getPlantForPumpkinButton(btnSettings));
		stage.addActor(btnSettings);

		// HUD to display highest round beaten
		stage.addActor(new MenuHud(game));

		// Prepare music if we have not already done so
		if (game.music == null) {
			game.prepareMusic();
		}

		// Start music if required
		if (game.isMusicEnabled()) {
			if (!game.music.isPlaying()) {
				game.music.play();
			}
		} else {
			// Shouldn't be necessary to check this, but safe to do so
			if (game.music.isPlaying()) {
				game.music.stop();
			}
		}

		game.currentScreenCallback.notifyScreenVisible(ICurrentScreenCallback.Screen.MAIN_MENU);
	}
}
