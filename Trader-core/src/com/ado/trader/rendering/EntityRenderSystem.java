package com.ado.trader.rendering;

import com.ado.trader.entities.WallDirection;
import com.ado.trader.entities.components.Animation;
import com.ado.trader.entities.components.Area;
import com.ado.trader.entities.components.FeatureSprite;
import com.ado.trader.entities.components.Mask;
import com.ado.trader.entities.components.Name;
import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.SpriteComp;
import com.ado.trader.entities.components.Status;
import com.ado.trader.entities.components.WallSprite;
import com.ado.trader.input.InputHandler;
import com.ado.trader.map.Chunk;
import com.ado.trader.map.Map;
import com.ado.trader.map.MapRegion;
import com.ado.trader.systems.AnimationSystem;
import com.ado.trader.utils.GameServices;
import com.ado.trader.utils.IsoUtils;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.brashmonkey.spriter.LibGdxDrawer;
import com.brashmonkey.spriter.Player;

//Handles rendering of all active Entities.
//Renders next entity selected for placement at the mouses location.
public class EntityRenderSystem{
	ComponentMapper<Position> posMapper;
	ComponentMapper<SpriteComp> spriteMapper;
	ComponentMapper<WallSprite> wallMapper;
	ComponentMapper<Area> areaMapper;
	ComponentMapper<Mask> maskMapper;
	ComponentMapper<FeatureSprite> featureMapper;
	ComponentMapper<Animation> animMapper;
	ComponentMapper<Status> statusMapper;
	ComponentMapper<Name> nameMapper;
	
	Map map;
	MaskingSystem masks;
	SpriteManager spriteManager;
	
	Vector2 isoVec;

	public EntityRenderSystem(TextureAtlas atlas, Map map, MaskingSystem masks) {
		this.map = map;
		this.masks = masks;
		
		spriteManager = new SpriteManager(atlas);
		
		nameMapper = map.getWorld().getMapper(Name.class);
		spriteMapper = map.getWorld().getMapper(SpriteComp.class);
		animMapper = map.getWorld().getMapper(Animation.class);
		areaMapper = map.getWorld().getMapper(Area.class);
		wallMapper = map.getWorld().getMapper(WallSprite.class);
		maskMapper = map.getWorld().getMapper(Mask.class);
		featureMapper = map.getWorld().getMapper(FeatureSprite.class);
		statusMapper = map.getWorld().getMapper(Status.class);
		posMapper = map.getWorld().getMapper(Position.class);
		
	}
	
