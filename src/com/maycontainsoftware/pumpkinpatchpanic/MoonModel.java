package com.maycontainsoftware.pumpkinpatchpanic;

/**
 * MoonModel represents the Moon's logical data model.
 * 
 * @author Charlie
 */
class MoonModel {

	/*
	 * Theory of moon movement calculations:-
	 * 
	 * Want moon to rise to exactly half off the top of the screen. Want origin and destination points to be just off
	 * the sides of the screen, at 50% of the height of the screen. Can calculate coordinates of starting and highest
	 * positions. Can calculate slope of line between these. Can calculate midpoint of that line, and negative
	 * reciprocal of the slope. This gives a line with known slope and one known coordinate passing through the centre
	 * of the circle that joins the three moon positions.
	 * 
	 * Zenith: (640, 720) ~ Left position: (-55, 360) ~ Midpoint: (292, 540)
	 * 
	 * Slope: y2-y1/x2-x1 = 0.518 ~ Perpendicular slope: -1.9305
	 * 
	 * Line: y = m * x + c, 540 = -1.9305 * 292 + c, c = 1103.72
	 * 
	 * Centre of circle: y = m * x + c, y = -1.9305 * 640 + 1103.72, y = -131.83
	 */

	/*
	 * Old calculations to determine visible arc of moon movement:
	 * 
	 * Want the moon to start its journey just off the left edge of the screen, so need to know initial rotation
	 * required. Calculate by trig based on starting location and centre of circle, given previously-calculated distance
	 * between them.
	 * 
	 * sin A = o / h = (640 + 55) / (720 + 131.83) = 695 / 851.83 = 0.816
	 * 
	 * A = sin-1 0.816 = 54.68 degrees
	 * 
	 * (Note that we currently don't use this value!)
	 */

	/*
	 * Old calculations to determine moon traversal speed:
	 * 
	 * Rotation speed. Want the moon to traverse from left to right over 60 seconds. Now know the total angle covered,
	 * so need to calculate rotation time for a full 360-degree rotation.
	 * 
	 * 60-second arc: 54.68 * 2 = 109.35 degrees
	 * 
	 * 109.35 / 60 = 360 / t, t = 360 * 60 / 109.35 = 197.53 seconds
	 */

	/** Angle of rotation. Note that angle is wrt vertical, with positive numbers being more CCW rotation. */
	float angle;

	/** Angular speed in degrees per second. */
	float angularSpeed;

	/** Construct a new MoonModel. */
	public MoonModel() {
		// Want moon to start at far left horizon (not visible) so a delay exists before it appears
		// Note that positive angle is a CCW rotation
		angle = 90.0f;
		// Actually rotating three times slower than previously calculated - three minute arc horizon to horizon
		angularSpeed = 360.0f / (197.53f * 3);
	}

	/**
	 * Update model calculations.
	 * 
	 * @param delta
	 *            Number of seconds since last update.
	 */
	public void update(final float delta) {
		angle -= delta * angularSpeed;
		angle %= 360.0f;
	}
}