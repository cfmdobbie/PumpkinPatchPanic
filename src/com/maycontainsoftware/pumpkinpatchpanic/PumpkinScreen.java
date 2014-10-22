package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * The base class for most screens in the game. The PumpkinScreen contains a Stage and renders the graphical elements
 * common to all screens
 * 
 * @author Charlie
 */
public class PumpkinScreen implements Screen {

	/** Whether debug output should be logged. */
	private static final boolean DEBUG = false;

	/** Tag, for logging purposes. */
	private static final String TAG = PumpkinScreen.class.getSimpleName();

	/** Reference to the Game instance. */
	protected final PumpkinGame game;

	/** The static Stage containing the scenery. */
	private static Stage sceneryStage;

	protected Stage stage;

	/** The TextureAtlas containing all the graphics. */
	protected TextureAtlas atlas;

	// FUTURE: Plan was for moon to traverse sky entirely each round, this would require access to the moon position
	// from some PumpkinScreen subclasses, while others need to have default behaviour. Whether or not this is worth the
	// sheer amount of code it requires is debatable!

	/**
	 * Construct a new PumpkinScreen object.
	 * 
	 * @param game
	 *            The game instance.
	 */
	public PumpkinScreen(final PumpkinGame game) {
		this.game = game;
	}

	@Override
	public void show() {
		if (DEBUG) {
			Gdx.app.log(TAG, "show()");
		}

		// Load the atlas
		atlas = game.manager.get("atlas.atlas", TextureAtlas.class);

		if (sceneryStage == null) {
			// Create the Stage
			sceneryStage = game.createStage();
			generateScenery(sceneryStage);
		}

		stage = game.createStage();
		// Redirect events to the stage
		Gdx.input.setInputProcessor(stage);
		// TODO: Once Screen transitions are implemented, InputProcessor must be set in a different way
	}

	@Override
	public void render(float delta) {
		if (sceneryStage != null) {
			// Update and render the Stage
			sceneryStage.act();
			sceneryStage.draw();
		}

		stage.act();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		if (DEBUG) {
			Gdx.app.log(TAG, "resize(" + width + ", " + height + ")");
		}

		if (sceneryStage != null) {
			// Update Stage's viewport calculations
			game.updateViewport(sceneryStage);
		}
	}

	@Override
	public void hide() {
		if (DEBUG) {
			Gdx.app.log(TAG, "hide()");
		}
	}

	@Override
	public void pause() {
		if (DEBUG) {
			Gdx.app.log(TAG, "pause()");
		}
	}

	@Override
	public void resume() {
		if (DEBUG) {
			Gdx.app.log(TAG, "resume()");
		}
	}

	@Override
	public void dispose() {
		if (DEBUG) {
			Gdx.app.log(TAG, "dispose()");
		}

		// Don't dispose scenery stage - it is static and shared between all PumpkinScreens

		// Dispose of this screen's stage
		stage.dispose();
	}

	protected final Image getPlantForPumpkinButton(final Actor button) {
		final Image plant = new Image(atlas.findRegion("plant"));
		plant.setPosition(button.getX() - 33, button.getY() - 43);
		return plant;
	}

