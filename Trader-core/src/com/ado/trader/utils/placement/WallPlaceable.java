package com.ado.trader.utils.placement;

import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.SpriteComp;
import com.ado.trader.entities.components.Wall;
import com.ado.trader.map.WallLayer;
import com.ado.trader.screens.GameScreen;
import com.ado.trader.systems.EntityRenderSystem.Direction;
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
	
	public WallPlaceable(GameScreen game) {
		super(game);
	}

	public void place(int x, int y) {
		WallLayer wLayer = game.getMap().getCurrentLayerGroup().wallLayer;
		if(!wLayer.isOccupied(x, y)){
			Sprite s = new Sprite(firstSprite);
			Entity e = game.getEntities().createEntity(entityTypeID,firstId, s);
			Wall w = e.getComponent(Wall.class);
			
			w.firstSprite = first;
			if(secondSprite!=null){
				s = new Sprite(secondSprite);
				e.getComponent(SpriteComp.class).secondId = secondId;
				e.getComponent(SpriteComp.class).secondarySprite = s;
				e.getComponent(Wall.class).secondSprite = second;
			}
			e.getComponent(Position.class).setPosition(x, y, game.getMap().currentLayer);
			wLayer.map[x][y] = e.getId();
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
					if(!game.getMap().getCurrentLayerGroup().wallLayer.isOccupied(x, y)){
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
		ArrayMap<Integer, Sprite> spriteList = game.getRenderer().getRenderEntitySystem().getStaticSprites();
		Entity e = game.getWorld().getEntity(game.getMap().getCurrentLayerGroup().wallLayer.map[x][y]);
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
		String[] tmp = game.getEntities().getEntities().get(entityTypeID).get("sprite").split(",");
		ArrayMap<Integer, Sprite> spriteList = game.getRenderer().getRenderEntitySystem().getStaticSprites();
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
	public void rotateSelection(){
		String[] tmp = game.getEntities().getEntities().get(entityTypeID).get("sprite").split(",");
		ArrayMap<Integer, Sprite> spriteList = game.getRenderer().getRenderEntitySystem().getStaticSprites();
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
		if(game.getMap().getCurrentLayerGroup().wallLayer.isOccupied(x, y)){
			game.getEntities().deleteEntity(x,y, game.getMap().getCurrentLayerGroup().wallLayer);
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
