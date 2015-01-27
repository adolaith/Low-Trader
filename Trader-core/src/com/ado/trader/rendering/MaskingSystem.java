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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ArrayMap;

public class MaskingSystem {
	ArrayMap<String, Sprite> maskSprites;
	Map map;
	
	public MaskingSystem(Skin skin, Map map){
		this.map = map;
		maskSprites = new ArrayMap<String, Sprite>();
		Sprite s = new Sprite(skin.getSprite("wallMask_se"));
		s.scale(1f);
		loadMask("wallMask_se", s);
		s = new Sprite(skin.getSprite("wallMask_sw"));
		s.scale(1f);
		loadMask("wallMask_sw", s);
	}
	public void drawMask(SpriteBatch batch, String wallDir, Vector2 vec, float height, Position p, Mask mask){
		Vector2 tmp = IsoUtils.getColRow((int)InputHandler.getMousePos().x, (int)InputHandler.getMousePos().y, map.getTileWidth(), map.getTileHeight());
		//wall is within 6 tiles of x,y
		if(Math.abs((int)p.getX() - (int)tmp.x) < 6 &&  Math.abs((int)p.getY() - (int)tmp.y) < 4){
			if(height > 64){
				setGlMask(batch);
				Sprite s = maskSprites.get(wallDir);
				batch.draw(s, vec.x, vec.y, s.getWidth()*s.getScaleX(),s.getHeight()*s.getScaleY());
				setGlBlend(batch);
			}
		}else if(mask!=null){
			//wall has a feature mask(window, door)
			setGlMask(batch);
			batch.draw(mask.mask, vec.x, vec.y, mask.mask.getWidth()*mask.mask.getScaleX(),mask.mask.getHeight()*mask.mask.getScaleY());
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
	public void loadMask(String key, Sprite sprite){
		maskSprites.put(key, sprite);
	}
	public Sprite getMask(String key) {
		return maskSprites.get(key);
	}
	public ArrayMap<String, Sprite> getAllMasks(){
		return maskSprites;
	}
}