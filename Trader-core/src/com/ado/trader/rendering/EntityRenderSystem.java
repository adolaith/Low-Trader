package com.ado.trader.rendering;

import com.ado.trader.entities.components.Animation;
import com.ado.trader.entities.components.Area;
import com.ado.trader.entities.components.Feature;
import com.ado.trader.entities.components.Mask;
import com.ado.trader.entities.components.Name;
import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.SpriteComp;
import com.ado.trader.entities.components.Status;
import com.ado.trader.entities.components.Wall;
import com.ado.trader.map.Map;
import com.ado.trader.systems.StatusIconSystem.StatusIcon;
import com.ado.trader.utils.IsoUtils;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonBounds;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.SkeletonRendererDebug;

//Handles rendering of all active Entities. 
//Renders next entity selected for placement at the mouses location.
@Wire
public class EntityRenderSystem{
	ComponentMapper<Position> posMapper;
	ComponentMapper<SpriteComp> spriteMapper;
	ComponentMapper<Wall> wallMapper;
	ComponentMapper<Area> areaMapper;
	ComponentMapper<Mask> maskMapper;
	ComponentMapper<Feature> featureMapper;
	ComponentMapper<Animation> animMapper;
	ComponentMapper<Status> statusMapper;
	ComponentMapper<Name> nameMapper;
	
	Map map;
	SkeletonRenderer skeletonRenderer;
	SkeletonRendererDebug debugRenderer;
	ArrayMap<String, Sprite[]> entitySprites;
	MaskingSystem masks;

	public EntityRenderSystem(Map map, MaskingSystem masks) {
		this.map = map;
		this.masks = masks;
		
		skeletonRenderer = new SkeletonRenderer();
		debugRenderer = new SkeletonRendererDebug();
		skeletonRenderer.setPremultipliedAlpha(true);
	}
	
	int x, y;
	SkeletonBounds bounds = new SkeletonBounds();
	
