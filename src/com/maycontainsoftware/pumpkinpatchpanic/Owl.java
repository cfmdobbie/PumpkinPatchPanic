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

	public Owl(final float x, final float y, final TextureAtlas atlas, final OwlModel model) {

		// Call super with a graphic - mainly just to set up size for us
		super(atlas.findRegion("owl_down"));

		// Load owl frames
		eyesDown = new TextureRegionDrawable(atlas.findRegion("owl_down"));
		eyesLeft = new TextureRegionDrawable(atlas.findRegion("owl_left"));
		eyesRight = new TextureRegionDrawable(atlas.findRegion("owl_right"));
		blink = new TextureRegionDrawable(atlas.findRegion("owl_blink"));

		// Position the Owl on the stage
		setPosition(x, y);

		addAction(new OwlAction(model));

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

			// Reference to the Owl actor
			final Owl owl = (Owl) actor;

			// Eye change code
			if (model.isTimeToChangeEyes()) {

				// Need to change eyes
				model.pickNewEyeDirection();
				model.resetEyeChangeTimer();

				if (model.eyesOpen) {

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

			// Blink code
			if (model.isTimeToBlinkChange()) {

				model.eyesOpen = !model.eyesOpen;
				model.resetEyeBlinkTimer();

				if (!model.eyesOpen) {
					// Need to blink
					owl.setDrawable(owl.blink);
				} else {
					// Need to stop blinking
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

			return false;
		}
	}
}