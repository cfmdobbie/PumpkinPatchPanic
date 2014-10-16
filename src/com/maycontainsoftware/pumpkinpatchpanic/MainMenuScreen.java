package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
		final Image btnPlay = new Image(atlas.findRegion("btn_play"));
		btnPlay.setPosition(1280 / 2 - 230 / 2, 95);
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

		// XXX: Test pumpkin code
		// final TextureAtlas pumpkins = game.manager.get("pumpkins.atlas", TextureAtlas.class);
		// Base pumpkin
		// final Image pumpkin = new Image(pumpkins.findRegion("pumpkin"));
		// stage.addActor(pumpkin);
		// Pumpkin face
		// final Image lou = new Image(pumpkins.findRegion("lou"));
		// Some effects can be gained by changing batch color
		// However, better effects by changing levels on each graphic in The Gimp
		// How many frames would be required is to be decided, might influence decision
		// lou.setColor(1.0f, 0.5f, 0.5f, 0.5f);
		// stage.addActor(lou);

		// These pumpkin graphics are "workable", but don't fit with a pumpkin on the vine nor are easily sized to show
		// growth. So, either need lots more graphical work, or change idea behind game slightly to accommodate.

		// Face graphics can all be dropped to 168x124 or so, which is a significant saving on texture memory at the
		// cost of some more involved positioning. Ultimately want to animate pumpkin and face together (shake, rotate),
		// which may influence decision.

		game.currentScreenCallback.notifyScreenVisible(ICurrentScreenCallback.Screen.MAIN_MENU);
	}
}
