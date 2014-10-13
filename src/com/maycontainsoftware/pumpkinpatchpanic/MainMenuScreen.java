package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class MainMenuScreen implements Screen {

	/** Whether debug output should be logged. */
	private static final boolean DEBUG = false;

	/** Tag, for logging purposes. */
	private static final String TAG = MainMenuScreen.class.getSimpleName();

	/** Reference to the Game instance. */
	private final PPPGame game;

	// ** This Screen's Stage. */
	// private Stage stage;

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
		// stage = game.createStage();

		// Table to lay out components
		// final Table table = new Table();
		// table.setFillParent(true);
		// table.defaults().pad(5.0f);
		// stage.addActor(table);

		// Blue gradient background
		// table.setBackground(new TextureRegionDrawable(atlas.findRegion("sky")));
	}

	@Override
	public void render(final float delta) {

		game.batch.begin();
		game.batch.draw(atlas.findRegion("sky"), 0, 0, 1280, 720);
		game.batch.draw(atlas.findRegion("moon"), 640 - 100 / 2, 600);
		game.batch.draw(atlas.findRegion("hillside"), 0, 0, 1280, 720);
		game.batch.draw(atlas.findRegion("tree_left"), 0, 0, 200 * 2, 360 * 2);
		game.batch.draw(atlas.findRegion("tree_right"), 1280 - 200 * 2, 0, 200 * 2, 360 * 2);
		for (int i = 0; i < 10; i++) {
			game.batch.draw(atlas.findRegion("leaf_litter"), 200 + i * (1280 - 200 * 2) / 10, 200);
		}
		game.batch.draw(atlas.findRegion("help"), 1280 / 2 - 300 - 150 / 2, 150, 150, 150);
		game.batch.draw(atlas.findRegion("play"), 1280 / 2 - 200 / 2, 100, 200, 200);
		game.batch.draw(atlas.findRegion("settings"), 1280 / 2 + 300 - 150 / 2, 150, 150, 150);
		game.batch.end();

		// Update and render the Stage
		// stage.act();
		// stage.draw();
	}

	@Override
	public void resize(final int width, final int height) {
		if (DEBUG) {
			Gdx.app.log(TAG, "resize(" + width + ", " + height + ")");
		}

		// Update Stage's viewport calculations
		// game.updateViewport(stage);
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
		// stage.dispose();
	}
}
