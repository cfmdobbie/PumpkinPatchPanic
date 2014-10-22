package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Actor that represents a spirit escaping from a pumpkin.
 * 
 * @author Charlie
 */
class Spirit extends Image {

	/** Constructor. */
	public Spirit(final TextureRegion spiritRegion, final float x, final float y) {
		super(spiritRegion);

		// Set origin so rotations work correctly
		setOrigin(getWidth() / 2, getHeight() / 2);

		// Start out at 1/8th scale
		setScale(0.125f);

		// Start out transparent
		setColor(1.0f, 1.0f, 1.0f, 0.0f);

		// Start out centred on the specified coordinates
		setPosition(x - getWidth() / 2, y - getHeight() / 2);

		// Over the next few seconds: Scale up to 4x, spin, fade in then rapidly fade out
		addAction(Actions.parallel(Actions.scaleTo(4.0f, 4.0f, 2.0f), Actions.rotateBy(360.0f * 4, 2.0f),
				Actions.fadeIn(1.0f), Actions.sequence(Actions.delay(1.0f), Actions.fadeOut(0.25f))));
	}

	@Override
	public Actor hit(final float x, final float y, final boolean touchable) {
		// Want to be able to click behind/through this Actor, so hit() should always return null
		return null;
	}
}