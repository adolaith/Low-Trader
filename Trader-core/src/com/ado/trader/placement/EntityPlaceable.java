package com.ado.trader.placement;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.entities.components.Area;
import com.ado.trader.entities.components.Position;
import com.ado.trader.gui.CreateNpcWindow;
import com.ado.trader.input.InputHandler;
import com.ado.trader.rendering.EntityRenderSystem;
import com.ado.trader.utils.GameServices;
import com.ado.trader.utils.IsoUtils;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class EntityPlaceable extends Placeable {
	public int entityTypeID, spriteId;
	Sprite sprite;
	ComponentMapper<Area> areaMapper;
	CreateNpcWindow createNpcWindow;
	EntityFactory entities;
	
	public EntityPlaceable(GameServices gameRes) {
		super(gameRes.getMap());
		this.entities = gameRes.getEntities();
		this.createNpcWindow = new CreateNpcWindow(gameRes);
		areaMapper = gameRes.getWorld().getMapper(Area.class);
	}
	
	public void place(int x,int y){
		if(entityTypeID == 0 && createNpcWindow != null){
			createNpcWindow.showWindow((int)InputHandler.getIsoClicked().x, (int)InputHandler.getIsoClicked().y);
			return;
		}
		Sprite s = new Sprite(sprite);
		Entity e = EntityFactory.createEntity(entityTypeID,spriteId , s);
		e.getComponent(Position.class).setPosition(x, y, map.currentLayer);

		rotateArea(e);
		
		map.getEntityLayer().addToMap(e.getId(), x, y, map.currentLayer);
		map.getEntityLayer().markAreaOccupied(x, y, map.currentLayer, e, map.getEntityLayer());
		
		if(sprite.isFlipX()){
			sprite.flip(true, false);
		}
	}
	
	public void remove(int x, int y){
		if(map.getEntityLayer().isOccupied(x, y, map.currentLayer)){
			EntityFactory.deleteEntity(x,y, map.currentLayer, map.getEntityLayer());
		}
	}
	
	private void rotateArea(Entity e){
		if (areaMapper.has(e)) {
			if (sprite.isFlipX()) {
				for (Vector2 vec : areaMapper.get(e).area) {
					vec.rotate90(1);
				}
			}
		}
	}
	public void renderPreview(SpriteBatch batch){
		if(delete || entityTypeID == 0)return;
		
		Vector2 mousePos = IsoUtils.getColRow((int)InputHandler.getMousePos().x, (int)InputHandler.getMousePos().y,
				map.getTileWidth(), map.getTileHeight());
		
		mousePos = IsoUtils.getIsoXY((int)mousePos.x, (int)mousePos.y, 
				map.getTileWidth(), map.getTileHeight());
		
		batch.begin();
		if(entities.getEntities().get(entityTypeID).containsKey("area")){
			if(sprite.isFlipX()){
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
	
	public void rotateSelection(EntityRenderSystem entityRenderer){
		String[] tmp = entities.getEntities().get(entityTypeID).get("sprite").split(",");
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
	
	public void dragPlace(Vector2 start, Vector2 widthHeight) {}
}
