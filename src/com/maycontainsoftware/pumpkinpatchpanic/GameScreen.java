package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
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
	private static final boolean DEBUG = false;

	/** Tag, for logging purposes. */
	private static final String TAG = GameScreen.class.getSimpleName();

	// Game data

	/** Number of lives left. Game is over when all lives lost. */
	private int livesLeft;

	/** Time left in the current round. */
	private float timeLeft;

	/** Highest level reached - either this game, or from previous game and loaded from prefs. */
	private int highLevel;

	/** Current level. */
	private int currentLevel;

	/** Whether or not the game is running. */
	private boolean gameRunning;

	/** The current dialog being displayed. */
	private Table dialog;

	/** The interface showing level, lives and time remaining. */
	private Hud hud;

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
	private class Hud extends Table {

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

	/**
	 * Actor that represents a haunted pumpkin. During the game, a pumpkin goes through a number of stages:
	 * 
	 * 1) Initial delay. Initially, the pumpkin is dormant - a random delay exists before any other action takes place.
	 * 
	 * 2) Possession. Spirit begins to enter the pumpkin. Carved face fades in (fully) over random period of time.
	 * 
	 * 3) Possession Delay. Once face is completely visible, a random delay exists before either the pumpkin recovers,
	 * or it becomes possessed.
	 * 
	 * 4a) Recovery. Carved face fades away to a partially-visible state over a random period of time, then continues
	 * Possession.
	 * 
	 * 4b) Possessed. Evil face appears. In this state, player can tap pumpkin to exorcise it, returning it to the
	 * initial state. If left in Possessed state for a fixed period of time, the spirit escapes.
	 * 
	 * 5) Spirit Release. The spirit escapes from the pumpkin. The carved face disappears altogether, the pumpkin
	 * reverts to the initial state and the player loses a life.
	 * 
	 * @author Charlie
	 */
	private static class PumpkinActor extends Actor {

		/** The pumpkin texture. */
		final TextureRegion pumpkin;

		/** The foliage texture. */
		final TextureRegion plant;

		/** The normal face texture. */
		final TextureRegion face;

		/** The evil face texture. */
		final TextureRegion evilFace;

		/** The hole texture. */
		final TextureRegion hole;

		/** Reference to the game screen, used for accessing resources and global game state. */
		private final GameScreen screen;

		/** Enumeration representing pumpkin state. */
		private static enum PumpkinState {
			Dormant,
			Possession,
			Possession_Delay,
			Recovery,
			Possessed,
			Spirit_Release,
		}

		/** Pumpkin state. */
		private PumpkinState state;

		/** Temporary variable used for meausing time left in current state. */
		private float timer;

		/** Current alpha value of face graphic. */
		private float faceAlpha;

		/** Change in face alpha value, per second. Note this can be (and frequently is) negative. */
		private float alphaChangePerSecond;

		/** Construct a PumpkinActor. */
		public PumpkinActor(final GameScreen screen) {

			// Store reference to the screen
			this.screen = screen;

			// Load textures required for rendering
			plant = screen.atlas.findRegion("plant");
			pumpkin = screen.atlas.findRegion("pumpkin");
			face = screen.atlas.findRegion("face_normal");
			evilFace = screen.atlas.findRegion("face_evil");
			hole = screen.atlas.findRegion("hole");

			// For collision-detection reasons, the size of the Actor is the size of just the pumpkin
			setWidth(pumpkin.getRegionWidth());
			setHeight(pumpkin.getRegionHeight());

			// Initial state
			state = PumpkinState.Dormant;
			timer = MathUtils.random(1.0f, 3.0f);
			faceAlpha = 0.0f;

			addListener(new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

					if (!screen.gameRunning) {
						// If game isn't running, return false (ignore input, leave for other Actors to intercept)
						return false;
					} else {
						switch (state) {
						case Dormant:
						case Possession:
						case Possession_Delay:
						case Recovery:
							// Too early
							if (DEBUG) {
								Gdx.app.log(TAG, "Hit too early");
							}

							// TODO: Play sound: error, life lost

							// TODO: Decrement lives

							// Move to dormant state
							state = PumpkinState.Dormant;
							timer = MathUtils.random(1.0f, 3.0f);

							break;
						case Possessed:
							// Correct timing
							if (DEBUG) {
								Gdx.app.log(TAG, "Correct hit");
							}

							// TODO: Play sound: good hit, exorcised

							// Move to dormant state
							state = PumpkinState.Dormant;
							timer = MathUtils.random(1.0f, 3.0f);

							break;
						case Spirit_Release:
							// Too late
							if (DEBUG) {
								Gdx.app.log(TAG, "Hit too late");
							}

							// TODO: Play sound: error

							// Ignore this input.

							// Player has already been penalised for letting the spirit escape, so no further penalty
							// required.

							// No state change required - pumpkin will automatically recover

							break;
						default:
							throw new IllegalStateException();
						}
						return true;
					}
				}
			});
		}

		@Override
		public void act(float delta) {
			// Run any Actions added to this Actor
			super.act(delta);

			if (screen.gameRunning) {
				// Whatever we're doing, decrement the timer
				timer -= delta;

				switch (state) {
				case Dormant:
					// No special additional calculations

					// If timer expired, move to Possession state
					if (timer <= 0.0f) {
						state = PumpkinState.Possession;
						timer = MathUtils.random(1.0f, 5.0f);
						alphaChangePerSecond = (1.0f - 0.0f) / timer;
						faceAlpha = 0.0f;
					}
					break;
				case Possession:
					// Update face alpha value (alphaChangePerSecond will be positive)
					faceAlpha += alphaChangePerSecond * delta;

					// If timer expired, move to Possession_Delay state
					if (timer <= 0.0f) {
						state = PumpkinState.Possession_Delay;
						timer = MathUtils.random(0.1f, 3.0f);
						faceAlpha = 1.0f;
					}
					break;
				case Possession_Delay:
					// No special additional calculations

					// If timer expired, either become Possessed or move to Recovery
					if (timer <= 0.0f) {
						float possessionChance = 0.5f;
						if (MathUtils.random() <= possessionChance) {
							state = PumpkinState.Possessed;
							timer = 1.0f;
						} else {
							state = PumpkinState.Recovery;
							timer = MathUtils.random(1.0f, 5.0f);
							final float alphaTo = MathUtils.random(0.0f, 0.5f);
							alphaChangePerSecond = (alphaTo - 1.0f) / timer;
						}
					}
					break;
				case Recovery:
					// Update face alpha value (alphaChangePerSecond will be negative)
					faceAlpha += alphaChangePerSecond * delta;

					// If timer expired, move to Possession state
					if (timer <= 0.0f) {
						state = PumpkinState.Possession;
						timer = MathUtils.random(1.0f, 5.0f);
						alphaChangePerSecond = (1.0f - faceAlpha) / timer;
					}
					break;
				case Possessed:
					// No special additional calculations

					if (timer <= 0.0f) {
						// TODO: Decrement lives

						// TODO: Play sound: spirit escape, life lost

						// Move to spirit-released state
						state = PumpkinState.Spirit_Release;
						timer = 2.0f;

						// Release spirit
						final float x = getX() + getWidth() / 2;
						final float y = getY() + getHeight() / 2;
						final Spirit spirit = new Spirit(screen.atlas.findRegion("ghost"), x, y);
						screen.stage.addActor(spirit);
					}
					break;
				case Spirit_Release:
					// TODO
					if (timer <= 0.0f) {
						// Move to dormant state
						state = PumpkinState.Dormant;
						timer = MathUtils.random(1.0f, 3.0f);
					}
					break;
				default:
					throw new IllegalStateException();
				}
			}
		}

		@Override
		public void draw(SpriteBatch batch, float parentAlpha) {

			// Draw plant
			batch.draw(plant, getX() - 33, getY() - 43);

			// Draw the pumpkin
			batch.draw(pumpkin, getX(), getY());

			// Draw the face
			switch (state) {
			case Dormant:
				// No face visible
				break;
			case Possession:
			case Possession_Delay:
			case Recovery:
				// Normal face visible, potentially with alpha
				batch.setColor(1.0f, 1.0f, 1.0f, faceAlpha);
				batch.draw(face, getX(), getY());
				batch.setColor(Color.WHITE);
				break;
			case Possessed:
				// Evil face visible
				batch.draw(evilFace, getX(), getY());
				break;
			case Spirit_Release:
				// Hole visible
				// Know that timer in Spirit_Release state counts from 2.0f down to 0.0f
				// Calculate hole alpha from timer value
				batch.setColor(1.0f, 1.0f, 1.0f, Math.min(1.0f, timer));
				batch.draw(hole, getX(), getY());
				batch.setColor(Color.WHITE);
				break;
			default:
				throw new IllegalStateException();
			}
		}
	}

	/**
	 * Actor that represents a spirit escaping from a pumpkin.
	 * 
	 * @author Charlie
	 */
	static class Spirit extends Image {

		/** Constructor. */
		public Spirit(final TextureRegion spiritRegion, final float x, final float y) {
			super(spiritRegion);

			// Set origin so rotations work correctly
			setOrigin(getWidth() / 2, getHeight() / 2);

			// Start out at 1/8th scale
			setScale(0.125f);

			// Start out transparent
			setColor(1.0f, 1.0f, 1.0f, 0.0f);

			// Start out centred on the specified coordinates
			setPosition(x - getWidth() / 2, y - getHeight() / 2);

			// Over the next few seconds: Scale up to 4x, spin, fade in then rapidly fade out
			addAction(Actions.parallel(Actions.scaleTo(4.0f, 4.0f, 2.0f), Actions.rotateBy(360.0f * 4, 2.0f),
					Actions.fadeIn(1.0f), Actions.sequence(Actions.delay(1.0f), Actions.fadeOut(0.25f))));
		}

		@Override
		public Actor hit(final float x, final float y, final boolean touchable) {
			// Want to be able to click behind/through this Actor, so hit() should always return null
			return null;
		}
	}
}
