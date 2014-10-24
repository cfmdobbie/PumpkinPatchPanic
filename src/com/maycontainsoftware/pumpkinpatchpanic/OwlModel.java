package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.math.MathUtils;

/**
 * OwlModel represents an Owl's logical data model.
 * 
 * @author Charlie
 */
class OwlModel {

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

	/** Time since the last blink change. */
	float timeSinceBlinkChange;

	/** Time until the next blink change. */
	float timeToBlinkChange;

	/** Construct a new model. */
	public OwlModel() {

		// Start off looking down
		eyeDirection = EyeDirection.DOWN;
		timeSinceEyeChange = 0.0f;
		timeToEyeChange = MathUtils.random(0.5f, 4.0f);

		// And not blinking
		eyesOpen = true;
		timeSinceBlinkChange = 0.0f;
		timeToBlinkChange = MathUtils.random(3.0f, 6.0f);
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
	}

	public boolean isTimeToChangeEyes() {
		return timeSinceEyeChange > timeToEyeChange;
	}

	public boolean isTimeToBlinkChange() {
		return timeSinceBlinkChange > timeToBlinkChange;
	}
}
