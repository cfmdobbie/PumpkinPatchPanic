package com.maycontainsoftware.pumpkinpatchpanic;

import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * The Game instance.
 * 
 * @author Charlie
 */
public class PumpkinGame extends Game {

	/** Whether debug output should be logged. */
	private static final boolean DEBUG = false;

	/** A tag for logging purposes. */
	private static final String TAG = PumpkinGame.class.getSimpleName();

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
	// Skin skin;

	/** The callback used to notify host application code of the current state of the game. */
	final ICurrentScreenCallback currentScreenCallback;

	// Preferences

	/** Name of preferences file for state persistence. */
	private static final String PREFERENCES_NAME = "com.maycontainsoftware.pumpkinpatchpanic";

	/** Preferences file. */
	Preferences mPrefs;

	/** Background music. */
	Music music;

	/** Boolean value to track whether sounds are enabled or disabled. */
	boolean soundEnabled;

	// Scenery models

	List<OwlModel> owls;
	List<CloudModel> clouds;
	MoonModel moon;

	/**
	 * Construct a new PPPGame.
	 * 
	 * @param adVisibilityCallback
	 */
	public PumpkinGame(final ICurrentScreenCallback currentScreenCallback) {
		this.currentScreenCallback = currentScreenCallback;
	}

	@Override
	public void create() {

		// Set up SpriteBatch
		batch = new SpriteBatch();

		// Set up camera
		camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		// Move (0,0) point to bottom left of virtual area
		camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);

		// Create the AssetManager
		manager = new AssetManager();

		// Get reference to preferences file
		mPrefs = Gdx.app.getPreferences(PREFERENCES_NAME);

		// Start on the loading screen, which will load all assets then go to the main menu
		this.setScreen(new LoadingScreen(this));
	}

	@Override
	public void render() {

		// Don't scissor this clear operation
		Gdx.gl.glDisable(GL10.GL_SCISSOR_TEST);
		// Clear colour buffer to black
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// Update the camera
		camera.update();

		// Map rendered scene to centred viewport of correct aspect ratio
		Gdx.gl.glViewport((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);
		// Scissor buffer operations to the viewport
		Gdx.gl.glEnable(GL10.GL_SCISSOR_TEST);
		Gdx.gl.glScissor((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);

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

		// Allow superclass to dispose as required
		super.dispose();

		// Dispose of SpriteBatch if it's valid
		if (batch != null) {
			batch.dispose();
		}

		// Dispose of AssetManager if it's valid
		if (manager != null) {
			manager.dispose();
		}
	}

	/**
	 * Called when the display has resized and the stage needs to have its viewport calculations updated.
	 * 
	 * @param stage
	 *            The Stage to be updated
	 */
	public final void updateViewport(final Stage stage) {
		stage.setViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, false, viewport.x, viewport.y, viewport.width, viewport.height);
	}

	/**
	 * Utility method to allow the Game create a Stage on behalf of a Screen.
	 * 
	 * @return The new Stage
	 */
	public final Stage createStage() {
		return new Stage(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, false, batch);
	}

	/**
	 * Get the current highest round reached from preferences.
	 * 
	 * @return The current highest round that has been reached.
	 */
	public int getHighestRound() {
		return mPrefs.getInteger("highest_round", 0);
	}

	/**
	 * Set the highest round reached.
	 * 
	 * @param round
	 *            The new highest round.
	 */
	public void setHighestRound(int round) {
		mPrefs.putInteger("highest_round", round);
		mPrefs.flush();
	}

	/**
	 * Get whether or not music is enabled. (Setting comes from preferences.)
	 * 
	 * @return False if music is explicitly disabled, true otherwise.
	 */
	public boolean isMusicEnabled() {
		return mPrefs.getBoolean("music", true);
	}

	/**
	 * Set whether or not music is enabled.
	 * 
	 * @param enabled
	 *            True if music is enabled, false otherwise.
	 */
	public void setMusicEnabled(boolean enabled) {
		mPrefs.putBoolean("music", enabled);
		mPrefs.flush();
	}

	/** Prepare the background music for use. */
	private void prepareMusic() {
		music = manager.get("comeplaywithme.mp3", Music.class);
		music.setLooping(true);
	}

	void updateMusic() {
		if (music == null) {
			prepareMusic();
		}

		if (isMusicEnabled()) {
			// Music is enabled; if it's not playing, start it
			if (!music.isPlaying()) {
				music.play();
			}
		} else {
			// Music is not enabled; if it's playing, stop it
			if (music.isPlaying()) {
				music.stop();
			}
		}
	}

	/**
	 * Get whether or not sound is enabled. (Setting comes from preferences.)
	 * 
	 * @return False if sound is explicitly disabled, true otherwise.
	 */
	public boolean isSoundEnabled() {
		return mPrefs.getBoolean("sound", true);
	}

	/**
	 * Set whether or not sound is enabled.
	 * 
	 * @param enabled
	 *            True if sound is enabled, false otherwise.
	 */
	public void setSoundEnabled(boolean enabled) {
		mPrefs.putBoolean("sound", enabled);
		mPrefs.flush();
		soundEnabled = enabled;
	}

	/** Update the locally-stored sound setting. */
	void updateSound() {
		soundEnabled = isSoundEnabled();
	}

	void playSquark() {
		if (soundEnabled) {
			manager.get("squark.mp3", Sound.class).play();
		}
	}

	void playSpirit() {
		if (soundEnabled) {
			manager.get("spirit.mp3", Sound.class).play();
		}
	}

	void playThump() {
		if (soundEnabled) {
			manager.get("thump.mp3", Sound.class).play();
		}
	}
}
