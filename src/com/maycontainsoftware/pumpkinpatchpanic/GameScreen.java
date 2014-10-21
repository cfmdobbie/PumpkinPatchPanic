package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

/**
 * Game screen.
 * 
 * @author Charlie
 */
public class GameScreen extends PumpkinScreen {

	/** Whether debug output should be logged. */
	static final boolean DEBUG = false;

	/** Tag, for logging purposes. */
	static final String TAG = GameScreen.class.getSimpleName();

	// Game data

	/** Number of lives left. Game is over when all lives lost. */
	int livesLeft;

	/** Time left in the current round. */
	private float timeLeft;

	/** Highest level reached - either this game, or from previous game and loaded from prefs. */
	private int highLevel;

	/** Current level. */
	private int currentLevel;

	/** Whether or not the game is running. */
	boolean gameRunning;

	/** The current dialog being displayed. */
	private Table dialog;

	/** The interface showing level, lives and time remaining. */
	Hud hud;

	/**
	 * Construct a new GameScreen.
	 * 
	 * @param game
	 *            The Game instance.
	 */
	public GameScreen(final PumpkinGame game) {
		super(game);

		// Set up game data

		// Start with three lives
		livesLeft = 3;

		// One minute on the clock
		// TODO: Set to 30 seconds for testing purposes
		timeLeft = 30.0f;

		// Highest level reached
		highLevel = game.getHighLevel();

		// Start on level one
		currentLevel = 1;

		// Game starts running
		gameRunning = true;
	}

	@Override
	public void show() {
		if (DEBUG) {
			Gdx.app.log(TAG, "show()");
		}

		// Let PumpkinScreen do its thing
		super.show();

		final PumpkinActor backLeft = new PumpkinActor(this);
		backLeft.setPosition(480 - 230 / 2, 720 - 510);
		stage.addActor(backLeft);

		final PumpkinActor backRight = new PumpkinActor(this);
		backRight.setPosition(800 - 230 / 2, 720 - 510);
		stage.addActor(backRight);

		final PumpkinActor frontLeft = new PumpkinActor(this);
		frontLeft.setPosition(320 - 230 / 2, 720 - 670);
		stage.addActor(frontLeft);

		final PumpkinActor frontMiddle = new PumpkinActor(this);
		frontMiddle.setPosition(640 - 230 / 2, 720 - 670);
		stage.addActor(frontMiddle);

		final PumpkinActor frontRight = new PumpkinActor(this);
		frontRight.setPosition(960 - 230 / 2, 720 - 670);
		stage.addActor(frontRight);

		// Head Up Display
		hud = new Hud();
		stage.addActor(hud);

		// Stage action
		stage.addAction(new Action() {
			@Override
			public boolean act(float delta) {

				if (gameRunning) {
					// Decrement time left
					timeLeft -= delta;

					// Check for end of round
					if (timeLeft <= 0.0f) {

						// Set left left in round to exactly zero
						timeLeft = 0.0f;

						// Note that game is not currently running
						gameRunning = false;

						// Update the highest level beaten, if required
						if (currentLevel > highLevel) {
							// Update local value
							highLevel = currentLevel;
							// Save to preferences
							game.setHighLevel(highLevel);
							// Update HUD, apply an interesting value-change effect
							hud.highLevelLabel.setText(String.valueOf(highLevel));
							hud.highLevelLabel.setColor(Color.RED);
							hud.highLevelLabel.addAction(Actions.color(Color.WHITE, 1.0f));
						}

						dialog = createRoundOverDialog();
						stage.addActor(dialog);
					}

					// Update label
					hud.updateTimeLeft();
				}

				return false;
			}
		});

		// Menu button
		// final Button menuBtn = new Button(new TextureRegionDrawable(atlas.findRegion("btn_menu")));
		// menuBtn.setPosition(1280 / 2 - 230 / 2, 25);
		// menuBtn.setPosition(10.0f, 10.0f);
		// menuBtn.addListener(new ChangeListener() {
		// @Override
		// public void changed(final ChangeEvent event, final Actor actor) {
		// }
		// });
		// flash.addActor(getPlantForPumpkinButton(menuBtn));
		// flash.addActor(menuBtn);

		game.currentScreenCallback.notifyScreenVisible(ICurrentScreenCallback.Screen.GAME);
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		// Table.drawDebug(stage);
	}

	/**
	 * Get an image that directly overlays the pumpkin button which displays the "normal" pumpkin face. Note that the
	 * actor's alpha is set to 0.0f on creation.
	 * 
	 * @param pumpkin
	 *            The Widget that reprensents the pumpkin button.
	 * @return The pumpkin's face image
	 */
	protected final Image getFaceForPumpkinButton(final Widget pumpkin) {
		final Image face = new Image(atlas.findRegion("face_normal"));
		face.setPosition(pumpkin.getX(), pumpkin.getY());
		face.setColor(1.0f, 1.0f, 1.0f, 0.0f);
		return face;
	}

