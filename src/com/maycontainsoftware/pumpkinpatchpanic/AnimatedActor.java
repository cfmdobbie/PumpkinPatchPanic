package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;

/**
 * A simple animated Actor. The Actor is passed an Animation at time of creation and plays the frame as per absolute
 * time that the Actor has been attached to the Stage. Note that Animations are highly likely to be looping.
 * 
 * @author Charlie
 */
public class AnimatedActor extends Image {
	/**
	 * Create a new AnimatedActor
	 * 
	 * @param animation
	 *            The Animation to play.
	 */
	public AnimatedActor(final Animation animation) {
		super(new AnimationDrawable(animation));
	}

	@Override
	public void act(final float delta) {
		// Notify the drawable of the elapsed time
		((AnimationDrawable) getDrawable()).act(delta);
		super.act(delta);
	}

	/**
	 * A Drawable subclass that chooses a TextureRegion from an Animation.
	 * 
	 * @author Charlie
	 */
	private static class AnimationDrawable extends BaseDrawable {

		/** The Animation to be played. */
		private final Animation anim;

		/** The absolute elapsed time. */
		private float elapsedTime = 0;

		/**
		 * Construct a new AnimationDrawable
		 * 
		 * @param anim
		 *            The Animation that is to be played.
		 */
		public AnimationDrawable(final Animation anim) {
			this.anim = anim;
			// Minimum dimensions as per the first frame of animation
			setMinWidth(anim.getKeyFrame(0.0f).getRegionWidth());
			setMinHeight(anim.getKeyFrame(0.0f).getRegionHeight());
		}

		/**
		 * A method of similar purpose as Actor.act(float) - allowing the object to take action based on elapsed time.
		 * 
		 * @param delta
		 *            The elapsed time.
		 */
		public void act(final float delta) {
			elapsedTime += delta;
		}

		@Override
		public void draw(final SpriteBatch batch, final float x, final float y, final float width, final float height) {
			batch.draw(anim.getKeyFrame(elapsedTime), x, y, width, height);
		}
	}
}
