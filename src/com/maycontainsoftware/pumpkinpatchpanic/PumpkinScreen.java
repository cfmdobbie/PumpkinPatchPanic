package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

public class PumpkinScreen implements Screen {

	/** Whether debug output should be logged. */
	private static final boolean DEBUG = false;

	/** Tag, for logging purposes. */
	private static final String TAG = PumpkinScreen.class.getSimpleName();

	/** Reference to the Game instance. */
	protected final PPPGame game;

	/** This Screen's Stage. */
	protected Stage stage;

	public PumpkinScreen(final PPPGame game) {
		this.game = game;
	}

	@Override
	public void show() {
		if (DEBUG) {
			Gdx.app.log(TAG, "show()");
		}
		
		// Create the Stage
		stage = game.createStage();
		
		// Redirect events to the stage
		Gdx.input.setInputProcessor(stage);

		// Load the atlas
		final TextureAtlas atlas = game.manager.get("main_menu.atlas", TextureAtlas.class);
		
		// Sky is 640x201
		final Image sky = new Image(atlas.findRegion("sky"));
		sky.setSize(640 * 2, 201 * 2);
		sky.setY(720 - 201 * 2);
		stage.addActor(sky);

		final Image moon = new Image(atlas.findRegion("moon"));
		moon.setPosition(-110 / 2, -110 / 2);
		moon.setOrigin(110 / 2 + 1280 / 2, 110 / 2);
		// TODO: Would be better if moon orientation were fixed - use move actions instead with Sine interpolation?
		moon.addAction(Actions.forever(Actions.rotateBy(-360, 120.0f)));
		stage.addActor(moon);
		
		// CloudA is 360x108
		final Image cloudA = new Image(atlas.findRegion("cloud_a"));
		cloudA.setPosition(250, 500);
		stage.addActor(cloudA);
		
		// CloudB is 360x120
		final Image cloudB = new Image(atlas.findRegion("cloud_b"));
		cloudB.setPosition(900, 560);
		stage.addActor(cloudB);
		
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
		final Array<AtlasRegion> owlFrames = atlas.findRegions("owl");
		final Animation owlAnimation = new Animation(2.0f, owlFrames, Animation.LOOP);
		// TODO: Animation code
		final AnimatedActor owl = new AnimatedActor(owlAnimation);
		owl.setPosition(944, 504);
		stage.addActor(owl);
	}

	@Override
	public void render(float delta) {
		// Update and render the Stage
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

		stage.dispose();
	}
}
