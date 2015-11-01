package com.ado.trader.placement;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.entities.WallDirection;
import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.SpriteComp;
import com.ado.trader.entities.components.WallSprite;
import com.ado.trader.map.Chunk;
import com.ado.trader.map.Map;
import com.ado.trader.rendering.EntityRenderSystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class WallPlaceable extends Placeable{
	ComponentMapper<WallSprite> wallMap;
	String entityName;
	WallDirection first, second;
	
	public WallPlaceable(Map map, EntityRenderSystem entityRenderer) {
		super(map, entityRenderer);
		
		wallMap = map.getWorld().getMapper(WallSprite.class);
	}

	public void place(int mapX, int mapY) {
		Chunk c = map.getChunk(mapX, mapY);
		Vector2 t = Map.worldVecToTile(mapX, mapY);
		
		if(!c.getWalls().isOccupied((int) t.x, (int) t.y)){
			
			Entity e = EntityFactory.createEntity(entityName);
			
			WallSprite w = e.getComponent(WallSprite.class);
			
			w.firstSprite = first;
			
			if(second!=null){
				e.getComponent(WallSprite.class).secondSprite = second;
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
				}
			}
		}
	}
	private void changeExistingSprite(int tileX, int tileY, Chunk c){
		if(c.getWalls().map[tileX][tileX] == null) return;
		
		Entity e = map.getWorld().getEntity(c.getWalls().map[tileX][tileX]);
		
		WallSprite w = wallMap.get(e);
		
		if(w.secondSprite == null){
			w.secondSprite = first;
		}else{
			w.firstSprite = w.secondSprite;
			w.secondSprite = first;
		}
	}
	
	//maps click-dragged square to proper sprites
	private void selectCorrectDirection(int x,int y,Vector2 start,Vector2 widthHeight){
		Sprite[] sprites = entityRenderer.getSpriteManager().getWallSprites(key);
		
		if(sprites[1] == null)return;
		
		if(x==start.x&&y==start.y){		//s
			first = WallDirection.SW;
			second = WallDirection.SE;
		}else if(x==start.x&&y==widthHeight.y){		//w
			first = WallDirection.SW;
			second = WallDirection.NW;
		}else if(x==widthHeight.x&&y==widthHeight.y){		//n
			first = WallDirection.NE;
			second = WallDirection.NW;
		}else if(x==widthHeight.x&&y==start.y){		//e
			first = WallDirection.NE;
			second = WallDirection.SE;
		}else if(x<=widthHeight.x&&y==start.y){		//se
			first = WallDirection.SE;
		}else if(x<=widthHeight.x&&y==widthHeight.y){		//nw
			first = WallDirection.NW;
		}else if(y<=widthHeight.y&&x==start.x){				//sw
			first = WallDirection.SW;
		}else if(y<=widthHeight.y&&x==widthHeight.x){		//ne
			first = WallDirection.NE;
		}
	}
	
	public void rotateSelection(){
		switch(first){
		case NE:
			first = WallDirection.SE;
			break;
		case NW:
			first = WallDirection.SW;
			break;
		case SW:
			first = WallDirection.NE;
			break;
		case SE:
			first = WallDirection.NW;
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
		first = WallDirection.SW;
		second = null;
	}
	public void renderPreview(SpriteBatch batch) {
		
	}
}
