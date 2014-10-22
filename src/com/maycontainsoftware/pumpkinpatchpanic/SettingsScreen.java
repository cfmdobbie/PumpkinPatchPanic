package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

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
		final Button menuBtn = new Button(new TextureRegionDrawable(atlas.findRegion("btn_menu")));
		menuBtn.setPosition(1280 / 2 - 290 - 230 / 2, 190);
		menuBtn.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				game.setScreen(new MainMenuScreen(game));
				SettingsScreen.this.dispose();
			}
		});
		stage.addActor(getPlantForPumpkinButton(menuBtn));
		stage.addActor(menuBtn);

		// The "Reset High Score" pumpkin-button
		final Button resetScoreBtn = new Button(new TextureRegionDrawable(atlas.findRegion("btn_reset_score")));
		resetScoreBtn.setPosition(1280 / 2 + 95 - 230 / 2, 190);
		resetScoreBtn.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
			}
		});
		stage.addActor(getPlantForPumpkinButton(resetScoreBtn));
		stage.addActor(resetScoreBtn);

		// The "Sound On/Off" pumpkin-button
		final Button soundBtn = new Button(new TextureRegionDrawable(atlas.findRegion("btn_sound_off")), null,
				new TextureRegionDrawable(atlas.findRegion("btn_sound_on")));
		soundBtn.setPosition(1280 / 2 - 95 - 230 / 2, 60);
		soundBtn.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
			}
		});
		stage.addActor(getPlantForPumpkinButton(soundBtn));
		stage.addActor(soundBtn);

		// The "Music On/Off" pumpkin-button
		final Button musicBtn = new Button(new TextureRegionDrawable(atlas.findRegion("btn_music_off")), null,
				new TextureRegionDrawable(atlas.findRegion("btn_music_on")));
		musicBtn.setPosition(1280 / 2 + 290 - 230 / 2, 60);
		musicBtn.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
			}
		});
		stage.addActor(getPlantForPumpkinButton(musicBtn));
		stage.addActor(musicBtn);

		// HUD to display highest round beaten
		stage.addActor(new MenuHud(game));

		game.currentScreenCallback.notifyScreenVisible(ICurrentScreenCallback.Screen.SETTINGS);
	}
}
