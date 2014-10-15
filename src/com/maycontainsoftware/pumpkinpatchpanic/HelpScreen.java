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

public class HelpScreen extends PumpkinScreen {
	
	/** Whether debug output should be logged. */
	private static final boolean DEBUG = false;

	/** Tag, for logging purposes. */
	private static final String TAG = HelpScreen.class.getSimpleName();
	
	private Drawable[] helpPanels;
	private int helpIndex;
	private Image helpPanel;
	
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
		final TextureAtlas atlas = game.manager.get("main_menu.atlas", TextureAtlas.class);
		
		// Get reference to all extra textures required outside of the atlas
		helpPanels = new Drawable[] {
				new TextureRegionDrawable(new TextureRegion(game.manager.get("help_panel_1.png", Texture.class))),
				new TextureRegionDrawable(new TextureRegion(game.manager.get("help_panel_2.png", Texture.class))),
				new TextureRegionDrawable(new TextureRegion(game.manager.get("help_panel_3.png", Texture.class))),
				new TextureRegionDrawable(new TextureRegion(game.manager.get("help_panel_4.png", Texture.class))),
		};
		
		// Start by showing the first panel
		helpIndex = 0;
		
		// The help panel
		helpPanel = new Image(helpPanels[helpIndex]);
		helpPanel.setPosition(1280 / 2 - 512 / 2, 160);
		stage.addActor(helpPanel);
		
		// "Previous" button
		final Button previousBtn = new Button(new TextureRegionDrawable(atlas.findRegion("btn_prev")));
		previousBtn.setPosition(1280 / 2 - 300 - 230 / 2, 50);
		previousBtn.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				if(helpIndex > 0) {
					helpIndex--;
					updatePanel();
				}
			}
		});
		stage.addActor(previousBtn);
		
		// "Previous" button
		final Button menuBtn = new Button(new TextureRegionDrawable(atlas.findRegion("btn_menu")));
		menuBtn.setPosition(1280 / 2 - 230 / 2, 50);
		menuBtn.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				game.setScreen(new MainMenuScreen(game));
				HelpScreen.this.dispose();
			}
		});
		stage.addActor(menuBtn);
		
		// "Next" button
		final Button nextBtn = new Button(new TextureRegionDrawable(atlas.findRegion("btn_next")));
		nextBtn.setPosition(1280 / 2 + 300 - 230 / 2, 50);
		nextBtn.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				if(helpIndex < helpPanels.length - 1) {
					helpIndex++;
					updatePanel();
				}
			}
		});
		stage.addActor(nextBtn);
	}
	
	private final void updatePanel() {
		helpPanel.setDrawable(helpPanels[helpIndex]);
	}
}