	//renders entities from the rear of the map to front, avoiding sprite overlap caused by isometric view
	public void renderEntities(SpriteBatch batch, OrthographicCamera camera){
		
		if(batch.isDrawing()){
			batch.end();
		}
		batch.begin();
		
		//loops active regions
		int regionSum = map.getRegionMap().length + map.getRegionMap()[0].length;
		
		for(int regionCount = regionSum; regionCount >= 0; regionCount--){		//DEPTH COUNTER
			for(int regionY = map.getRegionMap()[0].length - 1; regionY >= 0; regionY--){
				for(int regionX = map.getRegionMap().length - 1; regionX >= 0; regionX--){		//DIAGONAL REGION READ
					if(regionX + regionY - regionCount == 0){
						if(map.getRegionMap()[regionX][regionY] == null){
							continue;
						}
						MapRegion region = map.getRegionMap()[regionX][regionY];

						//loops chunks in a region
						int chunkSum = region.getWidth() + region.getHeight();

						for(int chunkCount = chunkSum; chunkCount >= 0; chunkCount--){		//DEPTH COUNTER
							for(int chunkY = region.getHeight() - 1; chunkY >= 0; chunkY--){
								for(int chunkX = region.getWidth() - 1; chunkX >= 0; chunkX--){		//DIAGONAL CHUNK READ
									if(chunkX + chunkY - chunkCount == 0){
										if(region.getChunk(chunkX , chunkY) == null){
											continue;
										}
										Chunk chunk = region.getChunk(chunkX , chunkY);
										
										//loops tiles in a chunk
										int tileSum = chunk.getWidth() + chunk.getHeight();

										for(int tileCount = tileSum; tileCount >= 0; tileCount--){		//DEPTH COUNTER
											for(int tileY = chunk.getHeight() - 1; tileY >= 0; tileY--){
												for(int tileX = chunk.getWidth() - 1; tileX >= 0; tileX--){		//DIAGONAL TILE READ
													
													if(tileX + tileY - tileCount == 0){
														//get tile vec
														Vector2 worldTileVec = Map.tileToWorld(tileX, tileY, chunkX, chunkY, regionX, regionY);
														
														isoVec = IsoUtils.getIsoXY((int) worldTileVec.x, (int) worldTileVec.y, map.getTileWidth(), map.getTileHeight());
														
														//drawWide
														if(drawWideEntity(tileX, tileY, chunk, batch))continue;
														
														//draw north wall
														renderNorthernWall(tileX, tileY, chunk, batch);
														
														//draw items
														renderItems(tileX, tileY, chunk, batch);
														
														//draw entities
														renderEntity(tileX, tileY, chunk, batch);
														
														//draw south wall
														renderSouthernWall(tileX, tileY, chunk, batch);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		batch.end();
	}
	
	private boolean drawWideEntity(int tileX, int tileY, Chunk chunk, SpriteBatch batch){
		//check current tile for entity
		if(chunk.getEntities().isOccupied(tileX, tileY)){
			for(int c = 0; c < chunk.getEntities().map[tileX][tileY].length; c++){
				
				if(chunk.getEntities().map[tileX][tileY][c] == null) continue;
				
				Entity e = map.getWorld().getEntity(chunk.getEntities().map[tileX][tileY][c]);
				
				//entity is larger than 1 tile?
				if(areaMapper.has(e)){
					
					Position p = posMapper.get(e);
					
					//the current tile is the entity's origin/anchor tile?
					if(tileX == p.getTileX() && tileY == p.getTileY()){
						Area a = areaMapper.get(e);
						SpriteComp s = spriteMapper.get(e);
						
						//get entity's main sprite
						Sprite tmp = spriteManager.getEntitySprites(s.spriteName)[s.spriteIndex];
						
						//render next entity to prevent drawing overlap
						if(tmp.isFlipX()){
							if(chunk.getEntities().isOccupied(tileX +1, tileY -1)){
								Entity next = map.getWorld().getEntity(chunk.getEntities().map[tileX +1][tileY -1][0]);
								drawSprite(next, batch);
								tileX--;
								tileY--;
							}
						}
						
						//render any NE, NW walls for current tile
						renderNorthernWall(tileX, tileY, chunk, batch);
						
						//render NE, NW walls for other entity occupied tiles
						for(Vector2 vec:a.area){			
							int aX = (int)(vec.x + tileX);
							int aY = (int)(vec.y + tileY);
							renderNorthernWall(aX, aY, chunk, batch);
						}
						
						Vector2 iso = IsoUtils.getIsoXY(tileX, tileY, map.getTileWidth(), map.getTileHeight());
						
						//draw wide sprite offset to the bottom left occupied tile(eg [0,0],[1,1])
						if(tmp.isFlipX()){		
							batch.draw(tmp , iso.x + p.getIsoOffset().x - 4, iso.y + p.getIsoOffset().y - 32,
									tmp.getWidth()*tmp.getScaleX(),tmp.getHeight()*tmp.getScaleY());
						}else{
						//draw wide sprite offset to the top left occupied tile(eg [0,1],[1,0])

							batch.draw(tmp , iso.x + p.getIsoOffset().x - 68, iso.y + p.getIsoOffset().y - 32,
									tmp.getWidth()*tmp.getScaleX(),tmp.getHeight()*tmp.getScaleY());
						}
						
						//render any SE, SW walls for current tile
						renderSouthernWall(tileX, tileY, chunk, batch);
						
						//render SE, SW walls for other entity occupied tiles
						for(Vector2 vec:a.area){
							int aX = (int)(vec.x + tileX);
							int aY = (int)(vec.y + tileY);

							renderSouthernWall(aX, aY, chunk, batch);
						}
					}
					return true;
				}
			}
		}
		return false;
	}
	
	private void renderEntity(int tileX, int tileY, Chunk chunk, SpriteBatch batch){
		if(chunk.getEntities().isOccupied(tileX, tileY)){
			
			for(int c = 0; c < chunk.getEntities().map[tileX][tileY].length; c++){
				
				if(chunk.getEntities().map[tileX][tileY][c] == null) continue;
				
				Entity e = map.getWorld().getEntity(chunk.getEntities().map[tileX][tileY][c]);

				if(spriteMapper.has(e)){		//RENDER STATIC ENTITY
					drawSprite(e,batch);
					
				}else if(animMapper.has(e)){		//RENDER ANIMATED(NPC) ENTITY
					LibGdxDrawer drawer = GameServices.getWorld().getSystem(AnimationSystem.class).getDrawer();
					Player player = animMapper.get(e).getPlayer();
					Position pos = posMapper.get(e);
					
					player.setPosition(isoVec.x + pos.getIsoOffset().x + (Map.tileWidth / 2), isoVec.y + pos.getIsoOffset().y + (Map.tileHeight / 2));
					
					drawer.draw(player);
					
					if(InputHandler.DEBUG){
						drawer.getShapeRenderer().setAutoShapeType(true);
						drawer.getShapeRenderer().begin();
						
						drawer.drawBoxes(player);
						
						drawer.getShapeRenderer().end();
					}
					
					
//					//render status icons
//					if(statusMapper.has(e)){
//						StatusIcon icon = statusMapper.get(e).getStatusIcon();
//						Vector2 iconPos = new Vector2(skel.getX(), skel.getY());
//						bounds.update(skel, true);
//						iconPos.y += bounds.getHeight() + 4;
//						icon.drawIcon(batch, iconPos);
//					}
				}
			}
		}
	}
	
	private void renderItems(int tileX, int tileY, Chunk chunk, SpriteBatch batch){
		if(chunk.getItems().isOccupied(tileX, tileY)){
			Entity i = map.getWorld().getEntity(chunk.getItems().map[tileX][tileY][0]);
			
			String name = spriteMapper.get(i).spriteName;
			Sprite s = spriteManager.getItemSprite(name);
			
			batch.draw(s, (isoVec.x + map.getTileWidth() / 2) - s.getWidth(), isoVec.y + map.getTileHeight() / 3, 
					s.getWidth() * s.getScaleX(), s.getHeight() * s.getScaleY());
		}
	}
	
	private void renderNorthernWall(int tileX, int tileY, Chunk chunk, SpriteBatch batch){
		if(!chunk.getWalls().isOccupied(tileX, tileY)) return;
		
		//wall entity
		Entity e = map.getWorld().getEntity(chunk.getWalls().map[tileX][tileY]);
		
		if(e == null) return;
		
		WallSprite w = wallMapper.get(e);
		
		//check if walls are north facing
		if(!(w.firstSprite == WallDirection.NE || w.firstSprite == WallDirection.NW) && 
				!(w.secondSprite == WallDirection.NE || w.secondSprite == WallDirection.NW))return;
				
		drawNorthSprite(w.firstSprite, e, batch);
		if(w.secondSprite != null){
			drawNorthSprite(w.secondSprite, e, batch);
		}
		
		drawFeature(e, batch);
	}
	
	private void renderSouthernWall(int tileX, int tileY, Chunk chunk, SpriteBatch batch){
		if(!chunk.getWalls().isOccupied(tileX, tileY)) return;
		
		//wall entity
		Entity e = map.getWorld().getEntity(chunk.getWalls().map[tileX][tileY]);
		
		if(e == null) return;
		
		WallSprite w = wallMapper.get(e);
		
		//check if walls are south facing
		if(!(w.firstSprite == WallDirection.SE || w.firstSprite == WallDirection.SW) && 
				!(w.secondSprite == WallDirection.SE || w.secondSprite == WallDirection.SW))return;
		
		drawSouthSprite(w.firstSprite, e, batch);
		if(w.secondSprite != null){
			drawSouthSprite(w.secondSprite, e, batch);
		}
		
		drawFeature(e, batch);
	}
	
	//draw decorating sprite(lamps/paintings on wall, windows)
	private void drawFeature(Entity e, SpriteBatch batch){
		if(!featureMapper.has(e))return;
		
		FeatureSprite f = featureMapper.get(e);
		Sprite s = spriteManager.getFeatureSprites(f.featureName)[f.spriteIndex];
				
		batch.draw(s, isoVec.x, isoVec.y, s.getWidth() * s.getScaleX(), s.getHeight() * s.getScaleY());
	}

	private void drawNorthSprite(WallDirection dir, Entity e, SpriteBatch batch){
		if(dir == null)return;
		
		Position p = posMapper.get(e);
		Mask m = null;
		
		if(maskMapper.has(e)){
			m = maskMapper.get(e);
		}
		
		Sprite s = spriteManager.getWallSprites(wallMapper.get(e).spriteName)[dir.index()];
		
		Vector2 vec = null;
		
		switch(dir){
		case NE:
			vec = new Vector2((int)isoVec.x + 2, (int)isoVec.y);
			masks.drawMask(batch, dir.index(), vec, s.getHeight(), p, m);
			batch.draw(s , vec.x, vec.y, s.getWidth() * s.getScaleX(), s.getHeight() * s.getScaleY());
			
			break;
		case NW:
			vec = new Vector2((int)isoVec.x - 2, (int)isoVec.y);
			masks.drawMask(batch, dir.index(), vec, s.getHeight(), p, m);
			batch.draw(s , vec.x, vec.y, s.getWidth() * s.getScaleX(), s.getHeight() * s.getScaleY());
			
			break;
		}
		batch.flush();
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	private void drawSouthSprite(WallDirection dir, Entity e, SpriteBatch batch){
		if(dir==null)return;
		
		Position p = posMapper.get(e);
		Mask m = null;
		
		if(maskMapper.has(e)){
			m = maskMapper.get(e);
		}
		
		Sprite s = spriteManager.getWallSprites(wallMapper.get(e).spriteName)[dir.index()];
		
		Vector2 vec = null;
		
		switch(dir){
		case SE:
			vec = new Vector2((int)isoVec.x, (int)isoVec.y);
			masks.drawMask(batch, dir.index(), vec, s.getHeight(), p, m);
			batch.draw(s , vec.x, vec.y, s.getWidth() * s.getScaleX(), s.getHeight() * s.getScaleY());
		
			break;
		case SW:
			vec = new Vector2((int)isoVec.x, (int)isoVec.y);
			masks.drawMask(batch, dir.index(), vec, s.getHeight(), p, m);
			batch.draw(s , vec.x, vec.y, s.getWidth() * s.getScaleX(), s.getHeight() * s.getScaleY());
			
			break;
		}
		
		batch.flush();
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	private void drawSprite(Entity e, SpriteBatch batch){
		//draws entities
		Position p = posMapper.get(e);
		SpriteComp s = spriteMapper.get(e);
		Sprite tmp = spriteManager.getEntitySprites(s.spriteName)[s.spriteIndex];
		
		batch.draw(tmp , isoVec.x + p.getIsoOffset().x, isoVec.y + p.getIsoOffset().y,
				tmp.getWidth() * tmp.getScaleX(), tmp.getHeight() * tmp.getScaleY());		//static entities
	}
	public MaskingSystem getMasks() {
		return masks;
	}
	public SpriteManager getSpriteManager() {
		return spriteManager;
	}
}
