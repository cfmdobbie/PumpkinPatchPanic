package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Actor to represent the moon.
 * 
 * @author Charlie
 */
class Moon extends Image {

	/** Construct a new Moon actor. */
	public Moon(final MoonModel model, final TextureRegion region) {
		super(region);

		// Moon's position and origin are known and fixed
		// Position is middle of top edge of screen
		setPosition(640 - 110 / 2, 720 - 110 / 2);
		// Origin is some distance below screen
		setOrigin(110 / 2, 110 / 2 - 720 - 132);

		addAction(new MoonAction(model));
	}

	/** Action to animate the moon. */
	class MoonAction extends Action {

		/** Reference to the Moon's model. */
		final MoonModel model;

		/**
		 * Construct a new MoonAction.
		 * 
		 * @param model
		 *            The Moon's model.
		 */
		public MoonAction(final MoonModel model) {
			this.model = model;
		}

		@Override
		public boolean act(float delta) {

			// Update model
			model.update(delta);

			// Update actor position
			actor.setRotation(model.angle);

			return false;
		}
	}
}