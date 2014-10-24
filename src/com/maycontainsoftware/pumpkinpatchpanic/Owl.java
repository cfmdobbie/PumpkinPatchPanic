package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/** Actor to represent the owl. */
class Owl extends Image {

	// Drawables for the different owl graphics
	private final TextureRegionDrawable eyesDown;
	private final TextureRegionDrawable eyesLeft;
	private final TextureRegionDrawable eyesRight;
	private final TextureRegionDrawable blink;

	/**
	 * Construct a new Owl actor.
	 * 
	 * @param x
	 * @param y
	 * @param atlas
	 * @param model
	 */
	public Owl(final OwlModel model, final TextureAtlas atlas) {

		// Call super with a graphic - mainly just to set up size for us
		super(atlas.findRegion("owl_down"));

		// Load owl frames
		eyesDown = new TextureRegionDrawable(atlas.findRegion("owl_down"));
		eyesLeft = new TextureRegionDrawable(atlas.findRegion("owl_left"));
		eyesRight = new TextureRegionDrawable(atlas.findRegion("owl_right"));
		blink = new TextureRegionDrawable(atlas.findRegion("owl_blink"));

		// Position the Owl on the stage
		setPosition(model.x, model.y);

		OwlAction action = new OwlAction(model);
		addAction(action);
		action.updateGraphic();

		// Want owl to respond to touch - owl should blink when poked
		// However, as we have two stages and a single InputProcessor, this does not work
		// FUTURE: Fix owl's touch input
		// addListener(new InputListener() {
		// @Override
		// public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		// // Owls blink when touched
		// setDrawable(blink);
		// timeToBlinkChange = MathUtils.random(0.5f, 0.75f);
		// timeSinceBlinkChange = 0.0f;
		// return true;
		// }
		// });
	}

	/** Action to animate an owl. */
	static class OwlAction extends Action {

		private final OwlModel model;

		public OwlAction(final OwlModel model) {
			this.model = model;
		}

		@Override
		public boolean act(float delta) {

			// Update timers
			model.update(delta);

			// Eye change code
			if (model.isTimeToChangeEyes()) {

				// Need to change eyes
				model.pickNewEyeDirection();
				model.resetEyeChangeTimer();

				updateGraphic();
			}

			// Blink code
			if (model.isTimeToBlinkChange()) {

				model.eyesOpen = !model.eyesOpen;
				model.resetEyeBlinkTimer();

				updateGraphic();
			}

			return false;
		}

		public void updateGraphic() {

			// Reference to the Owl actor
			final Owl owl = (Owl) actor;

			if (!model.eyesOpen) {
				owl.setDrawable(owl.blink);
			} else {
				switch (model.eyeDirection) {
				case DOWN:
					owl.setDrawable(owl.eyesDown);
					break;
				case LEFT:
					owl.setDrawable(owl.eyesLeft);
					break;
				case RIGHT:
					owl.setDrawable(owl.eyesRight);
					break;
				}
			}
		}
	}
}