package com.ado.trader.placement;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.entities.components.Area;
import com.ado.trader.entities.components.Position;
import com.ado.trader.gui.CreateNpcWindow;
import com.ado.trader.input.InputHandler;
import com.ado.trader.utils.GameServices;
import com.ado.trader.utils.IsoUtils;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;

@Wire
public class EntityPlaceable extends Placeable {
	public String entityName;
	public int spriteIndex;
	ComponentMapper<Area> areaMapper;
	CreateNpcWindow createNpcWindow;
	EntityFactory entities;
	
	public EntityPlaceable(GameServices gameRes) {
		super(gameRes.getMap(), gameRes.getRenderer().getRenderEntitySystem());
		this.entities = gameRes.getEntities();
		this.createNpcWindow = new CreateNpcWindow(gameRes);
	}
	
	public void place(int x,int y){
		JsonValue profile = entities.getEntityData().get(entityName);
		
		if(profile.has("animation")){
			createNpcWindow.showWindow((int)InputHandler.getIsoClicked().x, (int)InputHandler.getIsoClicked().y);
			return;
		}
		
		Entity e = EntityFactory.createEntity(entityName, spriteIndex);
		e.getComponent(Position.class).setPosition(x, y, map.currentLayer);

		rotateArea(e);
		
		map.getEntityLayer().addToMap(e.getId(), x, y, map.currentLayer);
		map.getEntityLayer().markAreaOccupied(x, y, map.currentLayer, e, map.getEntityLayer());
		
	}
	
	public void remove(int x, int y){
		if(map.getEntityLayer().isOccupied(x, y, map.currentLayer)){
			EntityFactory.deleteEntity(x,y, map.currentLayer, map.getEntityLayer());
		}
	}
	
	private void rotateArea(Entity e){
		if (areaMapper.has(e)) {
			if (spriteIndex == 1) {
				for (Vector2 vec : areaMapper.get(e).area) {
					vec.rotate90(1);
				}
			}
		}
	}
	public void renderPreview(SpriteBatch batch){
		JsonValue profile = entities.getEntityData().get(entityName);
		
		if(delete || !profile.has("sprite"))return;
		
		Vector2 mousePos = IsoUtils.getColRow((int)InputHandler.getMousePos().x, (int)InputHandler.getMousePos().y,
				map.getTileWidth(), map.getTileHeight());
		
		mousePos = IsoUtils.getIsoXY((int)mousePos.x, (int)mousePos.y, 
				map.getTileWidth(), map.getTileHeight());
		
		Sprite sprite = entityRenderer.getSprites().get(entityName)[spriteIndex];
		
		batch.begin();
		if(profile.has("area")){
			if(spriteIndex == 1){
				batch.draw(sprite , mousePos.x-4, mousePos.y-32, 
						sprite.getWidth()*sprite.getScaleX(), sprite.getHeight()*sprite.getScaleY());
			}else{
				batch.draw(sprite , mousePos.x-68, mousePos.y-32, 
						sprite.getWidth()*sprite.getScaleX(), sprite.getHeight()*sprite.getScaleY());
			}
			return;
		}
		
		batch.draw(sprite, mousePos.x, mousePos.y, 
				sprite.getWidth()*sprite.getScaleX(), sprite.getHeight()*sprite.getScaleY());
		batch.end();
	}
	
	public void rotateSelection(){
		Sprite[] sprites = entityRenderer.getSprites().get(entityName);
		
		if(spriteIndex == sprites.length){
			spriteIndex = 0;
		}else{
			spriteIndex++;
		}
	}
	
	public void dragPlace(Vector2 start, Vector2 widthHeight) {}
}