	//renders entities from the rear of the map to front, avoiding sprite overlap caused by isometric view
	public void renderEntities(SpriteBatch batch, OrthographicCamera camera){
		debugRenderer.getShapeRenderer().setProjectionMatrix(camera.combined);
		
		int sum = map.getWidthInTiles()+map.getHeightInTiles()-2;
		if(batch.isDrawing()){
			batch.end();
		}
		batch.begin();
		for(int count = sum; count >= 0; count--){		//DEPTH COUNTER
			for(y = map.getHeightInTiles() - 1; y >= 0; y--){
				for(x = map.getWidthInTiles() - 1; x >= 0; x--){		//DIAGONAL MAP READ
					if(x + y - count == 0){
						if(drawWideEntity(batch))continue;
						
						renderNorthernWall(x, y, batch);
						
						if(map.getItemLayer().isOccupied(x, y, map.currentLayer)){
							Item i = map.getItemLayer().map[x][y][map.currentLayer];
							Vector2 itemPos = IsoUtils.getIsoXY(x, y, map.getTileWidth(), map.getTileHeight());
							itemPos.add(map.getTileWidth()/2, map.getTileHeight()/3);
							ItemSprite item = i.getData(ItemSprite.class);
							batch.draw(item.sprite, itemPos.x-item.sprite.getWidth(), itemPos.y, 
									item.sprite.getWidth()*item.sprite.getScaleX(),item.sprite.getHeight()*item.sprite.getScaleY());
						}
						
						if(map.getEntityLayer().isOccupied(x, y, map.currentLayer)){
							Entity e = map.getWorld().getEntity(map.getEntityLayer().map[x][y][map.currentLayer]);

							if(spriteMapper.has(e)){		//RENDER STATIC ENTITY
								drawSprite(e,batch);
							}else if(animMapper.has(e)){		//RENDER ANIMATED(NPC) ENTITY
								Skeleton skel = animMapper.get(e).getSkeleton();
								skeletonRenderer.draw(batch,skel);
								
								//render status icons
								if(statusMapper.has(e)){
									StatusIcon icon = statusMapper.get(e).getStatusIcon();
									Vector2 iconPos = new Vector2(skel.getX(), skel.getY());
									bounds.update(skel, true);
									iconPos.y += bounds.getHeight() + 4;
									icon.drawIcon(batch, iconPos);
								}
							}
						}
						renderSouthernWall(x, y, batch);
					}
				}
			}
		}
		batch.end();
	}
	private boolean drawWideEntity(SpriteBatch batch){
		//check current tile for entity
		if(map.getEntityLayer().isOccupied(x, y, map.currentLayer)){
			Entity e = map.getWorld().getEntity(map.getEntityLayer().map[x][y][map.currentLayer]);
			Position p = posMapper.get(e);
			
			//entity is larger than 1 tile?
			if(areaMapper.has(e)){
				//the current tile is the entity's origin/anchor tile?
				if(x==p.getX()&&y==p.getY()){
					Area a = areaMapper.get(e);
					SpriteComp s = spriteMapper.get(e);
					
					//get entity's main sprite
					Sprite tmp = entitySprites.get(nameMapper.get(e).getName())[s.mainSprite];
					
					//render next entity to prevent drawing overlap
					if(tmp.isFlipX()){
						if(map.getEntityLayer().isOccupied(x+1, y-1, map.currentLayer)){
							Entity next = map.getWorld().getEntity(map.getEntityLayer().map[x+1][y-1][map.currentLayer]);
							drawSprite(next, batch);
							this.x--;
							this.y--;
						}
					}
					
					//render any NE, NW walls for current tile
					renderNorthernWall(p.getX(), p.getY(), batch);
					
					//render NE, NW walls for other entity occupied tiles
					for(Vector2 vec:a.area){			
						int aX = (int)(vec.x+p.getX());
						int aY = (int)(vec.y+p.getY());
						renderNorthernWall(aX, aY, batch);
					}
					
					//draw wide sprite offset to the bottom left occupied tile(eg [0,0],[1,1])
					if(tmp.isFlipX()){		
						batch.draw(tmp , (int)p.getIsoPosition().x-4, (int)p.getIsoPosition().y-32,
								tmp.getWidth()*tmp.getScaleX(),tmp.getHeight()*tmp.getScaleY());
					}else{
					//draw wide sprite offset to the top left occupied tile(eg [0,1],[1,0])

						batch.draw(tmp , (int)p.getIsoPosition().x-68, (int)p.getIsoPosition().y-32,
								tmp.getWidth()*tmp.getScaleX(),tmp.getHeight()*tmp.getScaleY());
					}
					
					//render any SE, SW walls for current tile
					renderSouthernWall(p.getX(), p.getY(), batch);
					
					//render SE, SW walls for other entity occupied tiles
					for(Vector2 vec:a.area){
						int aX = (int)(vec.x+p.getX());
						int aY = (int)(vec.y+p.getY());

						renderSouthernWall(aX, aY, batch);
					}
				}
				return true;
			}
		}
		return false;
	}
	//draw decorating sprite(lamps/paintings on wall, windows)
	private void drawFeature(Entity e, Position p, SpriteBatch batch){
		if(!featureMapper.has(e))return;
		
		Feature f = featureMapper.get(e);
		Sprite s = entitySprites.get(f.featureName)[f.spriteIndex];
		batch.draw(s, p.getIsoPosition().x, p.getIsoPosition().y, s.getWidth() * s.getScaleX(), s.getHeight() * s.getScaleY());
	}

