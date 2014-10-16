package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
	 *            The PPPGame instance.
	 */
	public HelpScreen(final PPPGame game) {
		super(game);
	}

	@Override
	public void show() {
		if (DEBUG) {
			Gdx.app.log(TAG, "show()");
		}

		super.show();

		// Load the atlas
		final TextureAtlas atlas = game.manager.get("atlas.atlas", TextureAtlas.class);

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
		helpPanel.setPosition(1280 / 2 - 512 / 2, 160);
		stage.addActor(helpPanel);

		// Previous button
		
		// The plant attached to this pumpkin-button
		final Image prevPlant = new Image(atlas.findRegion("plant"));
		prevPlant.setPosition(1280 / 2 - 300 - 230 / 2 - 33, 100 - 43);
		stage.addActor(prevPlant);
		
		// The "Previous" pumpkin-button
		final Button previousBtn = new Button(new TextureRegionDrawable(atlas.findRegion("btn_prev")));
		previousBtn.setPosition(1280 / 2 - 300 - 230 / 2, 100);
		previousBtn.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				if (helpIndex > 0) {
					helpIndex--;
					updatePanel();
				}
			}
		});
		stage.addActor(previousBtn);

		// Menu button
		
		// The plant attached to this pumpkin-button
		final Image menuPlant = new Image(atlas.findRegion("plant"));
		menuPlant.setPosition(1280 / 2 - 230 / 2 - 33, 100 - 43);
		stage.addActor(menuPlant);
		
		// The "Menu" pumpkin-button
		final Button menuBtn = new Button(new TextureRegionDrawable(atlas.findRegion("btn_menu")));
		menuBtn.setPosition(1280 / 2 - 230 / 2, 100);
		menuBtn.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				game.setScreen(new MainMenuScreen(game));
				HelpScreen.this.dispose();
			}
		});
		stage.addActor(menuBtn);
		
		
		// Next button

		// The plant attached to this pumpkin-button
		final Image nextPlant = new Image(atlas.findRegion("plant"));
		nextPlant.setPosition(1280 / 2 + 300 - 230 / 2 - 33, 100 - 43);
		stage.addActor(nextPlant);
		
		// The "Next" pumpkin-button
		final Button nextBtn = new Button(new TextureRegionDrawable(atlas.findRegion("btn_next")));
		nextBtn.setPosition(1280 / 2 + 300 - 230 / 2, 100);
		nextBtn.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				if (helpIndex < helpPanels.length - 1) {
					helpIndex++;
					updatePanel();
				}
			}
		});
		stage.addActor(nextBtn);

		game.currentScreenCallback.notifyScreenVisible(ICurrentScreenCallback.Screen.HELP);
	}

	/** Update the currently-displayed help panel as per the current help-panel index. */
	private final void updatePanel() {
		helpPanel.setDrawable(helpPanels[helpIndex]);
	}
}