	private final void generateScenery(final Stage stage) {
		// Sky is 640x201
		final Image sky = new Image(atlas.findRegion("sky"));
		sky.setSize(640 * 2, 201 * 2);
		sky.setY(720 - 201 * 2);
		// FUTURE: Sky rotation - would be nice if the sky rotated very, very slowly. Would need much larger asset.
		// sky.addAction(Actions.forever(Actions.rotateBy(-360.0f, 10000.0f)));
		stage.addActor(sky);

		/*
		 * Theory of moon movement calculations:-
		 * 
		 * Want moon to rise to exactly half off the top of the screen. Want origin and destination points to be just
		 * off the sides of the screen, at 50% of the height of the screen. Can calculate coordinates of starting and
		 * highest positions. Can calculate slope of line between these. Can calculate midpoint of that line, and
		 * negative reciprocal of the slope. This gives a line with known slope and one known coordinate passing through
		 * the centre of the circle that joins the three moon positions.
		 * 
		 * Zenith: (640, 720) ~ Left position: (-55, 360) ~ Midpoint: (292, 540)
		 * 
		 * Slope: y2-y1/x2-x1 = 0.518 ~ Perpendicular slope: -1.9305
		 * 
		 * Line: y = m * x + c, 540 = -1.9305 * 292 + c, c = 1103.72
		 * 
		 * Centre of circle: y = m * x + c, y = -1.9305 * 640 + 1103.72, y = -131.83
		 */

		final Image moon = new Image(atlas.findRegion("moon"));
		moon.setPosition(640 - 110 / 2, 720 - 110 / 2);
		moon.setOrigin(110 / 2, 110 / 2 - 720 - 132);

		/*
		 * Old calculations to determine visible arc of moon movement:
		 * 
		 * Want the moon to start its journey just off the left edge of the screen, so need to know initial rotation
		 * required. Calculate by trig based on starting location and centre of circle, given previously-calculated
		 * distance between them.
		 * 
		 * sin A = o / h = (640 + 55) / (720 + 131.83) = 695 / 851.83 = 0.816
		 * 
		 * A = sin-1 0.816 = 54.68 degrees
		 * 
		 * (Note that we currently don't use this value!)
		 */

		// Want moon to start at far left horizon (not visible) so a delay exists before it appears
		moon.rotate(90.0f);

		/*
		 * Rotation speed. Want the moon to traverse from left to right over 60 seconds. Now know the total angle
		 * covered, so need to calculate rotation time for a full 360-degree rotation.
		 * 
		 * 60-second arc: 54.68 * 2 = 109.35 degrees
		 * 
		 * 109.35 / 60 = 360 / t, t = 360 * 60 / 109.35 = 197.53 seconds
		 */

		moon.addAction(Actions.forever(Actions.rotateBy(-360.0f, 197.53f)));

		stage.addActor(moon);

		// Clouds
		stage.addActor(new Cloud(atlas.findRegion("cloud_a"), 560));
		stage.addActor(new Cloud(atlas.findRegion("cloud_b"), 500));

		// Hillside is 640x225
		final Image hillside = new Image(atlas.findRegion("hillside"));
		hillside.setSize(640 * 2, 225 * 2);
		stage.addActor(hillside);

		// tree_left is 238x360
		final Image treeLeft = new Image(atlas.findRegion("tree_left"));
		treeLeft.setSize(238 * 2, 360 * 2);
		treeLeft.setPosition(0, 0);
		stage.addActor(treeLeft);

		// tree_right is 203x360
		final Image treeRight = new Image(atlas.findRegion("tree_right"));
		treeRight.setSize(203 * 2, 360 * 2);
		treeRight.setPosition(1280 - 203 * 2, 0);
		stage.addActor(treeRight);

		// owl is 60x100
		stage.addActor(new Owl(944, 504, atlas));
		stage.addActor(new Owl(285, 528, atlas));
	}

	/** Actor to represent the owl. */
	static class Owl extends Image {

		// Drawables for the different owl graphics
		private final TextureRegionDrawable eyesDown;
		private final TextureRegionDrawable eyesLeft;
		private final TextureRegionDrawable eyesRight;
		private final TextureRegionDrawable blink;

		private float timeSinceEyeChange = 0.0f;
		private float timeToEyeChange;
		private TextureRegionDrawable currentEyes;

		private float timeSinceBlinkChange = 0.0f;
		private float timeToBlinkChange;
		private boolean eyesOpen = true;

