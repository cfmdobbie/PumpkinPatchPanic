package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.math.MathUtils;

/**
 * CloudModel represents a Cloud's logical data model.
 * 
 * @author Charlie
 */
public class CloudModel {

	/** The width of the TextureRegion. */
	private final int width;

	/** The cloud's y-coordinate (fixed in constructor.) */
	final float y;

	/** The cloud's x-coordinate (updated in CloudAction.) */
	float x;

	/** The cloud's current velocity, in the form of x-coordinate change in pixels/second. */
	private float dx;

	/** Minimum cloud speed. */
	private static final float MIN_SPEED = 10;

	/** Maximum cloud speed. */
	private static final float MAX_SPEED = 40;

	/**
	 * Construct a new CloudModel.
	 * 
	 * @param y
	 *            The fixed y-coordinate.
	 * @param width
	 *            The width of the actor.
	 */
	public CloudModel(final float y, final int width) {
		this.y = y;
		this.width = width;

		// Set initial position and speed
		this.x = MathUtils.random(-width, 1280.0f);
		this.dx = -MathUtils.random(MIN_SPEED, MAX_SPEED);
	}

	/** Update the model wrt time. */
	public void move(final float delta) {
		x += dx * delta;
	}

	/** Whether or not the cloud is currently off-screen. */
	public boolean isOffscreen() {
		return x < -width;
	}

	/**
	 * Reposition the cloud off the right edge of the screen, and set the speed with which it will traverse the screen
	 * to the left.
	 */
	public void restart() {
		// Cloud is off-screen, recalculate speed, then reposition
		dx = -MathUtils.random(CloudModel.MIN_SPEED, CloudModel.MAX_SPEED);
		// Move cloud beyond right edge, with 1-10 seconds before it reappears
		x = 1280.0f + MathUtils.random(-dx, -dx * 10);
	}
}
