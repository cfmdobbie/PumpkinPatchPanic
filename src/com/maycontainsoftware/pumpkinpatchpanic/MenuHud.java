package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Head-up display for use in menus.
 * 
 * @author Charlie
 */
public class MenuHud extends Table {

	/** Label containing the current highest round reached. */
	private final Label highestRoundLabel;
	
	/** The game instance. */
	private final PumpkinGame game;

	/** Construct the HUD. */
	public MenuHud(final PumpkinGame game) {
		
		this.game = game;

		// Fill screen, 40px border
		setFillParent(true);
		pad(40.0f, 60.0f, 40.0f, 60.0f);

		// Debug rendering
		// debug();

		// Load fonts
		final BitmapFont font32 = game.manager.get("arialb_32.fnt", BitmapFont.class);
		final BitmapFont font64 = game.manager.get("arialb_64.fnt", BitmapFont.class);

		// Label styles
		Label.LabelStyle style32 = new Label.LabelStyle(font32, Color.WHITE);
		Label.LabelStyle style64 = new Label.LabelStyle(font64, Color.WHITE);

		row();
		add(new Label("Highest Round", style32)).left();

		row();
		add(highestRoundLabel = new Label("", style64)).expandX().expandY().top().left();
		updateHighestRound();
	}

	/** Update the highest round display from the game (via prefs). */
	public void updateHighestRound() {
		highestRoundLabel.setText(String.valueOf(game.getHighestRound()));
	}
}
