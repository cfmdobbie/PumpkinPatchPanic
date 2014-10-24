package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/** Actor that represents a cloud. */
class Cloud extends Image {

	/** The width of the TextureRegion. */
	private final int width;

	/** The cloud's y-coordinate (fixed in constructor.) */
	private final float y;

	/** The cloud's x-coordinate (updated in CloudAction.) */
	private float x;

	/** The cloud's current velocity, in the form of x-coordinate change in pixels/second. */
	private float dx;

	/** Minimum cloud speed. */
	private static final float MIN_SPEED = 10;

	/** Maximum cloud speed. */
	private static final float MAX_SPEED = 40;

	/** Constructor. */
	public Cloud(final TextureRegion region, float y) {
		super(region);

		// Clouds are semi-transparent
		setColor(1.0f, 1.0f, 1.0f, 0.9f);

		// Determine cloud dimensions
		width = region.getRegionWidth();

		// Remember y coordinate
		this.y = y;

		// Initial speed
		dx = -MathUtils.random(MIN_SPEED, MAX_SPEED);

		// Initial position
		this.x = MathUtils.random(-width, 1280.0f + -dx * 10);

		// Movement action
		addAction(new CloudAction());
	}

	/** Action to update cloud position and recalculate speed and position after it leaves the screen. */
	class CloudAction extends Action {
		@Override
		public boolean act(float delta) {
			if (x < -width) {
				// Cloud is off-screen, recalculate speed, then reposition
				dx = -MathUtils.random(MIN_SPEED, MAX_SPEED);
				// Move cloud beyond right edge, with 1-10 seconds before it reappears
				x = 1280.0f + MathUtils.random(-dx, -dx * 10);
			}

			// Move cloud
			x += dx * delta;
			Cloud.this.setPosition(x, y);

			return false;
		}
	}
}