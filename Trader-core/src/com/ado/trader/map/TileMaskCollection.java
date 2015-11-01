package com.ado.trader.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class TileMaskCollection {
	Sprite[][] masks;
	int tileWidth, tileHeight;
	
	public TileMaskCollection(TextureAtlas atlas, int tileWidth, int tileHeight){
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		loadMasks(atlas);
	}
	public void loadMasks(TextureAtlas atlas){
		masks = new Sprite[7][4];
		
		loopDirections(0, "terrain/mask_end", atlas);
		loopDirections(1, "terrain/mask_exit", atlas);
		loopDirections(2, "terrain/mask_fork", atlas);
		loopDirections(3, "terrain/mask_outer", atlas);
		loopDirections(4, "terrain/mask_centre", atlas);
		
		masks[5][0] = atlas.createSprite("terrain/mask_corner_n");
		masks[5][1] = atlas.createSprite("terrain/mask_corner_s");
		masks[5][2] = atlas.createSprite("terrain/mask_corner_e");
		masks[5][3] = atlas.createSprite("terrain/mask_corner_w");
		
		masks[6][0] = atlas.createSprite("terrain/mask_single");
	}
	
	private void loopDirections(int type, String filePath, TextureAtlas atlas){
		masks[type][0] = atlas.createSprite(filePath + "_ne");
		masks[type][1] = atlas.createSprite(filePath + "_se");
		masks[type][2] = atlas.createSprite(filePath + "_nw");
		masks[type][3] = atlas.createSprite(filePath + "_sw");
	}
	
	public void drawMask(SpriteBatch batch, float x, float y, int mask, int dir){
		batch.flush();
		//disable RGB color, only enable ALPHA to the frame buffer
		Gdx.gl.glColorMask(false, false, false, true);

		//change the blending function for our alpha map
		batch.setBlendFunction(GL30.GL_ONE, GL30.GL_ZERO);
		
		Sprite maskSprite = masks[mask][dir];

		//draw alpha mask sprite(s)
		batch.draw(maskSprite, x, y, tileWidth, tileHeight);

	}
	public void drawOverlay(SpriteBatch batch,float x, float y, Sprite tileOverlay){
		batch.flush();
		//now that the buffer has our alpha, we simply draw the sprite with the mask applied
		Gdx.gl.glColorMask(true, true, true, true);
		batch.setBlendFunction(GL30.GL_DST_ALPHA, GL30.GL_ONE_MINUS_DST_ALPHA);
		//draw our sprite to be masked
		batch.draw(tileOverlay, x, y, tileWidth, tileHeight);
		
		batch.setBlendFunction(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
	}
	public Sprite[][] getMaskSprites(){
		return masks;
	}
}
