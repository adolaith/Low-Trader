package com.ado.trader.placement;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.entities.components.Area;
import com.ado.trader.entities.components.Position;
import com.ado.trader.gui.CreateNpcWindow;
import com.ado.trader.input.InputHandler;
import com.ado.trader.map.Chunk;
import com.ado.trader.utils.GameServices;
import com.ado.trader.utils.IsoUtils;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;

public class EntityPlaceable extends Placeable {
	public String entityName;
	public int spriteIndex;
	
	ComponentMapper<Area> areaMapper;
	ComponentMapper<Position> positionMapper;
	
	CreateNpcWindow createNpcWindow;
	EntityFactory entities;
	
	public EntityPlaceable(GameServices gameRes) {
		super(gameRes.getMap(), gameRes.getRenderer().getRenderEntitySystem());
		this.entities = gameRes.getEntities();
		
		areaMapper = map.getWorld().getMapper(Area.class);
		positionMapper = map.getWorld().getMapper(Position.class);
		
//		this.createNpcWindow = new CreateNpcWindow(gameRes);
	}
	
	public void place(int mapX,int mapY){
		JsonValue profile = entities.getEntityData().get(entityName);
		
//		if(profile.has("animation")){
//			createNpcWindow.showWindow((int)InputHandler.getIsoClicked().x, (int)InputHandler.getIsoClicked().y);
//			return;
//		}
		
		Entity e = EntityFactory.createEntity(entityName, spriteIndex);
		
		Chunk c = map.getChunk(mapX, mapY);
		Vector2 tile = map.worldVecToTile(mapX, mapY);
		
		positionMapper.get(e).setPosition(mapX, mapY);

		rotateArea(e);
		
		c.getEntities().addToMap(e.getId(), (int) tile.x, (int) tile.y);
		c.getEntities().markAreaOccupied((int) tile.x, (int) tile.y, e, c.getEntities());
		
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
			batch.end();
			return;
		}
		
		batch.draw(sprite, mousePos.x, mousePos.y, 
				sprite.getWidth() * sprite.getScaleX(), sprite.getHeight() * sprite.getScaleY());
		batch.end();
	}
	
	public void rotateSelection(){
		Sprite[] sprites = entityRenderer.getSprites().get(entityName);
		
		if(spriteIndex < sprites.length ){
			if(sprites[spriteIndex + 1] != null){
				spriteIndex++;
				return;
			}
		}
		spriteIndex = 0;
	}
	
	public void dragPlace(Vector2 start, Vector2 widthHeight) {}

	@Override
	void clearSettings() {
		spriteIndex = 0;
		entityName = null;
	}
}
