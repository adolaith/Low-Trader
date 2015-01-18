package com.ado.trader.placement;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.entities.EntityFeatures;
import com.ado.trader.gui.GameServices;
import com.ado.trader.input.InputHandler;
import com.ado.trader.rendering.EntityRenderSystem;
import com.ado.trader.utils.IsoUtils;
import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class FeaturePlaceable extends Placeable {
	public String featureId;
	public int spriteId;
	Sprite sprite;
	
	EntityFeatures features;

	protected FeaturePlaceable(GameServices gameRes) {
		super(gameRes.getMap());
		features = new EntityFeatures(gameRes.getAtlas(), gameRes.getParser(), gameRes.getRenderer().getRenderEntitySystem());
	}
	
	@Override
	void place(int x, int y) {
		switch(features.getFeature(featureId).get("deco")){
		case "wall":
			if(!map.getWallLayer().isOccupied(x, y, map.currentLayer)){
				//Invalid placement. Wall decoration only
				return;
			}
			Entity w = map.getWorld().getEntity(map.getWallLayer().map[x][y][map.currentLayer]);
			features.applyFeature(w, featureId, spriteId, new Sprite(sprite));
			break;
		}
		if(sprite.isFlipX()){
			sprite.flip(true, false);
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
	public void rotateSelection(EntityRenderSystem entityRenderer){
		String[] tmp = features.getFeature(featureId).get("sprite").split(",");
		if(tmp.length==1){
			sprite.flip(true, false);
			return;
		}
		if(sprite.isFlipX()){
			sprite.flip(true, false);
			for(String s:tmp){
				int i = Integer.valueOf(s);
				if(i==spriteId)continue;
				spriteId = i;
				sprite = entityRenderer.getStaticSprites().get(i);
				break;
			}
		}else{
			sprite.flip(true, false);
		}
	}
	public void renderPreview(SpriteBatch batch){
		if(delete)return;
		Vector2 mousePos = IsoUtils.getColRow((int)InputHandler.getMousePos().x, (int)InputHandler.getMousePos().y, 
				map.getTileWidth(), map.getTileHeight());
		mousePos = IsoUtils.getIsoXY((int)mousePos.x, (int)mousePos.y, 
				map.getTileWidth(), map.getTileHeight());
		batch.begin();
		
		batch.draw(sprite, mousePos.x, mousePos.y, 
				sprite.getWidth()*sprite.getScaleX(), sprite.getHeight()*sprite.getScaleY());
		batch.end();
	}
	@Override
	void dragPlace(Vector2 start, Vector2 widthHeight) {
	}
}
