package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * Settings/options screen.
 * 
 * @author Charlie
 */
public class SettingsScreen extends PumpkinScreen {

	/** Whether debug output should be logged. */
	private static final boolean DEBUG = false;

	/** Tag, for logging purposes. */
	private static final String TAG = SettingsScreen.class.getSimpleName();

	/** Reference to HUD, needed for resetting high score. */
	private MenuHud menuHud;

	/**
	 * Construct a new SettingsScreen.
	 * 
	 * @param game
	 *            The Game instance.
	 */
	public SettingsScreen(final PumpkinGame game) {
		super(game);
	}

	@Override
	public void show() {
		if (DEBUG) {
			Gdx.app.log(TAG, "show()");
		}

		super.show();

		// The "Menu" pumpkin-button
		final Button menuBtn = new PumpkinButton(atlas, "menu");
		menuBtn.setPosition(1280 / 2 - 290 - 170 / 2, 190);
		menuBtn.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				game.setScreen(new MainMenuScreen(game));
				SettingsScreen.this.dispose();
			}
		});
		stage.addActor(menuBtn);

		// The "Reset High Score" pumpkin-button
		final Button resetScoreBtn = new PumpkinButton(atlas, "reset_score");
		resetScoreBtn.setPosition(1280 / 2 + 95 - 170 / 2, 190);
		resetScoreBtn.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				// Reset highest score to zero, update HUD, apply an interesting value-change effect
				game.setHighestRound(0);
				menuHud.updateHighestRound();
				menuHud.highestRoundLabel.setColor(Color.RED);
				menuHud.highestRoundLabel.addAction(Actions.color(Color.WHITE, 1.0f));
			}
		});
		stage.addActor(resetScoreBtn);

		// The "Sound On/Off" pumpkin-button
		final PumpkinButton.TogglePumpkinButton soundBtn = new PumpkinButton.TogglePumpkinButton(atlas, "sound",
				"no_sound");
		soundBtn.setPosition(1280 / 2 - 95 - 170 / 2, 60);
		soundBtn.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				game.setSoundEnabled(!game.soundEnabled);
				if (game.soundEnabled) {
					soundBtn.on();
				} else {
					soundBtn.off();
				}
			}
		});
		// Button is on by default, but should it be off?
		if (!game.soundEnabled) {
			soundBtn.off();
		}
		stage.addActor(soundBtn);

		// The "Music On/Off" pumpkin-button
		final PumpkinButton.TogglePumpkinButton musicBtn = new PumpkinButton.TogglePumpkinButton(atlas, "music",
				"no_music");
		musicBtn.setPosition(1280 / 2 + 290 - 170 / 2, 60);
		musicBtn.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				if (game.isMusicEnabled()) {
					game.setMusicEnabled(false);
					musicBtn.off();
				} else {
					game.setMusicEnabled(true);
					musicBtn.on();
				}
				game.updateMusic();
			}
		});
		// Button is on by default, but should it be off?
		if (!game.isMusicEnabled()) {
			musicBtn.off();
		}
		stage.addActor(musicBtn);

		// HUD to display highest round beaten
		stage.addActor(menuHud = new MenuHud(game));

		game.currentScreenCallback.notifyScreenVisible(ICurrentScreenCallback.Screen.SETTINGS);
	}
}
