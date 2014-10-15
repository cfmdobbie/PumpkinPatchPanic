package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;

public class AnimatedActor extends Image {
	public AnimatedActor(final Animation animation) {
		super(new AnimationDrawable(animation));
	}

	@Override
	public void act(final float delta) {
		((AnimationDrawable) getDrawable()).act(delta);
		super.act(delta);
	}

	private static class AnimationDrawable extends BaseDrawable {
		private final Animation anim;

		private float stateTime = 0;

		public AnimationDrawable(final Animation anim) {
			this.anim = anim;
			setMinWidth(anim.getKeyFrame(0.0f).getRegionWidth());
			setMinHeight(anim.getKeyFrame(0.0f).getRegionHeight());
		}

		public void act(final float delta) {
			stateTime += delta;
		}

		@Override
		public void draw(final SpriteBatch batch, final float x, final float y, final float width, final float height) {
			batch.draw(anim.getKeyFrame(stateTime), x, y, width, height);
		}
	}
}
