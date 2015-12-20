package com.ado.trader.placement;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.entities.components.Area;
import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.SpriteComp;
import com.ado.trader.input.InputHandler;
import com.ado.trader.map.Chunk;
import com.ado.trader.map.Map;
import com.ado.trader.utils.FileLogger;
import com.ado.trader.utils.GameServices;
import com.ado.trader.utils.IsoUtils;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;

public class EntityPlaceable extends Placeable {
	String baseId;
	int spriteIndex;
	
	JsonValue profile;
	
	ComponentMapper<Position> positionMapper;
	ComponentMapper<Area> areaMapper;
	ComponentMapper<SpriteComp> spriteMapper;
	
	public EntityPlaceable(GameServices gameRes) {
		super(gameRes.getMap(), gameRes.getRenderer().getRenderEntitySystem());
		
		areaMapper = map.getWorld().getMapper(Area.class);
		positionMapper = map.getWorld().getMapper(Position.class);
		spriteMapper = map.getWorld().getMapper(SpriteComp.class);
	}
	
	public void place(int mapX,int mapY){
		Entity e = EntityFactory.createEntity(baseId);
		
		//change sprite
		SpriteComp sprite = spriteMapper.get(e); 
		if(sprite.spriteIndex != spriteIndex){
			sprite.spriteIndex = spriteIndex;
		}
		
		Chunk c = map.getChunk(mapX, mapY);
		Vector2 tile = Map.worldVecToTile(mapX, mapY);
		
		positionMapper.get(e).setPosition((int) tile.x, (int) tile.y);

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
		if(delete || !profile.has("sprite"))return;
		
		Vector2 mousePos = IsoUtils.getColRow((int)InputHandler.getMousePos().x, (int)InputHandler.getMousePos().y,
				map.getTileWidth(), map.getTileHeight());
		
		mousePos = IsoUtils.getIsoXY((int)mousePos.x, (int)mousePos.y, 
				map.getTileWidth(), map.getTileHeight());
		
		String spriteName = profile.get("sprite").asStringArray()[0];
		Sprite sprite = entityRenderer.getSpriteManager().getEntitySprites(spriteName)[spriteIndex];
		
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
		String spriteName = profile.get("sprite").asStringArray()[0];
		Sprite[] sprites = entityRenderer.getSpriteManager().getEntitySprites(spriteName);
		
		if(spriteIndex < sprites.length ){
			if(sprites[spriteIndex + 1] != null){
				spriteIndex++;
				return;
			}
		}
		spriteIndex = 0;
	}
	
	public void setSelection(String baseId){
		this.baseId = baseId;
		this.spriteIndex = 0;
		
		String[] idSplit = baseId.split("\\.");
		
		profile = EntityFactory.getEntityData().get(idSplit[0]).get(idSplit[1]);
	}
	
	public void dragPlace(Vector2 start, Vector2 widthHeight) {}

	@Override
	public void clearSettings() {
		spriteIndex = 0;
		baseId = null;
		profile = null;
	}
}
