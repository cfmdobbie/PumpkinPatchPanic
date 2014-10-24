package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/** Actor that represents a cloud. */
class Cloud extends Image {

	/** Constructor. */
	public Cloud(final TextureRegion region, final CloudModel model) {
		super(region);

		// Clouds are semi-transparent
		setColor(1.0f, 1.0f, 1.0f, 0.9f);

		// Add the action to the actor
		addAction(new CloudAction(model));
	}

	/** Action to update cloud position and recalculate speed and position after it leaves the screen. */
	static class CloudAction extends Action {

		/** Reference to the Cloud's model. */
		final CloudModel model;

		/**
		 * Construct a new Cloud action.
		 * 
		 * @param model
		 *            The Cloud's model.
		 */
		public CloudAction(final CloudModel model) {
			this.model = model;
		}

		@Override
		public boolean act(float delta) {
			if (model.isOffscreen()) {
				model.restart();
			}

			// Move cloud
			model.move(delta);
			actor.setPosition(model.x, model.y);

			return false;
		}
	}
}