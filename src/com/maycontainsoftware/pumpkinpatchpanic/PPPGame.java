package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class PPPGame extends Game {

	/** Whether debug output should be logged. */
	private static final boolean DEBUG = false;

	/** A tag for logging purposes. */
	private static final String TAG = PPPGame.class.getSimpleName();

	// Virtual screen metrics

	/** The width of the virtual render area. */
	public static final int VIRTUAL_WIDTH = 1280;

	/** The height of the virtual render area. */
	public static final int VIRTUAL_HEIGHT = 720;

	/** The aspect ratio of the virtual render area. */
	private static final float VIRTUAL_ASPECT_RATIO = (float) VIRTUAL_WIDTH / (float) VIRTUAL_HEIGHT;

	/**
	 * The app-global SpriteBatch. For performance reasons, a single SpriteBatch exists and is accessed from all Screens
	 * in the app,
	 */
	SpriteBatch batch;

	/** The app-global camera. This is used by all Screens. */
	OrthographicCamera camera;

	/** Rectangle that represents the glViewport. */
	final Rectangle viewport = new Rectangle();

	/** The asset manager used by the loading screen to load all assets not directly required by the loading screen. */
	AssetManager manager;

	/** The Scene2D UI skin instance. */
	Skin skin;

	private final IAdVisibilityCallback adVisibilityCallback;
	private TextureAtlas atlas;

	// Preferences

	/** Name of preferences file for state persistence. */
	private static final String PREFERENCES_NAME = "com.maycontainsoftware.pumpkinpatchpanic";

	/** Preferences file. */
	Preferences mPrefs;

	public PPPGame(IAdVisibilityCallback adVisibilityCallback) {
		this.adVisibilityCallback = adVisibilityCallback;
	}

	@Override
	public void create() {

		atlas = new TextureAtlas(Gdx.files.internal("atlas.atlas"));

		// Set up SpriteBatch
		batch = new SpriteBatch();

		// Set up camera
		camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		// Move (0,0) point to bottom left of virtual area
		camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);

		manager = new AssetManager();

		// Get reference to preferences file
		mPrefs = Gdx.app.getPreferences(PREFERENCES_NAME);

		this.setScreen(new LoadingScreen(this));
	}

	private boolean adVisible = true;

	@Override
	public void render() {

		if(Gdx.input.isTouched()) {
			adVisible = !adVisible;
			Gdx.app.log(TAG, "adVisible: " + adVisible);
			adVisibilityCallback.setAdVisible(adVisible);
		}
		
		// Clear colour buffer to black
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		// Don't scissor this clear operation
		Gdx.gl.glDisable(GL10.GL_SCISSOR_TEST);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// Update the camera
		camera.update();

		// Map rendered scene to centred viewport of correct aspect ratio
		Gdx.gl.glViewport((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);
		// Scissor buffer operations to the viewport
		Gdx.gl.glEnable(GL10.GL_SCISSOR_TEST);
		Gdx.gl.glScissor((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);

		// Reset SpriteBatch color to white
		batch.setColor(Color.WHITE);

		// Render background
		batch.begin();
		batch.draw(atlas.findRegion("bg"), 0, 0, 1280, 720);
		batch.draw(atlas.findRegion("tree_left"), 0, 0, 160 * 720.0f / 300, 720); // 160x300
		batch.draw(atlas.findRegion("tree_right"), 1280 - 210 * 720.0f / 300, 0, 210 * 720.0f / 300, 720); // 210x300
		
		batch.draw(atlas.findRegion("pumpkin"), 200, 100, 200, 160);
		batch.draw(atlas.findRegion("pumpkin"), 500, 200, 200, 160);
		batch.draw(atlas.findRegion("pumpkin_evil"), 800, 100, 200, 160);
		batch.draw(atlas.findRegion("moon"), 1280 / 2 - 100 / 2, 600);
		
		batch.end();

		// Pass render() call to active Screen
		super.render();
	}

	@Override
	public void resize(final int width, final int height) {

		if (DEBUG) {
			Gdx.app.log(TAG, "resize(" + width + ", " + height + ")");
		}

		// Calculate display aspect ratio
		final float displayAspectRatio = (float) width / (float) height;

		// Recalculate glViewport
		if (displayAspectRatio > VIRTUAL_ASPECT_RATIO) {
			// Display is wider than the game
			viewport.setSize(height * VIRTUAL_ASPECT_RATIO, height);
			viewport.setPosition((width - height * VIRTUAL_ASPECT_RATIO) / 2, 0);
		} else if (displayAspectRatio < VIRTUAL_ASPECT_RATIO) {
			// Display is taller than the game
			viewport.setSize(width, width / VIRTUAL_ASPECT_RATIO);
			viewport.setPosition(0, (height - width / VIRTUAL_ASPECT_RATIO) / 2);
		} else {
			// Display exactly matches game
			viewport.setSize(width, height);
			viewport.setPosition(0, 0);
		}

		// Pass resize() call to active Screen
		super.resize(width, height);
	}

	@Override
	public void dispose() {
		batch.dispose();
		atlas.dispose();

		super.dispose();
	}

	public final void updateViewport(final Stage stage) {
		stage.setViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, false, viewport.x, viewport.y, viewport.width, viewport.height);
	}

	public final Stage createStage() {
		return new Stage(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, false, batch);
	}
}
