package com.ado.trader.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ArrayMap;

public class TileOverlay {
	ArrayMap<Mask, Sprite> maskTextures;
	
	public TileOverlay(TextureAtlas atlas){
		loadMasks(atlas);
	}
	public void loadMasks(TextureAtlas atlas){
		maskTextures = new ArrayMap<TileOverlay.Mask, Sprite>();
		maskTextures.put(Mask.WEST, atlas.createSprite("terrain/maskWest"));
		maskTextures.put(Mask.NORTH, atlas.createSprite("terrain/maskNorth"));
		maskTextures.put(Mask.SOUTH, atlas.createSprite("terrain/maskSouth"));
		maskTextures.put(Mask.EAST, atlas.createSprite("terrain/maskEast"));
		maskTextures.put(Mask.NE, atlas.createSprite("terrain/maskNe"));
		maskTextures.put(Mask.SE, atlas.createSprite("terrain/maskSe"));
		maskTextures.put(Mask.SW, atlas.createSprite("terrain/maskSw"));
		maskTextures.put(Mask.NW, atlas.createSprite("terrain/maskNw"));
		maskTextures.put(Mask.CENTRE, atlas.createSprite("terrain/maskCentre"));
	}
	public void drawMask(SpriteBatch batch, float x, float y, int width, int height, Mask mask){
		batch.flush();
		//disable RGB color, only enable ALPHA to the frame buffer
		Gdx.gl.glColorMask(false, false, false, true);

		//change the blending function for our alpha map
		batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);

		//draw alpha mask sprite(s)
		batch.draw(maskTextures.get(mask), x, y, width, height);

	}
	public void drawOverlay(SpriteBatch batch,float x, float y, int width, int height, Sprite overlay){
		batch.flush();
		//now that the buffer has our alpha, we simply draw the sprite with the mask applied
		Gdx.gl.glColorMask(true, true, true, true);
		batch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
		//draw our sprite to be masked
		batch.draw(overlay, x, y, width, height);
	}
	public enum Mask{
		NORTH(), EAST(), SOUTH(), WEST(), NE(),SE(),SW(),NW(), CENTRE();
	}
}
