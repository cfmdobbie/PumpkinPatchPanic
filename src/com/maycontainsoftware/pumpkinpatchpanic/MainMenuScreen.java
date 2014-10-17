package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
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
	
	private Group mainGroup;
	private Group helpGroup;
	private Group settingsGroup;
	
	/** The Drawables that make up the help panels. */
	private Drawable[] helpPanels;

	/** The index of the current help panel. */
	private int helpIndex;

	/** The Image widget that displays the current help panel. */
	private Image helpPanel;
	
	private static final float FADE_TIME = 0.25f;

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
		
		// Create the Main group
		mainGroup = makeMainGroup();
		stage.addActor(mainGroup);
		
		// Create the Settings group
		settingsGroup = makeSettingsGroup();
		// Starts off invisible
		settingsGroup.setColor(1.0f, 1.0f, 1.0f, 0.0f);
		settingsGroup.setVisible(false);
		stage.addActor(settingsGroup);
		
		// Create the Help group
		helpGroup = makeHelpGroup();
		// Starts off invisible
		helpGroup.setColor(1.0f, 1.0f, 1.0f, 0.0f);
		helpGroup.setVisible(false);
		stage.addActor(helpGroup);
		
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
	
	private Group makeMainGroup() {
		final Group g = new Group();
		
		// The "Help" pumpkin-button
		final Button btnHelp = new Button(new TextureRegionDrawable(atlas.findRegion("btn_help")));
		btnHelp.setPosition(1280 / 2 - 300 - 230 / 2, 150);
		btnHelp.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				helpIndex = 0;
				updateHelpPanel();
				Gdx.input.setInputProcessor(null);
				mainGroup.addAction(Actions.sequence(Actions.color(Color.CLEAR, FADE_TIME), Actions.visible(false)));
				helpGroup.addAction(Actions.sequence(Actions.delay(FADE_TIME), Actions.visible(true), Actions.color(Color.WHITE, FADE_TIME), new SetInputProcessorAction(stage)));
			}
		});
		g.addActor(getPlantForPumpkinButton(btnHelp));
		g.addActor(btnHelp);

		// The "Play" pumpkin-button
		final Button btnPlay = new Button(new TextureRegionDrawable(atlas.findRegion("btn_play")));
		btnPlay.setPosition(1280 / 2 - 230 / 2, 95);
		btnPlay.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.input.setInputProcessor(null);
				g.addAction(Actions.sequence(Actions.fadeOut(0.125f)));
			}
		});
		g.addActor(getPlantForPumpkinButton(btnPlay));
		g.addActor(btnPlay);

		// The "Settings" pumpkin-button
		final Button btnSettings = new Button(new TextureRegionDrawable(atlas.findRegion("btn_settings")));
		btnSettings.setPosition(1280 / 2 + 300 - 230 / 2, 150);
		btnSettings.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				Gdx.input.setInputProcessor(null);
				mainGroup.addAction(Actions.sequence(Actions.color(Color.CLEAR, FADE_TIME), Actions.visible(false)));
				settingsGroup.addAction(Actions.sequence(Actions.delay(FADE_TIME), Actions.visible(true), Actions.color(Color.WHITE, FADE_TIME), new SetInputProcessorAction(stage)));
			}
		});
		g.addActor(getPlantForPumpkinButton(btnSettings));
		g.addActor(btnSettings);
		
		return g;
	}
	
	private Group makeSettingsGroup() {
		final Group g = new Group();
		
		// The "Menu" pumpkin-button
		final Button menuBtn = new Button(new TextureRegionDrawable(atlas.findRegion("btn_menu")));
		menuBtn.setPosition(1280 / 2 - 290 - 230 / 2, 190);
		menuBtn.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				Gdx.input.setInputProcessor(null);
				settingsGroup.addAction(Actions.sequence(Actions.color(Color.CLEAR, FADE_TIME), Actions.visible(false)));
				mainGroup.addAction(Actions.sequence(Actions.delay(FADE_TIME), Actions.visible(true), Actions.color(Color.WHITE, FADE_TIME), new SetInputProcessorAction(stage)));
			}
		});
		g.addActor(getPlantForPumpkinButton(menuBtn));
		g.addActor(menuBtn);

		// The "Reset High Score" pumpkin-button
		final Button resetScoreBtn = new Button(new TextureRegionDrawable(atlas.findRegion("btn_reset_score")));
		resetScoreBtn.setPosition(1280 / 2 + 95 - 230 / 2, 190);
		resetScoreBtn.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
			}
		});
		g.addActor(getPlantForPumpkinButton(resetScoreBtn));
		g.addActor(resetScoreBtn);

		// The "Sound On/Off" pumpkin-button
		final Button soundBtn = new Button(new TextureRegionDrawable(atlas.findRegion("btn_sound_off")), null,
				new TextureRegionDrawable(atlas.findRegion("btn_sound_on")));
		soundBtn.setPosition(1280 / 2 - 95 - 230 / 2, 60);
		soundBtn.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
			}
		});
		g.addActor(getPlantForPumpkinButton(soundBtn));
		g.addActor(soundBtn);

		// The "Music On/Off" pumpkin-button
		final Button musicBtn = new Button(new TextureRegionDrawable(atlas.findRegion("btn_music_off")), null,
				new TextureRegionDrawable(atlas.findRegion("btn_music_on")));
		musicBtn.setPosition(1280 / 2 + 290 - 230 / 2, 60);
		musicBtn.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
			}
		});
		g.addActor(getPlantForPumpkinButton(musicBtn));
		g.addActor(musicBtn);
		
		return g;
	}
	
	private Group makeHelpGroup() {
		final Group g = new Group();
		
		// Get reference to all extra textures required outside of the atlas
		helpPanels = new Drawable[] {
				new TextureRegionDrawable(new TextureRegion(game.manager.get("help_panel_1.png", Texture.class))),
				new TextureRegionDrawable(new TextureRegion(game.manager.get("help_panel_2.png", Texture.class))),
				new TextureRegionDrawable(new TextureRegion(game.manager.get("help_panel_3.png", Texture.class))),
				new TextureRegionDrawable(new TextureRegion(game.manager.get("help_panel_4.png", Texture.class))), };

		// Start by showing the first panel
		helpIndex = 0;

		// The help panel
		helpPanel = new Image(helpPanels[helpIndex]);
		helpPanel.setPosition(1280 / 2 - 512 / 2, 100);
		g.addActor(helpPanel);

		// The "Previous" pumpkin-button
		final Button previousBtn = new Button(new TextureRegionDrawable(atlas.findRegion("btn_prev")));
		previousBtn.setPosition(1280 / 2 - 280 - 230 / 2, 25);
		previousBtn.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				if (helpIndex > 0) {
					helpIndex--;
					updateHelpPanel();
				}
			}
		});
		g.addActor(getPlantForPumpkinButton(previousBtn));
		g.addActor(previousBtn);

		// The "Menu" pumpkin-button
		final Button menuBtn = new Button(new TextureRegionDrawable(atlas.findRegion("btn_menu")));
		menuBtn.setPosition(1280 / 2 - 230 / 2, 25);
		menuBtn.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				Gdx.input.setInputProcessor(null);
				helpGroup.addAction(Actions.sequence(Actions.color(Color.CLEAR, FADE_TIME), Actions.visible(false)));
				mainGroup.addAction(Actions.sequence(Actions.delay(FADE_TIME), Actions.visible(true), Actions.color(Color.WHITE, FADE_TIME), new SetInputProcessorAction(stage)));
			}
		});
		g.addActor(getPlantForPumpkinButton(menuBtn));
		g.addActor(menuBtn);

		// The "Next" pumpkin-button
		final Button nextBtn = new Button(new TextureRegionDrawable(atlas.findRegion("btn_next")));
		nextBtn.setPosition(1280 / 2 + 280 - 230 / 2, 25);
		nextBtn.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				if (helpIndex < helpPanels.length - 1) {
					helpIndex++;
					updateHelpPanel();
				}
			}
		});
		g.addActor(getPlantForPumpkinButton(nextBtn));
		g.addActor(nextBtn);
		
		return g;
	}

	/** Update the currently-displayed help panel as per the current help-panel index. */
	private final void updateHelpPanel() {
		helpPanel.setDrawable(helpPanels[helpIndex]);
	}
}
