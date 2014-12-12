package com.ado.trader.utils.placement;

import com.ado.trader.entities.components.Area;
import com.ado.trader.entities.components.Position;
import com.ado.trader.map.IntMapLayer;
import com.ado.trader.screens.GameScreen;
import com.ado.trader.utils.InputHandler;
import com.ado.trader.utils.IsoUtils;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class EntityPlaceable extends Placeable {
	public int entityTypeID, spriteId;
	Sprite sprite;
	
	public EntityPlaceable(GameScreen game) {
		super(game);
	}
	
	public void place(int x,int y){
		if(entityTypeID == 0){
			game.getGui().getEntityCustom().showWindow((int)game.getInput().getIsoClicked().x, (int)game.getInput().getIsoClicked().y);
			return;
		}
		Sprite s = new Sprite(sprite);
		Entity e = game.getEntities().createEntity(entityTypeID,spriteId , s);
		e.getComponent(Position.class).setPosition(x, y, game.getMap().currentLayer);

		rotateArea(e, game);
		
		game.getMap().getCurrentLayerGroup().entityLayer.addToMap(e.getId(), x, y);
		markAreaOccupied(x, y, e, game.getMap().getCurrentLayerGroup().entityLayer);
		
		if(sprite.isFlipX()){
			sprite.flip(true, false);
		}
	}
	
	public void remove(int x, int y){
		if(game.getMap().getCurrentLayerGroup().entityLayer.isOccupied(x, y)){
			game.getEntities().deleteEntity(x,y, game.getMap().getCurrentLayerGroup().entityLayer);
		}
	}
	
	public void markAreaOccupied(int x, int y, Entity e, IntMapLayer layer){
		ComponentMapper<Area> areaM = game.getWorld().getMapper(Area.class);
		if (!areaM.has(e)) return;
		Area a = areaM.get(e);
		for(Vector2 vec: a.area){
			layer.addToMap(e.getId(), (int)(x+vec.x), (int)(y+vec.y));
		}
	}
	private void rotateArea(Entity e, GameScreen game){
		ComponentMapper<Area> areaM = game.getWorld().getMapper(Area.class);
		if (areaM.has(e)) {
			if (sprite.isFlipX()) {
				for (Vector2 vec : areaM.get(e).area) {
					vec.rotate90(1);
				}
			}
		}
	}
	public void renderPreview(SpriteBatch batch){
		if(delete || entityTypeID == 0)return;
		InputHandler input = game.getInput();
		
		Vector2 mousePos = IsoUtils.getColRow((int)input.getMousePos().x, (int)input.getMousePos().y,
				game.getMap().getTileWidth(), game.getMap().getTileHeight());
		
		mousePos = IsoUtils.getIsoXY((int)mousePos.x, (int)mousePos.y, 
				game.getMap().getTileWidth(), game.getMap().getTileHeight());
		
		batch.begin();
		if(game.getEntities().getEntities().get(entityTypeID).containsKey("area")){
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
	
	public void rotateSelection(){
		String[] tmp = game.getEntities().getEntities().get(entityTypeID).get("sprite").split(",");
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
				sprite = game.getRenderer().getRenderEntitySystem().getStaticSprites().get(i);
				break;
			}
		}else{
			sprite.flip(true, false);
		}
	}
	
	public void dragPlace(Vector2 start, Vector2 widthHeight) {}
}
