package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Actor that represents a haunted pumpkin. During the game, a pumpkin goes through a number of stages:
 * 
 * 1) Initial delay. Initially, the pumpkin is dormant - a random delay exists before any other action takes place.
 * 
 * 2) Possession. Spirit begins to enter the pumpkin. Carved face fades in (fully) over random period of time.
 * 
 * 3) Possession Delay. Once face is completely visible, a random delay exists before either the pumpkin recovers, or it
 * becomes possessed.
 * 
 * 4a) Recovery. Carved face fades away to a partially-visible state over a random period of time, then continues
 * Possession.
 * 
 * 4b) Possessed. Evil face appears. In this state, player can tap pumpkin to exorcise it, returning it to the initial
 * state. If left in Possessed state for a fixed period of time, the spirit escapes.
 * 
 * 5) Spirit Release. The spirit escapes from the pumpkin. The carved face disappears altogether, the pumpkin reverts to
 * the initial state and the player loses a life.
 * 
 * @author Charlie
 */
class PumpkinActor extends Actor {

	/** The pumpkin texture. */
	final TextureRegion pumpkin;

	/** The foliage texture. */
	final TextureRegion plant;

	/** The normal face texture. */
	TextureRegion face;

	/** The evil face texture. */
	TextureRegion evilFace;

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
	private PumpkinActor.PumpkinState state;

	/** Temporary variable used for meausing time left in current state. */
	private float timer;

	/** Current alpha value of face graphic. */
	private float faceAlpha;

	/** Change in face alpha value, per second. Note this can be (and frequently is) negative. */
	private float alphaChangePerSecond;

	/** Hiss sound, made by a pumpkin in the Possessed state. */
	private final Sound hiss;

	/** Id of the currently-playing hiss sound. */
	private long hissId;

	/** Construct a PumpkinActor. */
	public PumpkinActor(final GameScreen screen) {

		// Store reference to the screen
		this.screen = screen;

		// Load textures required for rendering
		plant = screen.atlas.findRegion("plant");
		pumpkin = screen.atlas.findRegion("pumpkin");
		hole = screen.atlas.findRegion("hole");
		// Note: face and evilFace are set in resetToDormant();

		// For collision-detection reasons, the size of the Actor is the size of just the pumpkin
		setWidth(pumpkin.getRegionWidth());
		setHeight(pumpkin.getRegionHeight());

		// Get reference to sound file
		hiss = screen.game.manager.get("hiss.mp3", Sound.class);

		// Initial state
		resetToDormant();

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
						if (GameScreen.DEBUG) {
							Gdx.app.log(GameScreen.TAG, "Hit too early");
						}

						// TODO: Play sound: error, life lost

						// Decrement lives
						screen.livesLeft--;
						screen.hud.updateLives();

						// Check for game over condition
						if (screen.livesLeft <= 0) {
							// Open game-over dialog
							screen.dialog = screen.createGameOverDialog();
							screen.stage.addActor(screen.dialog);
						}

						// Move to dormant state
						resetToDormant();

						break;
					case Possessed:
						// Correct timing
						if (GameScreen.DEBUG) {
							Gdx.app.log(GameScreen.TAG, "Correct hit");
						}

						// TODO: Play sound: good hit, exorcised

						hiss.stop(hissId);

						// Move to dormant state
						resetToDormant();

						break;
					case Spirit_Release:
						// Too late
						if (GameScreen.DEBUG) {
							Gdx.app.log(GameScreen.TAG, "Hit too late");
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

					// Play the "interaction" sound effect
					screen.game.playThump();

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

			// Additional processing
			switch (state) {
			case Dormant:
			case Possession_Delay:
			case Possessed:
			case Spirit_Release:
				// No additional processing
				break;
			case Possession:
				// Update face alpha value (alphaChangePerSecond will be positive)
				faceAlpha += alphaChangePerSecond * delta;
				break;
			case Recovery:
				// Update face alpha value (alphaChangePerSecond will be negative)
				faceAlpha += alphaChangePerSecond * delta;
				break;
			default:
				throw new IllegalStateException();
			}

			// Timer expiration
			if (timer <= 0.0f) {
				switch (state) {
				case Dormant:
					// If timer expired, move to Possession state
					state = PumpkinState.Possession;
					timer = Difficulty.getPossessionTime(screen.currentRound);
					alphaChangePerSecond = (1.0f - 0.0f) / timer;
					faceAlpha = 0.0f;
					break;
				case Possession:
					// If timer expired, move to Possession_Delay state
					state = PumpkinState.Possession_Delay;
					timer = Difficulty.getPossessionDelay(screen.currentRound);
					faceAlpha = 1.0f;
					break;
				case Possession_Delay:
					// If timer expired, either become Possessed or move to Recovery
					if (Difficulty.getRecoveryChance(screen.currentRound)) {
						// Proceed to Recovery
						state = PumpkinState.Recovery;
						timer = Difficulty.getRecoveryTime(screen.currentRound);
						final float alphaTo = MathUtils.random(0.0f, 0.5f);
						alphaChangePerSecond = (alphaTo - 1.0f) / timer;
					} else {
						// Proceed to Possessed
						state = PumpkinState.Possessed;
						timer = Difficulty.getPossessedTime(screen.currentRound);

						// Play hiss sound
						hissId = hiss.play();
					}
					break;
				case Recovery:
					// If timer expired, move to Possession state
					state = PumpkinState.Possession;
					timer = Difficulty.getPossessionTime(screen.currentRound);

					alphaChangePerSecond = (1.0f - faceAlpha) / timer;
					break;
				case Possessed:
					// Decrement lives
					screen.livesLeft--;
					screen.hud.updateLives();

					// TODO: Play sound: spirit escape, life lost

					// Check for game over condition
					if (screen.livesLeft <= 0) {
						// Open game-over dialog
						screen.dialog = screen.createGameOverDialog();
						screen.stage.addActor(screen.dialog);
					}

					// Move to spirit-released state
					state = PumpkinState.Spirit_Release;
					timer = 2.0f;

					// Release spirit
					final float x = getX() + getWidth() / 2;
					final float y = getY() + getHeight() / 2;
					final Spirit spirit = new Spirit(screen.atlas.findRegion("ghost"), x, y);
					screen.stage.addActor(spirit);

					// Stop hissing
					hiss.stop(hissId);

					// Play sound effect
					screen.game.playSpirit();

					break;
				case Spirit_Release:
					// Move to dormant state
					resetToDormant();
					break;
				default:
					throw new IllegalStateException();
				}
			}
		}
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {

		// Draw plant
		batch.draw(plant, getX() - 35, getY() - 25);

		// Pumpkin shakes slightly as it becomes possessed. Shaking increases in strength as the carved face fades in,
		// and increases in strength and speed when it becomes possessed.

		final float amplitude;
		final float normalizedAngle;

		switch (state) {
		case Dormant:
		case Spirit_Release:
			amplitude = 0.0f;
			normalizedAngle = 0.0f;
			break;
		case Possession:
		case Possession_Delay:
		case Recovery:
			amplitude = 1.0f * faceAlpha;
			normalizedAngle = MathUtils.sinDeg(2 * TimeUtils.millis() % 360);
			break;
		case Possessed:
			amplitude = 3.0f;
			normalizedAngle = MathUtils.sinDeg(4 * TimeUtils.millis() % 360);
			break;
		default:
			throw new IllegalStateException();
		}

		final float angle = normalizedAngle * amplitude;

		// Draw the pumpkin
		batch.draw(pumpkin, getX(), getY(), getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), getScaleX(),
				getScaleY(), angle);

		// Draw the detail texture
		switch (state) {
		case Dormant:
			// No face visible
			break;
		case Possession:
		case Possession_Delay:
		case Recovery:
			// Normal face visible, potentially with alpha
			// Face is always 170x130
			batch.setColor(1.0f, 1.0f, 1.0f, faceAlpha);
			batch.draw(face, getX() + 15, getY() + 10, 170 / 2, 130 / 2, 170, 130, getScaleX(), getScaleY(), angle);
			batch.setColor(Color.WHITE);
			break;
		case Possessed:
			// Evil face visible
			// Face is always 170x130
			batch.draw(evilFace, getX() + 15, getY() + 10, 170 / 2, 130 / 2, 170, 130, getScaleX(), getScaleY(), angle);
			break;
		case Spirit_Release:
			// Hole visible
			// Know that timer in Spirit_Release state counts from 2.0f down to 0.0f
			// Calculate hole alpha from timer value
			batch.setColor(1.0f, 1.0f, 1.0f, Math.min(1.0f, timer));
			batch.draw(hole, getX() + 15, getY() + 10, 170 / 2, 130 / 2, 170, 130, getScaleX(), getScaleY(), angle);
			batch.setColor(Color.WHITE);
			break;
		default:
			throw new IllegalStateException();
		}
	}

