package com.ado.trader.placement;

import com.ado.trader.entities.EntityFeatures;
import com.ado.trader.input.InputHandler;
import com.ado.trader.map.Chunk;
import com.ado.trader.map.Map;
import com.ado.trader.utils.GameServices;
import com.ado.trader.utils.IsoUtils;
import com.artemis.Entity;
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
	void place(int mapX, int mapY) {
		Chunk c = map.getChunk(mapX, mapY);
		Vector2 tile = Map.worldVecToTile(mapX, mapY);
		
		String decorates = features.getFeature(featureName).get("decorates").asString();
		switch(decorates){
		case "wall":
			if(!c.getWalls().isOccupied((int)tile.x, (int)tile.y)){
				//Invalid placement. Wall decoration only
				return;
			}
			Entity w = map.getWorld().getEntity(c.getWalls().map[(int)tile.x][(int)tile.y]);
			features.applyFeature(w, featureName, spriteIndex);
			break;
		}
	}

	public void rotateSelection(){
		Sprite[] sprites = entityRenderer.getSpriteManager().getFeatureSprites(featureName);
		
		if(spriteIndex <= sprites.length){
			if(sprites[spriteIndex + 1] != null){
				spriteIndex++;
				return;
			}
		}
		spriteIndex = 0;
	}
	
	public void renderPreview(SpriteBatch batch){
		if(delete)return;
		Vector2 mousePos = IsoUtils.getColRow((int)InputHandler.getMousePos().x, (int)InputHandler.getMousePos().y, 
				map.getTileWidth(), map.getTileHeight());
		mousePos = IsoUtils.getIsoXY((int)mousePos.x, (int)mousePos.y, 
				map.getTileWidth(), map.getTileHeight());
		
		Sprite sprite = entityRenderer.getSpriteManager().getFeatureSprites(featureName)[spriteIndex];
		
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

	@Override
	void clearSettings() {
		featureName = null;
		spriteIndex = 0;
	}
}
