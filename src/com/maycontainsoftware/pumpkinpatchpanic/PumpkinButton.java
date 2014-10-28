package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

class PumpkinButton extends Button {
	final TextureAtlas atlas;

	public PumpkinButton(final TextureAtlas atlas, final String prefix) {
		super(new TextureRegionDrawable(atlas.findRegion(prefix + "_up")), new TextureRegionDrawable(
				atlas.findRegion(prefix + "_down")));
		this.atlas = atlas;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		// TODO: Support scale changes
		// Plant
		batch.draw(atlas.findRegion("plant"), getX() - 15 - 33, getY() - 10 - 43);
		// Pumpkin base
		batch.draw(atlas.findRegion("pumpkin"), getX() - 15, getY() - 10);
		// Decal
		super.draw(batch, parentAlpha);
	}

	/**
	 * A PumpkinButton with two sets of up/down states, plus utility methods to easily switch between them.
	 * 
	 * @author Charlie
	 */
	static class TogglePumpkinButton extends PumpkinButton {

		final String onPrefix;
		final String offPrefix;

		/** Constructor. */
		public TogglePumpkinButton(final TextureAtlas atlas, final String onPrefix, final String offPrefix) {
			super(atlas, onPrefix);

			this.onPrefix = onPrefix;
			this.offPrefix = offPrefix;
		}

		/** Switch the button graphics to a different up/down pair. */
		private void switchGraphics(String prefix) {
			final Button.ButtonStyle style = getStyle();
			style.up = new TextureRegionDrawable(atlas.findRegion(prefix + "_up"));
			style.down = new TextureRegionDrawable(atlas.findRegion(prefix + "_down"));
			setStyle(style);
		}

		/** Switch the button graphics to the previously-specified "on" up/down pair. */
		public void on() {
			switchGraphics(onPrefix);
		}

		/** Switch the button graphics to the previously-specified "off" up/down pair. */
		public void off() {
			switchGraphics(offPrefix);
		}

	}
}