package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * The first displayed screen, which is non-interactive and exists solely to load assets in the background without
 * blocking the UI thread.
 * 
 * @author Charlie
 */
public class LoadingScreen implements Screen {

	/** Whether debug output should be logged. */
	private static final boolean DEBUG = false;

	/** Tag, for logging purposes. */
	private static final String TAG = LoadingScreen.class.getSimpleName();

	/** Reference to the Game instance. */
	private final PumpkinGame game;

	/** This Screen's Stage. */
	private Stage stage;

	/**
	 * Construct a new LoadingScreen object.
	 * 
	 * @param game
	 */
	public LoadingScreen(final PumpkinGame game) {
		this.game = game;
	}

	@Override
	public void show() {
		if (DEBUG) {
			Gdx.app.log(TAG, "show()");
		}

		// Load any assets required for the loading screen
		game.manager.load("loading.atlas", TextureAtlas.class);
		game.manager.finishLoading();
		TextureAtlas loadingAtlas = game.manager.get("loading.atlas", TextureAtlas.class);

		// Create the Stage
		stage = game.createStage();

		// Table to lay out components
		final Table table = new Table();
		table.setFillParent(true);
		table.defaults().pad(5.0f);
		stage.addActor(table);

		// Blue gradient background
		table.setBackground(new TextureRegionDrawable(loadingAtlas.findRegion("loading_bg")));

		// Text saying "Loading"
		table.add(new Image(loadingAtlas.findRegion("loading_text")));

		// Loading progress bar
		table.row();
		table.add(new LoadingBar(game.manager, loadingAtlas.findRegion("loading_bar_bg"), loadingAtlas
				.findRegion("loading_bar_fg")));

		// Load assets in AssetManager

		// Texture atlases
		game.manager.load("atlas.atlas", TextureAtlas.class);
		game.manager.load("pumpkins.atlas", TextureAtlas.class);

		// Textures
		game.manager.load("help_panel_1.png", Texture.class);
		game.manager.load("help_panel_2.png", Texture.class);
		game.manager.load("help_panel_3.png", Texture.class);
		game.manager.load("help_panel_4.png", Texture.class);

		// Fonts
		game.manager.load("arialb_32.fnt", BitmapFont.class);
		game.manager.load("arialb_64.fnt", BitmapFont.class);

		// Sound effects
		// TODO: Sound effects
		// game.manager.load("lose.mp3", Sound.class);

		// Music
		game.manager.load("comeplaywithme.mp3", Music.class);

		game.currentScreenCallback.notifyScreenVisible(ICurrentScreenCallback.Screen.LOADING);
	}

	@Override
	public void render(final float delta) {

		// Update and render the Stage
		stage.act();
		stage.draw();

		// Continue to load assets
		if (game.manager.update()) {
			// Assets have been loaded!
			if (DEBUG) {
				Gdx.app.log(TAG, "game.manager.update() = true");
			}

			// Open main menu
			game.setScreen(new MainMenuScreen(game));
			this.dispose();

			// Start music
			// TODO: Need control over music, this needs to be somewhere else
			final Music music = game.manager.get("comeplaywithme.mp3", Music.class);
			music.setLooping(true);
			music.play();
		}
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

	/**
	 * The widget that displays the loading bar.
	 * 
	 * @author Charlie
	 */
	static class LoadingBar extends Image {

		final TextureRegion foreground;
		final float offsetX;
		final float offsetY;
		final float minU;
		final float widthU;
		final AssetManager manager;

		public LoadingBar(final AssetManager manager, final TextureRegion background, final TextureRegion foreground) {
			super(background);

			this.manager = manager;

			// Copy the TextureRegion as we're going to mess with its U2 coord
			this.foreground = new TextureRegion(foreground);
			minU = foreground.getU();
			widthU = foreground.getU2() - minU;

			// Calculate offset of foreground wrt background
			offsetX = (background.getRegionWidth() - foreground.getRegionWidth()) / 2.0f;
			offsetY = (background.getRegionHeight() - foreground.getRegionHeight()) / 2.0f;
		}

		@Override
		public void draw(final SpriteBatch batch, final float parentAlpha) {

			// Draw the background
			super.draw(batch, parentAlpha);

			// Update the portion of the foreground bar to display
			foreground.setU2(minU + widthU * manager.getProgress());

			// Draw the foreground bar
			batch.draw(foreground, getX() + offsetX, getY() + offsetY);
		}
	}
}
