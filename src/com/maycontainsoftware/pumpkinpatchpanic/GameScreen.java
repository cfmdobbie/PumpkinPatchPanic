package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

/**
 * Game screen.
 * 
 * @author Charlie
 */
public class GameScreen extends PumpkinScreen {

	/** Whether debug output should be logged. */
	private static final boolean DEBUG = false;

	/** Tag, for logging purposes. */
	private static final String TAG = GameScreen.class.getSimpleName();

	/**
	 * Construct a new GameScreen.
	 * 
	 * @param game
	 *            The Game instance.
	 */
	public GameScreen(final PumpkinGame game) {
		super(game);
	}

	@Override
	public void show() {
		if (DEBUG) {
			Gdx.app.log(TAG, "show()");
		}

		super.show();

		// TODO: Create game elements

		// Pumpkins are 230x150

		final Image backLeft = new Image(atlas.findRegion("pumpkin"));
		backLeft.setPosition(480 - 230 / 2, 720 - 510);
		stage.addActor(getPlantForPumpkinButton(backLeft));
		stage.addActor(backLeft);
		stage.addActor(getFaceForPumpkinButton(backLeft));

		final Image backRight = new Image(atlas.findRegion("pumpkin"));
		backRight.setPosition(800 - 230 / 2, 720 - 510);
		stage.addActor(getPlantForPumpkinButton(backRight));
		stage.addActor(backRight);
		stage.addActor(getFaceForPumpkinButton(backRight));

		final Image frontLeft = new Image(atlas.findRegion("pumpkin"));
		frontLeft.setPosition(320 - 230 / 2, 720 - 670);
		stage.addActor(getPlantForPumpkinButton(frontLeft));
		stage.addActor(frontLeft);
		stage.addActor(getFaceForPumpkinButton(frontLeft));

		final Image frontMiddle = new Image(atlas.findRegion("pumpkin"));
		frontMiddle.setPosition(640 - 230 / 2, 720 - 670);
		stage.addActor(getPlantForPumpkinButton(frontMiddle));
		stage.addActor(frontMiddle);
		stage.addActor(getFaceForPumpkinButton(frontMiddle));

		final Image frontRight = new Image(atlas.findRegion("pumpkin"));
		frontRight.setPosition(960 - 230 / 2, 720 - 670);
		stage.addActor(getPlantForPumpkinButton(frontRight));
		stage.addActor(frontRight);
		stage.addActor(getFaceForPumpkinButton(frontRight));

		// Head Up Display
		stage.addActor(new Hud());

		game.currentScreenCallback.notifyScreenVisible(ICurrentScreenCallback.Screen.GAME);
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		// Table.drawDebug(stage);
	}

	protected final Image getFaceForPumpkinButton(final Widget pumpkin) {
		final Image face = new Image(atlas.findRegion("face_normal"));
		face.setPosition(pumpkin.getX(), pumpkin.getY());
		face.setColor(1.0f, 1.0f, 1.0f, 0.0f);
		return face;
	}

	/**
	 * Inner class that represents the head-up display.
	 * 
	 * @author Charlie
	 */
	class Hud extends Table {

		/** Label containing the current highest level reached. */
		private final Label highLevelLabel;

		/** Label containing the current level reached. */
		private final Label currentLevelLabel;

		/** Label containing the current time remaining. */
		private final Label timeRemainingLabel;

		/** Array of tokens to represent lives on the HUD - used by the updateLives() method. */
		final Image[] lifeTokens;

		/** Container for the life tokens. */
		private final HorizontalGroup lifeContainer;

		/** Current lives remaining. */
		private int livesLeft;

		/** Construct the HUD. */
		public Hud() {

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

			// Create widgets

			row();
			add(new Label("High Level", style32));
			add(new Label("Lives Left", style32)).expandX();
			add(new Label("Current Level", style32));

			row();
			add(highLevelLabel = new Label("7", style64)).left();

			// Life tokens are five lives and one "overflow" indication
			lifeTokens = new Image[] { new Image(atlas.findRegion("life")), new Image(atlas.findRegion("life")),
					new Image(atlas.findRegion("life")), new Image(atlas.findRegion("life")),
					new Image(atlas.findRegion("life")), new Image(atlas.findRegion("life_plus")) };

			// Life container
			lifeContainer = new HorizontalGroup();
			add(lifeContainer).top();

			add(currentLevelLabel = new Label("3", style64)).right();

			row();
			add();
			add(new Label("Time Remaining", style32));
			add();

			row();
			add();
			add(timeRemainingLabel = new Label("0:43", style64)).expandY().top();
			add();

			// XXX: Temporary listener to test HUD display
			timeRemainingLabel.addListener(new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					highLevelLabel.setText("" + MathUtils.random(0, 20));
					currentLevelLabel.setText("" + MathUtils.random(0, 20));
					timeRemainingLabel.setText("" + MathUtils.random(0, 1) + ":" + MathUtils.random(0, 5)
							+ MathUtils.random(0, 9));
					livesLeft = MathUtils.random(0, 6);
					updateLives();
					return super.touchDown(event, x, y, pointer, button);
				}
			});

			// Initial state

			// Start with 3 lives
			livesLeft = 3;
			updateLives();
		}

		/** Update the lives display. */
		private final void updateLives() {
			// Remove all life tokens
			for (final Image token : lifeTokens) {
				lifeContainer.removeActor(token);
			}
			// Add back as many as we have lives, to a maximum of the total available
			for (int i = 0; i < lifeTokens.length; i++) {
				if (livesLeft > i) {
					lifeContainer.addActor(lifeTokens[i]);
				}
			}
		}
	}
}
