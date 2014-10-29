package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/** Actor to represent the owl. */
class Owl extends Image {

	// Drawables for the different owl graphics
	private final TextureRegionDrawable eyesDown;
	private final TextureRegionDrawable eyesLeft;
	private final TextureRegionDrawable eyesRight;
	private final TextureRegionDrawable blink;
	private final TextureRegionDrawable poke;

	/**
	 * Construct a new Owl actor.
	 * 
	 * @param x
	 * @param y
	 * @param atlas
	 * @param model
	 */
	public Owl(final OwlModel model, final PumpkinGame game, final TextureAtlas atlas) {

		// Call super with a graphic - mainly just to set up size for us
		super(atlas.findRegion("owl_down"));

		// Load owl frames
		eyesDown = new TextureRegionDrawable(atlas.findRegion("owl_down"));
		eyesLeft = new TextureRegionDrawable(atlas.findRegion("owl_left"));
		eyesRight = new TextureRegionDrawable(atlas.findRegion("owl_right"));
		blink = new TextureRegionDrawable(atlas.findRegion("owl_blink"));
		poke = new TextureRegionDrawable(atlas.findRegion("owl_poke"));

		// Position the Owl on the stage
		setPosition(model.x, model.y);

		final OwlAction action = new OwlAction(model);
		addAction(action);
		action.updateGraphic();

		// Owl responds to touch - by looking a bit crazy for a moment
		addListener(new InputListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer,
					final int button) {
				// Tell the model we've been poked
				model.poke();
				// Ask the action to update the graphic as required
				action.updateGraphic();
				// Play the squark sound effect
				game.playSquark();

				return true;
			}
		});
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

			if (model.poked) {
				owl.setDrawable(owl.poke);
			} else if (!model.eyesOpen) {
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