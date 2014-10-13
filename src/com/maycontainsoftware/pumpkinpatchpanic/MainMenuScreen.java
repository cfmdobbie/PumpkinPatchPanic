package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class MainMenuScreen implements Screen {

	/** Whether debug output should be logged. */
	private static final boolean DEBUG = false;

	/** Tag, for logging purposes. */
	private static final String TAG = MainMenuScreen.class.getSimpleName();

	/** Reference to the Game instance. */
	private final PPPGame game;

	/** This Screen's Stage. */
	private Stage stage;

	TextureAtlas atlas;

	public MainMenuScreen(final PPPGame game) {
		this.game = game;
	}

	@Override
	public void show() {
		if (DEBUG) {
			Gdx.app.log(TAG, "show()");
		}

		// Load the atlas for this screen
		atlas = game.manager.get("main_menu.atlas", TextureAtlas.class);

		// Create the Stage
		stage = game.createStage();

		// Table to lay out components
		// final Table table = new Table();
		// table.setFillParent(true);
		// table.defaults().pad(5.0f);
		// stage.addActor(table);

		// Blue gradient background
		// table.setBackground(new TextureRegionDrawable(atlas.findRegion("sky")));

		final Image sky = new Image(atlas.findRegion("sky"));
		sky.setSize(1280, 720);
		stage.addActor(sky);

		final Image moon = new Image(atlas.findRegion("moon"));
		moon.setPosition(-50, -50);
		moon.setOrigin(50 + 1280 / 2, 50);
		// TODO: Would be better if moon orientation were fixed - use move actions instead with Sine interpolation?
		moon.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.rotateBy(-360, 10.0f)));
		stage.addActor(moon);

		final Image hillside = new Image(atlas.findRegion("hillside"));
		hillside.setSize(1280, 720);
		stage.addActor(hillside);

		// tree_left is 360x200
		final Image treeLeft = new Image(atlas.findRegion("tree_left"));
		treeLeft.setSize(400, 720);
		treeLeft.setPosition(-400, 0);
		treeLeft.addAction(Actions.moveTo(0, 0, 0.5f));
		stage.addActor(treeLeft);

		// tree_right is 360x200
		final Image treeRight = new Image(atlas.findRegion("tree_right"));
		treeRight.setSize(400, 720);
		treeRight.setPosition(1280, 0);
		treeRight.addAction(Actions.moveTo(1280 - 400, 0, 0.5f));
		stage.addActor(treeRight);

		// Leaf litter
		for (int i = 0; i < 100; i++) {
			final Image leafLitter = new Image(atlas.findRegion("leaf_litter"));
			final int x = MathUtils.random(100, (1280 - 200));
			final int y = MathUtils.random(50, 300);
			leafLitter.setPosition(x, y);
			//leafLitter.setZIndex(y);
			stage.addActor(leafLitter);
		}
		
		final Image help = new Image(atlas.findRegion("help"));
		help.setSize(150, 150);
		help.setPosition(1280 / 2 - 300 - 150 / 2, 150);
		//help.setZIndex(50);
		stage.addActor(help);
		
		final Image play = new Image(atlas.findRegion("play"));
		play.setSize(200, 200);
		play.setPosition(1280 / 2 - 200 / 2, 100);
		//play.setZIndex(50);
		stage.addActor(play);
		
		final Image settings = new Image(atlas.findRegion("settings"));
		settings.setSize(150, 150);
		settings.setPosition(1280 / 2 + 300 - 150 / 2, 150);
		//settings.setZIndex(50);
		stage.addActor(settings);
	}

	@Override
	public void render(final float delta) {

		// Update and render the Stage
		stage.act();
		stage.draw();
	}

	@Override
	public void resize(final int width, final int height) {
		if (DEBUG) {
			Gdx.app.log(TAG, "resize(" + width + ", " + height + ")");
		}

		// Update Stage's viewport calculations
		game.updateViewport(stage);
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		stage.dispose();
	}
}