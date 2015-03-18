package com.ado.trader.placement;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.entities.EntityFeatures;
import com.ado.trader.input.InputHandler;
import com.ado.trader.utils.GameServices;
import com.ado.trader.utils.IsoUtils;
import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class FeaturePlaceable extends Placeable {
	public String featureName;
	public int spriteIndex;
	
	EntityFeatures features;

	protected FeaturePlaceable(GameServices gameRes) {
		super(gameRes.getMap(), gameRes.getRenderer().getRenderEntitySystem());
		features = new EntityFeatures(gameRes.getAtlas(), gameRes.getRenderer().getRenderEntitySystem());
	}
	
	@Override
	void place(int x, int y) {
		String decorates = features.getFeature(featureName).get("decorates").asString();
		switch(decorates){
		case "wall":
			if(!map.getWallLayer().isOccupied(x, y, map.currentLayer)){
				//Invalid placement. Wall decoration only
				return;
			}
			Entity w = map.getWorld().getEntity(map.getWallLayer().map[x][y][map.currentLayer]);
			features.applyFeature(w, featureName, spriteIndex);
			break;
		}
	}

	@Override
	void remove(int x, int y) {
		if(!delete){return;}
		if(map.getWallLayer().isOccupied(x, y, map.currentLayer)){
			EntityFactory.deleteEntity(x,y, map.currentLayer, map.getWallLayer());
		}
		if(!Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)){
			delete^=delete;
		}
	}
	
	public void rotateSelection(){
		Sprite[] sprites = entityRenderer.getSprites().get(featureName);
		
		if(spriteIndex == sprites.length){
			spriteIndex = 0;
		}else{
			spriteIndex++;
		}
	}
	
	public void renderPreview(SpriteBatch batch){
		if(delete)return;
		Vector2 mousePos = IsoUtils.getColRow((int)InputHandler.getMousePos().x, (int)InputHandler.getMousePos().y, 
				map.getTileWidth(), map.getTileHeight());
		mousePos = IsoUtils.getIsoXY((int)mousePos.x, (int)mousePos.y, 
				map.getTileWidth(), map.getTileHeight());
		
		Sprite sprite = entityRenderer.getSprites().get(featureName)[spriteIndex];
		
		batch.begin();
		
		batch.draw(sprite, mousePos.x, mousePos.y, 
				sprite.getWidth()*sprite.getScaleX(), sprite.getHeight()*sprite.getScaleY());
		batch.end();
	}
	
	@Override
	void dragPlace(Vector2 start, Vector2 widthHeight) {
	}
	public EntityFeatures getFeatures(){
		return features;
	}
}