	private final Table createRoundOverDialog() {

		class RoundOverDialog extends Table {
			private float countdown = 5.0f;
			private int countdownInt = MathUtils.ceil(countdown);
			private final Label countdownLabel;

			public RoundOverDialog() {
				setBackground(new NinePatchDrawable(atlas.createPatch("horizontal_flash_bg")));
				final int height = 340;
				setBounds(0, 720 / 2 - height / 2, 1280, height);

				final BitmapFont font32 = game.manager.get("arialb_32.fnt", BitmapFont.class);
				final BitmapFont font64 = game.manager.get("arialb_64.fnt", BitmapFont.class);
				final Label.LabelStyle style32 = new Label.LabelStyle(font32, Color.WHITE);
				final Label.LabelStyle style64 = new Label.LabelStyle(font64, Color.WHITE);

				row();
				final Label roundCompleteLabel = new Label("Round Complete!", style64);
				roundCompleteLabel.setColor(1.0f, 0.5f, 0.0f, 1.0f);
				add(roundCompleteLabel);

				row();
				add(new Label("Next round starts in:", style32));

				row();
				countdownLabel = new Label("", style64);
				updateTime();
				// countdownLabel.setColor(1.0f, 0.5f, 0.0f, 1.0f);
				add(countdownLabel);

				addAction(new Action() {
					@Override
					public boolean act(float delta) {

						// Update the countdown value
						countdown -= delta;

						// Determine the preferred display value
						final int newCountdownInt = MathUtils.ceil(countdown);

						// If full second has passed (i.e. the display value has changed), update it
						if (countdownInt != newCountdownInt) {

							// Update the local value
							countdownInt = newCountdownInt;

							// Update the Label
							updateTime();

							// Highlight the change with a simple animation
							countdownLabel.setColor(Color.RED);
							countdownLabel.addAction(Actions.color(Color.WHITE, 0.75f));
						}

						if (countdown <= 0.0f) {
							// Coundown is complete!

							// Dispose of this dialog
							dialog.remove();
							dialog = null;

							// Reset the game
							nextRound();

							// Consume the Action
							return true;
						} else {
							// Countdown still running, continue
							return false;
						}
					}
				});
			}

			/**
			 * Update the coundown time shown on the round-complete dialog. Note that this is hard-coded to work for
			 * times from five seconds to zero only.
			 */
			private void updateTime() {
				// Note that this will NOT WORK for values >= 10 seconds
				countdownLabel.setText("0:0" + countdownInt);
			}
		}

		return new RoundOverDialog();
	}

	private void nextRound() {

		// One minute on the clock
		// TODO: Set to 30 seconds for testing purposes
		timeLeft = 30.0f;

		// Start on level one
		currentLevel++;
		hud.updateCurrentLevel();

		// Game is running again
		gameRunning = true;
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

		/** Label containing the current time left. */
		private final Label timeLeftLabel;

		/** Array of tokens to represent lives on the HUD - used by the updateLives() method. */
		final Image[] lifeTokens;

		/** Container for the life tokens. */
		private final HorizontalGroup lifeContainer;

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
			add(highLevelLabel = new Label(String.valueOf(highLevel), style64)).left();

			// Life tokens are five lives and one "overflow" indication
			lifeTokens = new Image[] { new Image(atlas.findRegion("life")), new Image(atlas.findRegion("life")),
					new Image(atlas.findRegion("life")), new Image(atlas.findRegion("life")),
					new Image(atlas.findRegion("life")), new Image(atlas.findRegion("life_plus")) };

			// Life container
			lifeContainer = new HorizontalGroup();
			add(lifeContainer).top();
			// Update initial life token state
			updateLives();

			add(currentLevelLabel = new Label(String.valueOf(currentLevel), style64)).right();

			row();
			add();
			add(new Label("Time Remaining", style32));
			add();

			row();
			add();
			add(timeLeftLabel = new Label(getTimeLeftAsString(), style64)).expandY().top();
			add();
		}

		/** Update the time left label using the value held at Screen level. */
		private void updateTimeLeft() {
			timeLeftLabel.setText(getTimeLeftAsString());
		}

		/** Update the current level label using the value held at Screen level. */
		private void updateCurrentLevel() {
			currentLevelLabel.setText(String.valueOf(currentLevel));
		}

		/** Return the time left as a String. */
		private String getTimeLeftAsString() {
			// Using ceil instead of integer-cast because we want time to terminate as soon as it reaches 0:00
			int time = MathUtils.ceil(timeLeft);
			int mins = time / 60;
			int secs = time % 60;
			return mins + ":" + (secs < 10 ? "0" + secs : secs);
		}

		/** Update the lives display. */
		final void updateLives() {
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
