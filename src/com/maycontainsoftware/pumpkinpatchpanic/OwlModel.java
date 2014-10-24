package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.math.MathUtils;

/**
 * OwlModel represents an Owl's logical data model.
 * 
 * @author Charlie
 */
class OwlModel {

	/** Actor's x-coordinate. */
	final float x;

	/** Actor's y-coordinate. */
	final float y;

	/** Enumeration of eye direction. */
	static enum EyeDirection {
		DOWN,
		LEFT,
		RIGHT,
	}

	/** Current eye direction. */
	EyeDirection eyeDirection;

	/** Time since the the last eye change. */
	float timeSinceEyeChange;

	/** Time until the next eye change. */
	float timeToEyeChange;

	/** Whether or not the eyes are currently open. */
	boolean eyesOpen;

	/** Whether or not owl has just been poked. */
	boolean poked;

	/** Time since the last blink change. */
	float timeSinceBlinkChange;

	/** Time until the next blink change. */
	float timeToBlinkChange;

	/** Construct a new model. */
	public OwlModel(final float x, final float y) {

		// Remember coordinates
		this.x = x;
		this.y = y;

		// Start off looking down
		eyeDirection = EyeDirection.DOWN;
		timeSinceEyeChange = 0.0f;
		timeToEyeChange = MathUtils.random(0.5f, 4.0f);

		// And not blinking
		eyesOpen = true;
		timeSinceBlinkChange = 0.0f;
		timeToBlinkChange = MathUtils.random(3.0f, 6.0f);

		// And not poked
		poked = false;
	}

	public void pickNewEyeDirection() {
		switch (eyeDirection) {
		case DOWN:
			eyeDirection = MathUtils.randomBoolean() ? EyeDirection.LEFT : EyeDirection.RIGHT;
			break;
		case LEFT:
			eyeDirection = MathUtils.randomBoolean() ? EyeDirection.DOWN : EyeDirection.RIGHT;
			break;
		case RIGHT:
			eyeDirection = MathUtils.randomBoolean() ? EyeDirection.DOWN : EyeDirection.LEFT;
			break;
		}
	}

	public void update(final float delta) {
		timeSinceEyeChange += delta;
		timeSinceBlinkChange += delta;
	}

	public void resetEyeChangeTimer() {
		timeSinceEyeChange = 0.0f;
		timeToEyeChange = MathUtils.random(0.5f, 4.0f);
	}

	public void resetEyeBlinkTimer() {
		if (eyesOpen) {
			timeToBlinkChange = MathUtils.random(3.0f, 6.0f);
		} else {
			timeToBlinkChange = MathUtils.random(0.25f);
		}
		timeSinceBlinkChange = 0.0f;
		// Blink also resets the poked state
		poked = false;
	}

	public boolean isTimeToChangeEyes() {
		return timeSinceEyeChange > timeToEyeChange;
	}

	public boolean isTimeToBlinkChange() {
		return timeSinceBlinkChange > timeToBlinkChange;
	}

	public void poke() {
		poked = true;
		eyesOpen = true;
		timeToBlinkChange = 0.25f;
		timeSinceBlinkChange = 0.0f;
		timeToEyeChange = timeToBlinkChange + MathUtils.random(0.5f, 4.0f);
		timeSinceEyeChange = 0.0f;
	}
}
