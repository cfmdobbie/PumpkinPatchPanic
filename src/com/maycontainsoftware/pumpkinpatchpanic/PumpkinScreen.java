package com.maycontainsoftware.pumpkinpatchpanic;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

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

		stage = game.createStage();
		generateScenery(stage);

		// Redirect events to the stage
		// FUTURE: Once Screen transitions are implemented, InputProcessor must be set in a different way
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		stage.act();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		if (DEBUG) {
			Gdx.app.log(TAG, "resize(" + width + ", " + height + ")");
		}

		// Update Stage's viewport calculations
		game.updateViewport(stage);
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

		// Sky is 640x192
		final Image sky = new Image(atlas.findRegion("sky"));
		sky.setSize(640 * 2, 192 * 2);
		sky.setY(720 - 192 * 2);
		// FUTURE: Sky rotation - would be nice if the sky rotated very, very slowly. Would need much larger asset.
		// sky.addAction(Actions.forever(Actions.rotateBy(-360.0f, 10000.0f)));
		stage.addActor(sky);

		// Moon

		if (game.moon == null) {
			game.moon = new MoonModel();
		}
		Moon moon = new Moon(game.moon, atlas.findRegion("moon"));
		stage.addActor(moon);

		// Clouds

		if (game.clouds == null) {
			game.clouds = new ArrayList<CloudModel>(2);
			game.clouds.add(new CloudModel(560, atlas.findRegion("cloud_a").getRegionWidth(), "cloud_a"));
			game.clouds.add(new CloudModel(500, atlas.findRegion("cloud_b").getRegionWidth(), "cloud_b"));
		}

		for (final CloudModel model : game.clouds) {
			stage.addActor(new Cloud(model, atlas));
		}

		// Hillside is 640x207
		final Image hillside = new Image(atlas.findRegion("hillside"));
		hillside.setSize(640 * 2, 207 * 2);
		stage.addActor(hillside);

		// tree_left is 205x360
		final Image treeLeft = new Image(atlas.findRegion("tree_left"));
		treeLeft.setSize(205 * 2, 360 * 2);
		treeLeft.setPosition(0, 0);
		stage.addActor(treeLeft);

		// tree_right is 270x360
		final Image treeRight = new Image(atlas.findRegion("tree_right"));
		treeRight.setSize(270 * 2, 360 * 2);
		treeRight.setPosition(1280 - 270 * 2, 0);
		stage.addActor(treeRight);

		// Owls
		// owl is 60x100

		if (game.owls == null) {
			game.owls = new ArrayList<OwlModel>();

			final int[][] owlPositions = new int[][] { { 227, 233 }, { 155, 229 }, { 1060, 232 }, { 866, 138 },
					{ 1082, 310 }, };

			int[] position = owlPositions[MathUtils.random(owlPositions.length - 1)];

			game.owls.add(new OwlModel(position[0], 720 - position[1]));
		}

		for (final OwlModel model : game.owls) {
			stage.addActor(new Owl(model, game, atlas));
		}
	}
}
