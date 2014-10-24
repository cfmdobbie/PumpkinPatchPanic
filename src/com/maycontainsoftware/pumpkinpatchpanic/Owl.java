package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
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

	private float timeSinceEyeChange = 0.0f;
	private float timeToEyeChange;

	private static enum EyeDirection {
		DOWN,
		LEFT,
		RIGHT,
	}

	private EyeDirection eyeDirection;

	private float timeSinceBlinkChange = 0.0f;
	private float timeToBlinkChange;
	private boolean eyesOpen = true;

	public Owl(final float x, final float y, final TextureAtlas atlas) {
		// Call super with a graphic - mainly just to set up size for us
		super(atlas.findRegion("owl_down"));

		// Load owl frames
		eyesDown = new TextureRegionDrawable(atlas.findRegion("owl_down"));
		eyesLeft = new TextureRegionDrawable(atlas.findRegion("owl_left"));
		eyesRight = new TextureRegionDrawable(atlas.findRegion("owl_right"));
		blink = new TextureRegionDrawable(atlas.findRegion("owl_blink"));

		// Start off looking down
		eyeDirection = EyeDirection.DOWN;
		timeToEyeChange = MathUtils.random(0.5f, 4.0f);

		// And not blinking
		timeToBlinkChange = MathUtils.random(3.0f, 6.0f);

		setPosition(x, y);

		addAction(new OwlAction());

		// Want owl to respond to touch - owl should blink when poked
		// However, as we have two stages and a single InputProcessor, this does not work
		// FUTURE: Fix owl's touch input
		addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				// Owls blink when touched
				setDrawable(blink);
				timeToBlinkChange = MathUtils.random(0.5f, 0.75f);
				timeSinceBlinkChange = 0.0f;
				return true;
			}
		});
	}

	/** Action to animate an owl. */
	class OwlAction extends Action {
		@Override
		public boolean act(float delta) {

			// Eye change code

			timeSinceEyeChange += delta;

			if (timeSinceEyeChange > timeToEyeChange) {
				// Need to change eyes
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

				timeSinceEyeChange = 0.0f;
				timeToEyeChange = MathUtils.random(0.5f, 4.0f);

				if (eyesOpen) {
					switch (eyeDirection) {
					case DOWN:
						setDrawable(eyesDown);
						break;
					case LEFT:
						setDrawable(eyesLeft);
						break;
					case RIGHT:
						setDrawable(eyesRight);
						break;
					}
				}
			}

			// Blink code

			timeSinceBlinkChange += delta;

			if (timeSinceBlinkChange >= timeToBlinkChange) {
				if (eyesOpen) {
					// Need to blink
					setDrawable(blink);
					timeToBlinkChange = MathUtils.random(0.25f);
					timeSinceBlinkChange = 0.0f;
				} else {
					// Need to stop blinking
					switch (eyeDirection) {
					case DOWN:
						setDrawable(eyesDown);
						break;
					case LEFT:
						setDrawable(eyesLeft);
						break;
					case RIGHT:
						setDrawable(eyesRight);
						break;
					}
					timeToBlinkChange = MathUtils.random(3.0f, 6.0f);
					timeSinceBlinkChange = 0.0f;
				}
				eyesOpen = !eyesOpen;
			}

			return false;
		}
	}
}