		public Owl(final float x, final float y, final TextureAtlas atlas) {
			// Call super with a graphic - mainly just to set up size for us
			super(atlas.findRegion("owl_down"));

			// Load owl frames
			eyesDown = new TextureRegionDrawable(atlas.findRegion("owl_down"));
			eyesLeft = new TextureRegionDrawable(atlas.findRegion("owl_left"));
			eyesRight = new TextureRegionDrawable(atlas.findRegion("owl_right"));
			blink = new TextureRegionDrawable(atlas.findRegion("owl_blink"));

			// Start off looking down
			currentEyes = eyesDown;
			timeToEyeChange = MathUtils.random(0.5f, 4.0f);

			// And not blinking
			timeToBlinkChange = MathUtils.random(3.0f, 6.0f);

			setPosition(x, y);

			addAction(new OwlAction());

			// Want owl to respond to touch - owl should blink when poked
			// However, as we have two stages and a single InputProcessor, this does not work
			// FUTURE: Fix owl's touch input
			addListener(new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					// Owls blink when touched
					setDrawable(blink);
					timeToBlinkChange = MathUtils.random(0.5f, 0.75f);
					timeSinceBlinkChange = 0.0f;
					return true;
				}
			});
		}

		/** Action to animate an owl. */
		class OwlAction extends Action {
			@Override
			public boolean act(float delta) {

				// Eye change code

				timeSinceEyeChange += delta;

				if (timeSinceEyeChange > timeToEyeChange) {
					// Need to change eyes
					if (currentEyes == eyesDown) {
						currentEyes = MathUtils.randomBoolean() ? eyesLeft : eyesRight;
					} else if (currentEyes == eyesLeft) {
						currentEyes = MathUtils.randomBoolean() ? eyesDown : eyesRight;
					} else {
						currentEyes = MathUtils.randomBoolean() ? eyesLeft : eyesDown;
					}

					timeSinceEyeChange = 0.0f;
					timeToEyeChange = MathUtils.random(0.5f, 4.0f);

					if (eyesOpen) {
						setDrawable(currentEyes);
					}
				}

				// Blink code

				timeSinceBlinkChange += delta;

				if (timeSinceBlinkChange >= timeToBlinkChange) {
					if (eyesOpen) {
						// Need to blink
						setDrawable(blink);
						timeToBlinkChange = MathUtils.random(0.25f);
						timeSinceBlinkChange = 0.0f;
					} else {
						// Need to stop blinking
						setDrawable(currentEyes);
						timeToBlinkChange = MathUtils.random(3.0f, 6.0f);
						timeSinceBlinkChange = 0.0f;
					}
					eyesOpen = !eyesOpen;
				}

				return false;
			}
		}
	}

	/** Actor that represents a cloud. */
	static class Cloud extends Image {

		/** The width of the TextureRegion. */
		private final int width;

		/** The cloud's y-coordinate (fixed in constructor.) */
		private final float y;

		/** The cloud's x-coordinate (updated in CloudAction.) */
		private float x;

		/** The cloud's current velocity, in the form of x-coordinate change in pixels/second. */
		private float dx;

		/** Minimum cloud speed. */
		private static final float MIN_SPEED = 10;

		/** Maximum cloud speed. */
		private static final float MAX_SPEED = 40;

		/** Constructor. */
		public Cloud(final TextureRegion region, float y) {
			super(region);

			// Clouds are semi-transparent
			setColor(1.0f, 1.0f, 1.0f, 0.9f);

			// Determine cloud dimensions
			width = region.getRegionWidth();

			// Remember y coordinate
			this.y = y;

			// Initial speed
			dx = -MathUtils.random(MIN_SPEED, MAX_SPEED);

			// Initial position
			this.x = MathUtils.random(-width, 1280.0f + -dx * 10);

			// Movement action
			addAction(new CloudAction());
		}

		/** Action to update cloud position and recalculate speed and position after it leaves the screen. */
		class CloudAction extends Action {
			@Override
			public boolean act(float delta) {
				if (x < -width) {
					// Cloud is off-screen, recalculate speed, then reposition
					dx = -MathUtils.random(MIN_SPEED, MAX_SPEED);
					// Move cloud beyond right edge, with 1-10 seconds before it reappears
					x = 1280.0f + MathUtils.random(-dx, -dx * 10);
				}

				// Move cloud
				x += dx * delta;
				Cloud.this.setPosition(x, y);

				return false;
			}
		}
	}
}
