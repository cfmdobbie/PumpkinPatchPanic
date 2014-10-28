package com.maycontainsoftware.pumpkinpatchpanic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

class PumpkinButton extends Button {
	final TextureAtlas atlas;
	public PumpkinButton(final TextureAtlas atlas, final String prefix) {
		super(new TextureRegionDrawable(atlas.findRegion(prefix + "_up")), new TextureRegionDrawable(atlas.findRegion(prefix + "_down")));
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
}