	/** Reset the pumpkin to initial state. Used primarily at the start of each round. */
	public void resetToDormant() {
		state = PumpkinState.Dormant;
		timer = Difficulty.getDormantTime(screen.currentRound);
		faceAlpha = 0.0f;

		// Pick a random face, which might be the same one as currently displayed
		final int faceNumber = MathUtils.random(1, 9);
		face = screen.atlas.findRegion("face" + faceNumber);
		evilFace = screen.atlas.findRegion("face" + faceNumber + "_evil");
	}

	/**
	 * Class to calculate game constants in such a way that difficulty increases with each new round.
	 * 
	 * Basic of calculations is an exponential decay, which is of the form:
	 * 
	 * y = initial * e^(-lambda * x)
	 * 
	 * Which we can simplify as follows:
	 * 
	 * a) Set initial = 1, so values are all clamped in range (0, 1]
	 * 
	 * c) Set lambda s.t. the output values decay to ~10% of 0 after a suitable expected round cap. A lambda of 0.1
	 * makes the output value 10% at 23 rounds, 5% at 30 rounds.
	 * 
	 * @author Charlie
	 */
	static class Difficulty {

		private static final float decay(int round) {
			return (float) Math.pow(Math.E, -0.1 * round);
		}

		static float getDormantTime(int round) {
			return MathUtils.random(1.0f, 1.5f + decay(round) * 2.5f);
		}

		static float getPossessionTime(int round) {
			return MathUtils.random(1.0f, 1.0f + decay(round) * 3.0f);
		}

		static float getPossessionDelay(int round) {
			return MathUtils.random(0.1f, 0.5f + decay(round) * 1.5f);
		}

		static boolean getRecoveryChance(int round) {
			float chance = decay(round) * 0.8f;
			return MathUtils.randomBoolean(chance);
		}

		static float getRecoveryTime(int round) {
			return MathUtils.random(0.5f, 1.0f + decay(round) * 3.0f);
		}

		static float getPossessedTime(int round) {
			return 0.5f + decay(round) * 1.0f;
		}
	}
}
