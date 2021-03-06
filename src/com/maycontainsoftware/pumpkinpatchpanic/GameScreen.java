package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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

	/** Highest round reached - either this game, or from previous game and loaded from prefs. */
	private int highestRound;

	/** Current round. */
	int currentRound;

	/**
	 * Whether or not the game is running. When set to false, game logic should not run (although it may be appropriate
	 * to run certain animations.)
	 */
	boolean gameRunning;

	/**
	 * The current dialog being displayed. A "dialog" in this app is a message flashed up on top of everything else.
	 * Only one dialog is supported at a time.
	 */
	Table dialog;

	/** The interface showing round, lives and time remaining. */
	Hud hud;

	/** Array of the pumpkins. */
	private PumpkinActor[] pumpkins;

	private final Music wind;

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

		// 30 seconds on the clock
		timeLeft = 30.0f;

		// Highest round reached
		highestRound = game.getHighestRound();

		// Start on round one
		currentRound = 1;

		// Game starts running
		gameRunning = true;

		// If music is playing, stop it; we don't play the background music while the game is running
		if (game.music.isPlaying()) {
			game.music.stop();
		}

		// Load atmospheric wind track
		wind = game.manager.get("wind.mp3", Music.class);
		wind.setLooping(false);
		// TODO: Should this follow music or sound setting?
		if (game.soundEnabled) {
			wind.play();
		}
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
		backLeft.setScale(0.875f);
		stage.addActor(backLeft);

		final PumpkinActor backRight = new PumpkinActor(this);
		backRight.setPosition(800 - 230 / 2, 720 - 510);
		backRight.setScale(0.875f);
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

		pumpkins = new PumpkinActor[] { backLeft, backRight, frontLeft, frontMiddle, frontRight };

		// Head Up Display
		hud = new Hud();
		stage.addActor(hud);

		// Stage action - this handles the timer countdown and round-over detection
		stage.addAction(new Action() {
			@Override
			public boolean act(float delta) {

				// Only act if the game is currently running
				if (gameRunning) {

					// Decrement time left
					timeLeft -= delta;

					// Check for end of round
					if (timeLeft <= 0.0f) {

						// Set time left in round to exactly zero
						timeLeft = 0.0f;

						// Mark the game as not currently running
						gameRunning = false;

						// Show round-over dialog
						dialog = createRoundOverDialog();
						stage.addActor(dialog);
					}

					// Update label
					hud.updateTimeLeft();
				}

				return false;
			}
		});

		// Catch back button
		Gdx.input.setCatchBackKey(true);
		stage.getRoot().addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {

							// Stop atmospheric sound
							if (wind.isPlaying()) {
								wind.stop();
							}

							game.setScreen(new MainMenuScreen(game));
							GameScreen.this.dispose();
						}
					});
					return true;
				} else {
					return super.keyDown(event, keycode);
				}
			}
		});

		game.currentScreenCallback.notifyScreenVisible(ICurrentScreenCallback.Screen.GAME);
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		// Table.drawDebug(stage);
	}

	/** Create a dialog to notify of "round over" condition. */
	private final Table createRoundOverDialog() {
		/**
		 * Inner class to represent the dialog.
		 * 
		 * @author Charlie
		 */
		class RoundOverDialog extends Table {

			/** Countdown time before next round starts. */
			private float countdown = 5.0f;

			/** Integer version of countdown time. */
			private int countdownInt = MathUtils.ceil(countdown);

			public RoundOverDialog() {

				// White background, black border top and bottom
				setBackground(new NinePatchDrawable(atlas.createPatch("horizontal_flash_bg")));

				// Bounds of dialog
				final int height = 340;
				setBounds(0, 720 / 2 - height / 2, 1280, height);

				// Get references to required widget styles
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
				final Label countdownLabel = new Label("0:0" + countdownInt, style64);
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
							// Note that this will NOT WORK for values >= 10 seconds
							countdownLabel.setText("0:0" + countdownInt);

							// Highlight the change with a simple animation
							countdownLabel.setColor(Color.RED);
							countdownLabel.addAction(Actions.color(Color.WHITE, 0.75f));
						}

						if (countdown <= 0.0f) {
							// Countdown is complete!

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
		}

		return new RoundOverDialog();
	}

	/** Create a dialog to notify of "game over" condition. */
	final Table createGameOverDialog() {
		/**
		 * Inner class to represent the dialog.
		 * 
		 * @author Charlie
		 */
		class GameOverDialog extends Table {

			public GameOverDialog() {

				// White background, black border top and bottom
				setBackground(new NinePatchDrawable(atlas.createPatch("horizontal_flash_bg")));

				// Bounds of dialog
				final int height = 340;
				setBounds(0, 720 / 2 - height / 2, 1280, height);

				// Get references to required widget styles
				final BitmapFont font32 = game.manager.get("arialb_32.fnt", BitmapFont.class);
				final BitmapFont font64 = game.manager.get("arialb_64.fnt", BitmapFont.class);
				final Label.LabelStyle style32 = new Label.LabelStyle(font32, Color.WHITE);
				final Label.LabelStyle style64 = new Label.LabelStyle(font64, Color.WHITE);

				row();
				final Label roundCompleteLabel = new Label("Game Over!", style64);
				roundCompleteLabel.setColor(1.0f, 0.5f, 0.0f, 1.0f);
				add(roundCompleteLabel);
				add().width(394.0f);

				row();
				add(new Label("You reached round " + currentRound, style32));

				if (currentRound > highestRound) {
					row();
					add(new Label("New high score!", style32));

					// Update local value
					highestRound = currentRound;

					// Save to preferences
					game.setHighestRound(highestRound);

					// Update HUD, apply an interesting value-change effect
					hud.highestRoundLabel.setText(String.valueOf(highestRound));
					hud.highestRoundLabel.setColor(Color.RED);
					hud.highestRoundLabel.addAction(Actions.color(Color.WHITE, 1.0f));
				}

				gameRunning = false;

				// debug();

				// The "Menu" pumpkin-button
				final Button menuBtn = new PumpkinButton(atlas, "menu", game);
				menuBtn.setPosition(940 - 200 / 2, 340 / 2 - 190 / 2 + 30);
				menuBtn.addListener(new ChangeListener() {
					@Override
					public void changed(final ChangeEvent event, final Actor actor) {
						game.setScreen(new MainMenuScreen(game));
						GameScreen.this.dispose();
					}
				});
				addActor(menuBtn);

				// Stop atmospheric sound
				if (wind.isPlaying()) {
					wind.stop();
				}
			}
		}

		return new GameOverDialog();
	}

	/** Update game state for next round. */
	private void nextRound() {

		// 30 seconds on the clock
		timeLeft = 30.0f;

		// Extra life every five levels, so only if last level was divisible by 5
		if (currentRound % 5 == 0) {
			livesLeft++;
			hud.updateLives();
		}

		// Increment round
		currentRound++;
		hud.updateCurrentRound();

		// Reset pumpkins
		for (final PumpkinActor pumpkin : pumpkins) {
			pumpkin.resetToDormant();
		}

		// Game is running again
		gameRunning = true;

		// Start atmospheric sound as required
		if (game.soundEnabled) {
			wind.play();
		}
	}

	/**
	 * Inner class that represents the head-up display.
	 * 
	 * @author Charlie
	 */
	class Hud extends Table {

		/** Label containing the current highest round reached. */
		private final Label highestRoundLabel;

		/** Label containing the current round reached. */
		private final Label currentRoundLabel;

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
			add(new Label("Highest Round", style32)).left();
			add(new Label("Lives Left", style32)).expandX();
			final Label currentRoundTextLabel = new Label("Current Round", style32);
			currentRoundTextLabel.setAlignment(Align.right);
			add(currentRoundTextLabel).right();

			row();
			add(highestRoundLabel = new Label(String.valueOf(highestRound), style64)).left();

			// Life tokens are five lives and one "overflow" indication
			lifeTokens = new Image[] { new Image(atlas.findRegion("life")), new Image(atlas.findRegion("life")),
					new Image(atlas.findRegion("life")), new Image(atlas.findRegion("life")),
					new Image(atlas.findRegion("life")), new Image(atlas.findRegion("life_plus")) };

			// Life container
			lifeContainer = new HorizontalGroup();
			add(lifeContainer).top();
			// Update initial life token state
			updateLives();

			add(currentRoundLabel = new Label(String.valueOf(currentRound), style64)).right();

			row();
			add().width(400.0f);
			add(new Label("Time Remaining", style32));
			add().width(400.0f);

			row();
			add();
			add(timeLeftLabel = new Label(getTimeLeftAsString(), style64)).expandY().top();
			add();
		}

		/** Update the time left label using the value held at Screen level. */
		private void updateTimeLeft() {
			timeLeftLabel.setText(getTimeLeftAsString());
		}

		/** Update the current round label using the value held at Screen level. */
		private void updateCurrentRound() {
			currentRoundLabel.setText(String.valueOf(currentRound));
			currentRoundLabel.setColor(Color.RED);
			currentRoundLabel.addAction(Actions.color(Color.WHITE, 1.0f));
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