	private void drawNorthSprite(Direction dir, int spriteIndex, Entity e, SpriteBatch batch){
		if(dir==null)return;
		
		Position p = posMapper.get(e);
		Mask m = null;
		
		if(maskMapper.has(e)){
			m = maskMapper.get(e);
		}
		
		Sprite s = entitySprites.get(nameMapper.get(e).getName())[spriteIndex];
		
		Vector2 vec = null;
		
		switch(dir){
		case NE:
			vec = new Vector2((int)p.getIsoPosition().x+(30*s.getScaleX()), (int)p.getIsoPosition().y+(16*s.getScaleY()));
			masks.drawMask(batch, 0, vec, s.getHeight(), p, m);
			batch.draw(s , vec.x, vec.y,s.getWidth()*s.getScaleX(),s.getHeight()*s.getScaleY());
			
			break;
		case NW:
			vec = new Vector2((int)p.getIsoPosition().x-4, (int)p.getIsoPosition().y+(16*s.getScaleY()));
			masks.drawMask(batch, 1, vec, s.getHeight(), p, m);
			batch.draw(s , vec.x, vec.y,s.getWidth()*s.getScaleX(),s.getHeight()*s.getScaleY());
			
			break;
		}
		batch.flush();
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	private void renderNorthernWall(int x, int y, SpriteBatch batch){
		if(!map.getWallLayer().isOccupied(x, y, map.currentLayer)) return;
		
		//wall entity
		Entity e = map.getWorld().getEntity(map.getWallLayer().map[x][y][map.currentLayer]);
		
		Wall w = wallMapper.get(e);
		
		//check if walls are north facing
		if(!(w.firstSprite==Direction.NE||w.firstSprite==Direction.NW)&&!
				(w.secondSprite==Direction.NE||w.secondSprite==Direction.NW))return;
				
		SpriteComp sC = spriteMapper.get(e);
		
		drawNorthSprite(w.firstSprite, sC.mainSprite, e, batch);
		drawNorthSprite(w.secondSprite, sC.secondSprite, e, batch);
		drawFeature(e, posMapper.get(e), batch);
	}
	
	private void renderSouthernWall(int x, int y, SpriteBatch batch){
		if(!map.getWallLayer().isOccupied(x, y, map.currentLayer)) return;
		
		Entity e = map.getWorld().getEntity(map.getWallLayer().map[x][y][map.currentLayer]);
		Wall w = wallMapper.get(e);
		
		//check if walls are south facing
		if(!(w.firstSprite==Direction.SE||w.firstSprite==Direction.SW)&&!
				(w.secondSprite==Direction.SE||w.secondSprite==Direction.SW))return;
		
		SpriteComp sC = spriteMapper.get(e);
		
		drawSouthSprite(w.firstSprite, sC.mainSprite, e, batch);
		drawSouthSprite(w.secondSprite, sC.secondSprite, e, batch);
		drawFeature(e, posMapper.get(e), batch);
	}
	private void drawSouthSprite(Direction dir, int spriteIndex, Entity e, SpriteBatch batch){
		if(dir==null)return;
		
		Position p = posMapper.get(e);
		Mask m = null;
		
		if(maskMapper.has(e)){
			m = maskMapper.get(e);
		}
		
		Sprite s = entitySprites.get(nameMapper.get(e).getName())[spriteIndex];
		
		Vector2 vec = null;
		
		switch(dir){
		case SE:
			vec = new Vector2((int)p.getIsoPosition().x+(28*s.getScaleX()), (int)p.getIsoPosition().y);
			masks.drawMask(batch, 0, vec, s.getHeight(), p, m);
			batch.draw(s , vec.x, vec.y,s.getWidth()*s.getScaleX(),s.getHeight()*s.getScaleY());
		
			break;
		case SW:
			vec = new Vector2((int)p.getIsoPosition().x, (int)p.getIsoPosition().y);
			masks.drawMask(batch, 1, vec, s.getHeight(), p, m);
			batch.draw(s , vec.x, vec.y,s.getWidth()*s.getScaleX(),s.getHeight()*s.getScaleY());
			
			break;
		}
		
		batch.flush();
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
	private void drawSprite(Entity e, SpriteBatch batch){
		//draws entities
		Position p = posMapper.get(e);
		SpriteComp s = spriteMapper.get(e);
		Sprite tmp = entitySprites.get(nameMapper.get(e).getName())[s.mainSprite];
		
		batch.draw(tmp , (int)p.getIsoPosition().x, (int)p.getIsoPosition().y,tmp.getWidth()*tmp.getScaleX(),tmp.getHeight()*tmp.getScaleY());		//static entities
	}
	public ArrayMap<String, Sprite[]> getSprites() {
		return entitySprites;
	}
	public SkeletonRenderer getSkeletonRenderer() {
		return skeletonRenderer;
	}
	public SkeletonRendererDebug getDebugRenderer() {
		return debugRenderer;
	}
	public void loadSprites(ArrayMap<String, Sprite[]> entitySprites){
		this.entitySprites = entitySprites;
	}
	public MaskingSystem getMasks() {
		return masks;
	}
	public enum Direction{
		NW,NE,SW,SE,NORTH,SOUTH
	}
}
