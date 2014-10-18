package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

/**
 * Game screen.
 * 
 * @author Charlie
 */
public class GameScreen extends PumpkinScreen {

	/** Whether debug output should be logged. */
	private static final boolean DEBUG = false;

	/** Tag, for logging purposes. */
	private static final String TAG = GameScreen.class.getSimpleName();

	/**
	 * Construct a new GameScreen.
	 * 
	 * @param game
	 *            The Game instance.
	 */
	public GameScreen(final PumpkinGame game) {
		super(game);
	}

	@Override
	public void show() {
		if (DEBUG) {
			Gdx.app.log(TAG, "show()");
		}

		super.show();

		// TODO: Create game elements

		// Pumpkins are 230x150

		final Image backLeft = new Image(atlas.findRegion("pumpkin"));
		backLeft.setPosition(480 - 230 / 2, 720 - 510);
		stage.addActor(getPlantForPumpkinButton(backLeft));
		stage.addActor(backLeft);
		stage.addActor(getFaceForPumpkinButton(backLeft));

		final Image backRight = new Image(atlas.findRegion("pumpkin"));
		backRight.setPosition(800 - 230 / 2, 720 - 510);
		stage.addActor(getPlantForPumpkinButton(backRight));
		stage.addActor(backRight);
		stage.addActor(getFaceForPumpkinButton(backRight));

		final Image frontLeft = new Image(atlas.findRegion("pumpkin"));
		frontLeft.setPosition(320 - 230 / 2, 720 - 670);
		stage.addActor(getPlantForPumpkinButton(frontLeft));
		stage.addActor(frontLeft);
		stage.addActor(getFaceForPumpkinButton(frontLeft));

		final Image frontMiddle = new Image(atlas.findRegion("pumpkin"));
		frontMiddle.setPosition(640 - 230 / 2, 720 - 670);
		stage.addActor(getPlantForPumpkinButton(frontMiddle));
		stage.addActor(frontMiddle);
		stage.addActor(getFaceForPumpkinButton(frontMiddle));

		final Image frontRight = new Image(atlas.findRegion("pumpkin"));
		frontRight.setPosition(960 - 230 / 2, 720 - 670);
		stage.addActor(getPlantForPumpkinButton(frontRight));
		stage.addActor(frontRight);
		stage.addActor(getFaceForPumpkinButton(frontRight));

		game.currentScreenCallback.notifyScreenVisible(ICurrentScreenCallback.Screen.GAME);
	}

	protected final Image getFaceForPumpkinButton(final Widget pumpkin) {
		final Image face = new Image(atlas.findRegion("face_normal"));
		face.setPosition(pumpkin.getX(), pumpkin.getY());
		return face;
	}
}
