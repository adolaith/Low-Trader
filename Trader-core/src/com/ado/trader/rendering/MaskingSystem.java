package com.ado.trader.rendering;

import com.ado.trader.entities.components.Mask;
import com.ado.trader.entities.components.Position;
import com.ado.trader.input.InputHandler;
import com.ado.trader.map.Map;
import com.ado.trader.utils.IsoUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

public class MaskingSystem {
	ArrayMap<String, Sprite[]> maskSprites;
	Map map;
	
	public MaskingSystem(TextureAtlas atlas, Map map){
		this.map = map;
		maskSprites = new ArrayMap<String, Sprite[]>();
		
		Array<AtlasRegion> regions = atlas.getRegions();
		for(AtlasRegion a: regions){
			if(a.name.startsWith("masks")){
				//load masks
				String name = a.name.substring(a.name.indexOf('/') + 1);
				name = name.split("_")[0];
				
				if(maskSprites.containsKey(name)){
					Sprite[] list = maskSprites.get(name);
					Sprite secondMask = new Sprite(a);
					secondMask.scale(1f);
					list[1] = secondMask;
					continue;
				}
				
				Sprite[] list = new Sprite[2];
				Sprite firstMask = new Sprite(a);
				firstMask.scale(1f);
				list[0] = firstMask;
				
				maskSprites.put(name, list);
			}
		}
	}
	public void drawMask(SpriteBatch batch, int spriteIndex, Vector2 vec, float height, Position p, Mask mask){
		Vector2 tmp = IsoUtils.getColRow((int)InputHandler.getMousePos().x, (int)InputHandler.getMousePos().y, map.getTileWidth(), map.getTileHeight());
		tmp = Map.worldVecToTile((int) tmp.x, (int) tmp.y);
		
		//Mouse near wall mask. wall is within 6 tiles of mouse
		if(Math.abs((int)p.getTileX() - (int)tmp.x) < 6 &&  Math.abs((int)p.getTileY() - (int)tmp.y) < 4){
			//only sprites over 64 in height get masked(keep fences short) 
			if(height > 64){
				setGlMask(batch);
				Sprite s = maskSprites.get("wallMask")[spriteIndex];
				
				batch.draw(s, vec.x, vec.y, s.getWidth()*s.getScaleX(),s.getHeight()*s.getScaleY());
				setGlBlend(batch);
			}
		
		}else if(mask!=null){
			//wall has a feature mask(window, door)
			setGlMask(batch);
			Sprite s = maskSprites.get(mask.maskName)[mask.maskIndex];
			
			batch.draw(s, vec.x, vec.y, s.getWidth() * s.getScaleX(), s.getHeight() * s.getScaleY());
			setGlBlend(batch);
		}
	}
	private void setGlMask(SpriteBatch batch){
		batch.flush();
		//disable RGB color, only enable ALPHA to the frame buffer
		Gdx.gl.glColorMask(false, false, false, true);
		//change the blending function for our alpha map
		batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
	}
	private void setGlBlend(SpriteBatch batch){
		batch.flush();
		//now that the buffer has our alpha, we simply draw the sprite with the mask applied
		Gdx.gl.glColorMask(true, true, true, true);
		batch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
	}
	public void loadMaskSet(String key, Sprite[] sprites){
		maskSprites.put(key, sprites);
	}
	public Sprite[] getMask(String key) {
		return maskSprites.get(key);
	}
	public ArrayMap<String, Sprite[]> getAllMasks(){
		return maskSprites;
	}
}
