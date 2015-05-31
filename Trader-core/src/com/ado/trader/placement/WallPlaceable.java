package com.ado.trader.placement;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.SpriteComp;
import com.ado.trader.entities.components.Wall;
import com.ado.trader.map.Chunk;
import com.ado.trader.map.Map;
import com.ado.trader.rendering.EntityRenderSystem;
import com.ado.trader.rendering.EntityRenderSystem.Direction;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class WallPlaceable extends Placeable{
	ComponentMapper<SpriteComp> spriteMap;
	ComponentMapper<Wall> wallMap;
	String entityName;
	Integer firstSprite, secondSprite;
	Direction first, second;
	EntityFactory entities;
	
	public WallPlaceable(Map map, EntityFactory entities, EntityRenderSystem entityRenderer) {
		super(map, entityRenderer);
		this.entities = entities;
		
		spriteMap = map.getWorld().getMapper(SpriteComp.class);
		wallMap = map.getWorld().getMapper(Wall.class);
	}

	public void place(int mapX, int mapY) {
		Chunk c = map.getChunk(mapX, mapY);
		Vector2 t = map.worldVecToTile(mapX, mapY);
		
		if(!c.getWalls().isOccupied((int) t.x, (int) t.y)){
			Entity e = EntityFactory.createEntity(entityName, firstSprite);
			Wall w = e.getComponent(Wall.class);
			
			w.firstSprite = first;
			
			if(secondSprite!=null){
				e.getComponent(SpriteComp.class).secondSprite = secondSprite;
				e.getComponent(Wall.class).secondSprite = second;
			}
			
			e.getComponent(Position.class).setPosition((int) t.x, (int) t.y);
			c.getWalls().map[(int) t.x][(int) t.y] = e.getId();
			
		}else{
			changeExistingSprite((int) t.x, (int) t.y, c);
		}
		clearSettings();
	}

	public void dragPlace(Vector2 start, Vector2 widthHeight) {
		for(int x=(int) start.x;x<widthHeight.x+1; x++){
			for(int y=(int) start.y;y<widthHeight.y+1; y++){
				if((x<=widthHeight.x&&y==start.y||y==widthHeight.y)
						||(y<=widthHeight.y&&x==start.x||x==widthHeight.x)){	//outline check
					
					selectCorrectDirection(x,y,start,widthHeight);
					place(x, y);
//					
//					if(!map.getWallLayer().isOccupied(x, y, map.currentLayer)){
//						selectCorrectDirection(x,y,start,widthHeight);
//						place(x, y);
//					}else{
//						changeExistingSprite(x, y);
//					}
				}
			}
		}
	}
	private void changeExistingSprite(int tileX, int tileY, Chunk c){
		if(c.getWalls().map[tileX][tileX] == null) return;
		
		Entity e = map.getWorld().getEntity(c.getWalls().map[tileX][tileX]);
		
		SpriteComp s = spriteMap.get(e);
		Wall w = wallMap.get(e);
		
		if(s.secondSprite == null){
			s.secondSprite = firstSprite;
			w.secondSprite = first;
		}else{
			s.mainSprite = s.secondSprite;
			w.firstSprite = w.secondSprite;
			s.secondSprite = firstSprite;
			w.secondSprite = first;
		}
	}
	
	//maps click-dragged square to proper sprites
	private void selectCorrectDirection(int x,int y,Vector2 start,Vector2 widthHeight){
		Sprite[] sprites = entityRenderer.getSprites().get(entityName);
		
		if(sprites[1] == null)return;
		
		if(x==start.x&&y==start.y){		//s
			firstSprite = 0;
			first = Direction.SW;
			secondSprite = 1;
			second = Direction.SE;
		}else if(x==start.x&&y==widthHeight.y){		//w
			firstSprite = 0;
			first = Direction.SW;
			secondSprite = 1;
			second = Direction.NW;
		}else if(x==widthHeight.x&&y==widthHeight.y){		//n
			firstSprite = 0;
			first = Direction.NE;
			secondSprite = 1;
			second = Direction.NW;
		}else if(x==widthHeight.x&&y==start.y){		//e
			firstSprite = 0;
			first = Direction.NE;
			secondSprite = 1;
			second = Direction.SE;
		}else if(x<=widthHeight.x&&y==start.y){		//se
			firstSprite = 1;
			first = Direction.SE;
		}else if(x<=widthHeight.x&&y==widthHeight.y){		//nw
			firstSprite = 1;
			first = Direction.NW;
		}else if(y<=widthHeight.y&&x==start.x){				//sw
			firstSprite = 0;
			first = Direction.SW;
		}else if(y<=widthHeight.y&&x==widthHeight.x){		//ne
			firstSprite = 0;
			first = Direction.NE;
		}
	}
	
	public void rotateSelection(){
		switch(first){
		case NE:
			firstSprite = 1;
			first = Direction.SE;
			break;
		case NW:
			firstSprite = 0;
			first = Direction.SW;
			break;
		case SW:
			firstSprite = 0;
			first = Direction.NE;
			break;
		case SE:
			firstSprite = 1;
			first = Direction.NW;
			break;
		}
	}
	public void remove(int x, int y) {
//		if(!delete){return;}
//		if(map.getWallLayer().isOccupied(x, y, map.currentLayer)){
//			EntityFactory.deleteEntity(x,y, map.currentLayer, map.getWallLayer());
//		}
//		if(!Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)){
//			delete^=delete;
//		}
	}
	public void clearSettings(){
		first = Direction.SW;
		firstSprite = 0;
		second = null;
		secondSprite = null;
	}
	public void renderPreview(SpriteBatch batch) {
		
	}
}
