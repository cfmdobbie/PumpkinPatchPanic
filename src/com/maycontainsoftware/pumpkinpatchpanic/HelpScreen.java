package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * A screen that displays basic "how to play" information, in the form of a small number of "panels" - graphical assets
 * with instructions on - that can be flipped through.
 * 
 * @author Charlie
 */
public class HelpScreen extends PumpkinScreen {

	/** Whether debug output should be logged. */
	private static final boolean DEBUG = false;

	/** Tag, for logging purposes. */
	private static final String TAG = HelpScreen.class.getSimpleName();

	/** The Drawables that make up the help panels. */
	private Drawable[] helpPanels;

	/** The index of the current help panel. */
	private int helpIndex;

	/** The Image widget that displays the current help panel. */
	private Image helpPanel;

	/**
	 * Construct a new HelpScreen.
	 * 
	 * @param game
	 *            The Game instance.
	 */
	public HelpScreen(final PumpkinGame game) {
		super(game);
	}

	@Override
	public void show() {
		if (DEBUG) {
			Gdx.app.log(TAG, "show()");
		}

		super.show();

		// Get reference to all extra textures required outside of the atlas
		helpPanels = new Drawable[] {
				new TextureRegionDrawable(new TextureRegion(game.manager.get("help_panel_1.png", Texture.class))),
				new TextureRegionDrawable(new TextureRegion(game.manager.get("help_panel_2.png", Texture.class))),
				new TextureRegionDrawable(new TextureRegion(game.manager.get("help_panel_3.png", Texture.class))),
				new TextureRegionDrawable(new TextureRegion(game.manager.get("help_panel_4.png", Texture.class))),
				new TextureRegionDrawable(new TextureRegion(game.manager.get("help_panel_5.png", Texture.class))),
				new TextureRegionDrawable(new TextureRegion(game.manager.get("help_panel_6.png", Texture.class))),

		};

		// Start by showing the first panel
		helpIndex = 0;

		// The help panel
		helpPanel = new Image(helpPanels[helpIndex]);
		helpPanel.setPosition(1280 / 2 - 512 / 2, 100);
		stage.addActor(helpPanel);

		// The "Previous" pumpkin-button
		final Button previousBtn = new PumpkinButton(atlas, "arrow_left", game);
		previousBtn.setPosition(1280 / 2 - 280 - 170 / 2, 25);
		previousBtn.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				if (helpIndex > 0) {
					helpIndex--;
					updateHelpPanel();
				}
			}
		});
		stage.addActor(previousBtn);

		// The "Menu" pumpkin-button
		final Button menuBtn = new PumpkinButton(atlas, "menu", game);
		menuBtn.setPosition(1280 / 2 - 170 / 2, 25);
		menuBtn.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				game.setScreen(new MainMenuScreen(game));
				HelpScreen.this.dispose();
			}
		});
		stage.addActor(menuBtn);

		// The "Next" pumpkin-button
		final Button nextBtn = new PumpkinButton(atlas, "arrow_right", game);
		nextBtn.setPosition(1280 / 2 + 280 - 170 / 2, 25);
		nextBtn.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				if (helpIndex < helpPanels.length - 1) {
					helpIndex++;
					updateHelpPanel();
				}
			}
		});
		stage.addActor(nextBtn);

		// HUD to display highest round beaten
		stage.addActor(new MenuHud(game));

		// Catch back button
		Gdx.input.setCatchBackKey(true);
		stage.getRoot().addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							game.setScreen(new MainMenuScreen(game));
							HelpScreen.this.dispose();
						}
					});
					return true;
				} else {
					return super.keyDown(event, keycode);
				}
			}
		});

		game.currentScreenCallback.notifyScreenVisible(ICurrentScreenCallback.Screen.HELP);
	}

	/** Update the currently-displayed help panel as per the current help-panel index. */
	private final void updateHelpPanel() {
		helpPanel.setDrawable(helpPanels[helpIndex]);
	}
}
