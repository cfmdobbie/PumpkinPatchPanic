package com.maycontainsoftware.pumpkinpatchpanic;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
		// FUTURE: Once Screen transitions are implemented, InputProcessor must be set in a different way
		Gdx.input.setInputProcessor(stage);
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

		// Moon

		game.moon = new MoonModel();
		Moon moon = new Moon(game.moon, atlas.findRegion("moon"));
		stage.addActor(moon);

		// Clouds

		game.clouds = new ArrayList<CloudModel>(2);

		final TextureRegion regionA = atlas.findRegion("cloud_a");
		final CloudModel modelA = new CloudModel(560, regionA.getRegionWidth());
		stage.addActor(new Cloud(regionA, modelA));
		game.clouds.add(modelA);

		final TextureRegion regionB = atlas.findRegion("cloud_b");
		final CloudModel modelB = new CloudModel(500, regionB.getRegionWidth());
		stage.addActor(new Cloud(regionB, modelB));
		game.clouds.add(modelB);

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

		// Owls
		// owl is 60x100

		game.owls = new ArrayList<OwlModel>();

		final OwlModel owl1 = new OwlModel();
		stage.addActor(new Owl(944, 504, atlas, owl1));
		game.owls.add(owl1);

		final OwlModel owl2 = new OwlModel();
		stage.addActor(new Owl(285, 528, atlas, owl2));
		game.owls.add(owl2);
	}
}
