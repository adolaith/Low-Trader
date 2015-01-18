package com.ado.trader.placement;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.SpriteComp;
import com.ado.trader.entities.components.Wall;
import com.ado.trader.map.Map;
import com.ado.trader.map.WallLayer;
import com.ado.trader.rendering.EntityRenderSystem;
import com.ado.trader.rendering.EntityRenderSystem.Direction;
import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;

public class WallPlaceable extends Placeable{
	int entityTypeID, firstId, secondId;
	Sprite firstSprite, secondSprite;
	Direction first, second;
	EntityRenderSystem entityRenderer;
	EntityFactory entities;
	
	public WallPlaceable(Map map, EntityFactory entities, EntityRenderSystem entityRenderer) {
		super(map);
		this.entities = entities;
		this.entityRenderer = entityRenderer;
	}

	public void place(int x, int y) {
		WallLayer wLayer = map.getWallLayer();
		if(!wLayer.isOccupied(x, y, map.currentLayer)){
			Sprite s = new Sprite(firstSprite);
			Entity e = entities.createEntity(entityTypeID,firstId, s);
			Wall w = e.getComponent(Wall.class);
			
			w.firstSprite = first;
			if(secondSprite!=null){
				s = new Sprite(secondSprite);
				e.getComponent(SpriteComp.class).secondId = secondId;
				e.getComponent(SpriteComp.class).secondarySprite = s;
				e.getComponent(Wall.class).secondSprite = second;
			}
			e.getComponent(Position.class).setPosition(x, y, map.currentLayer);
			wLayer.map[x][y][map.currentLayer] = e.getId();
		}else{
			changeExistingSprite(x, y);
		}
		resetDirections();
	}

	public void dragPlace(Vector2 start, Vector2 widthHeight) {
		for(int x=(int) start.x;x<widthHeight.x+1; x++){
			for(int y=(int) start.y;y<widthHeight.y+1; y++){
				if((x<=widthHeight.x&&y==start.y||y==widthHeight.y)
						||(y<=widthHeight.y&&x==start.x||x==widthHeight.x)){	//outline check
					if(!map.getWallLayer().isOccupied(x, y, map.currentLayer)){
						selectCorrectDirection(x,y,start,widthHeight);
						place(x, y);
					}else{
						changeExistingSprite(x, y);
					}
				}
			}
		}
	}
	private void changeExistingSprite(int x, int y){
		ArrayMap<Integer, Sprite> spriteList = entityRenderer.getStaticSprites();
		Entity e = map.getWorld().getEntity(map.getWallLayer().map[x][y][map.currentLayer]);
		SpriteComp s = e.getComponent(SpriteComp.class);
		Wall w = e.getComponent(Wall.class);
		if(s.secondarySprite==null){
			s.secondId = firstId;
			s.secondarySprite = new Sprite(spriteList.get(firstId));
			w.secondSprite = first;
		}else{
			s.mainId = s.secondId;
			s.mainSprite = s.secondarySprite;
			w.firstSprite = w.secondSprite;
			s.secondId = firstId;
			s.secondarySprite = new Sprite(spriteList.get(firstId));
			w.secondSprite = first;
		}
	}
	//returns an entity with the correct sprite selected when click+dragged
	private void selectCorrectDirection(int x,int y,Vector2 start,Vector2 widthHeight){
		String[] tmp = entities.getEntities().get(entityTypeID).get("sprite").split(",");
		ArrayMap<Integer, Sprite> spriteList = entityRenderer.getStaticSprites();
		if(tmp[1].isEmpty())return;
		
		if(x==start.x&&y==start.y){		//s
			firstId = Integer.valueOf(tmp[0]);
			firstSprite = spriteList.get(firstId);
			first = Direction.SW;
			secondId = Integer.valueOf(tmp[1]);
			secondSprite = spriteList.get(secondId);
			second = Direction.SE;
		}else if(x==start.x&&y==widthHeight.y){		//w
			firstId = Integer.valueOf(tmp[0]);
			firstSprite = spriteList.get(firstId);
			first = Direction.SW;
			secondId = Integer.valueOf(tmp[1]);
			secondSprite = spriteList.get(secondId);
			second = Direction.NW;
		}else if(x==widthHeight.x&&y==widthHeight.y){		//n
			firstId = Integer.valueOf(tmp[0]);
			firstSprite = spriteList.get(firstId);
			first = Direction.NE;
			secondId = Integer.valueOf(tmp[1]);
			secondSprite = spriteList.get(secondId);
			second = Direction.NW;
		}else if(x==widthHeight.x&&y==start.y){		//e
			firstId = Integer.valueOf(tmp[0]);
			firstSprite = spriteList.get(firstId);
			first = Direction.NE;
			secondId = Integer.valueOf(tmp[1]);
			secondSprite = spriteList.get(secondId);
			second = Direction.SE;
		}else if(x<=widthHeight.x&&y==start.y){		//se
			firstId = Integer.valueOf(tmp[1]);
			firstSprite = spriteList.get(firstId);
			first = Direction.SE;
		}else if(x<=widthHeight.x&&y==widthHeight.y){		//nw
			firstId = Integer.valueOf(tmp[1]);
			firstSprite = spriteList.get(firstId);
			first = Direction.NW;
		}else if(y<=widthHeight.y&&x==start.x){				//sw
			firstId = Integer.valueOf(tmp[0]);
			firstSprite = spriteList.get(firstId);
			first = Direction.SW;
		}else if(y<=widthHeight.y&&x==widthHeight.x){		//ne
			firstId = Integer.valueOf(tmp[0]);
			firstSprite = spriteList.get(firstId);
			first = Direction.NE;
		}
	}
	public void rotateSelection(EntityRenderSystem entityRenderer){
		String[] tmp = entities.getEntities().get(entityTypeID).get("sprite").split(",");
		ArrayMap<Integer, Sprite> spriteList = entityRenderer.getStaticSprites();
		if(tmp[1].isEmpty())return;
		
		switch(first){
		case NE:
			firstId = Integer.valueOf(tmp[1]);
			firstSprite = spriteList.get(firstId);
			first = Direction.SE;
			break;
		case NW:
			firstId = Integer.valueOf(tmp[0]);
			firstSprite = spriteList.get(firstId);
			first = Direction.SW;
			break;
		case SW:
			first = Direction.NE;
			break;
		case SE:
			first = Direction.NW;
			break;
		}
	}
	public void remove(int x, int y) {
		if(!delete){return;}
		if(map.getWallLayer().isOccupied(x, y, map.currentLayer)){
			entities.deleteEntity(x,y, map.currentLayer, map.getWallLayer());
		}
		if(!Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)){
			delete^=delete;
		}
	}
	public void resetDirections(){
		first = Direction.SW;
		second = null;
		secondSprite = null;
	}
	public void renderPreview(SpriteBatch batch) {
	}
}
