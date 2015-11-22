package com.ado.trader.map;

import com.ado.trader.entities.components.WallSprite;
import com.ado.trader.input.MapEditorInput;
import com.ado.trader.pathfinding.Mover;
import com.ado.trader.pathfinding.TileBasedMap;
import com.ado.trader.placement.TilePlaceable;
import com.ado.trader.systems.GameTime;
import com.ado.trader.systems.SaveSystem;
import com.ado.trader.utils.GameServices;
import com.ado.trader.utils.IsoUtils;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

//Tile map class. Contain tile map array, world width/height, tile width/height
public class Map implements TileBasedMap{
	public static int tileWidth = 64*2;
	public static int tileHeight = 32*2;
	
	Sprite tileOutline;
	Array<Sprite> tileSprites;
	MapRegion[][] activeRegions;

	TileCollection tilePool;
	TileMaskCollection overlay;
	MapStreamer streamer;
	
	World world;
	
	public Map(GameServices gameRes) {
		init(gameRes.getAtlas(), GameServices.getWorld());
	}
	
	public Map(String loadName, GameServices gameRes) {
		init(gameRes.getAtlas(), GameServices.getWorld());
	}
	private void init(TextureAtlas atlas, World world){
		tileOutline = atlas.createSprite("gui/highlightTile");
		this.world = world;
		activeRegions = new MapRegion[3][3];
		
		overlay = new TileMaskCollection(atlas, tileWidth, tileHeight+(tileHeight/2));
		tileSprites = loadTileSprites(atlas);
		tilePool = new TileCollection(); 
	}

	//creates a map with tile in an isometric layout
	public void createMap(TilePlaceable tilePlacer){
		int startId = tilePlacer.getNextRegionId();
		tilePlacer.incrementID();
		
		MapRegion region = new MapRegion(startId);

		Chunk chunk = new Chunk(world);
		
		//set chunk to middle of region
		region.setChunk(1, 1, chunk);
		//set region to middle of map
		activeRegions[1][1] = region;
		
		TileLayer layer = chunk.getTiles();
		
		for(int x = 0; x < layer.getWidth(); x++){
			for(int y = 0; y < layer.getHeight(); y++){
				if(x == 0||y == 0){
					layer.map[x][y] = tilePool.createTile(0);
				}else{
					layer.map[x][y] = tilePool.createTile(1);
				}
			}
		}
	}
	
	public void saveGameState(String dirName){
		streamer.saveMap(dirName);
		world.getSystem(SaveSystem.class).saveEntities(dirName);
	}
	
	//draws tile backgrounds
	public void draw(SpriteBatch batch){
		if(batch.isDrawing()){
			batch.end();
		}
		batch.begin();
		
		//loops active regions
		int sum = activeRegions.length + activeRegions[0].length;
		
		for(int count = sum; count >= 0; count--){		//DEPTH COUNTER
			for(int y = activeRegions[0].length - 1; y >= 0; y--){
				for(int x = activeRegions.length - 1; x >= 0; x--){		//DIAGONAL MAP READ
					if(x + y - count == 0){
						if(activeRegions[x][y] == null){
							continue;
						}
						MapRegion region = activeRegions[x][y];
						
						//loops chunks in a region
						int regionSum = region.chunks.length + region.chunks[0].length;
						
						for(int regionCount = regionSum; regionCount >= 0; regionCount--){		//DEPTH COUNTER
							for(int regionY = region.chunks[0].length - 1; regionY >= 0; regionY--){
								for(int regionX = region.chunks.length - 1; regionX >= 0; regionX--){		//DIAGONAL MAP READ
									if(regionX + regionY - regionCount == 0){
										if(region.chunks[regionX][regionY] == null){
											continue;
										}
										Chunk chunk = region.chunks[regionX][regionY];
										TileLayer layer = chunk.tiles;
										
										//loops tiles in a chunk
										int chunkSum = chunk.getWidth() + chunk.getHeight();
										
										for(int chunkCount = chunkSum; chunkCount >= 0; chunkCount--){		//DEPTH COUNTER
											for(int chunkY = chunk.getHeight() - 1; chunkY >= 0; chunkY--){
												for(int chunkX = chunk.getWidth() - 1; chunkX >= 0; chunkX--){		//DIAGONAL MAP READ
													if(chunkX + chunkY - chunkCount == 0){
														if(layer.map[chunkX][chunkY] == null){
															continue;
														}
														Tile tile = layer.map[chunkX][chunkY];
														
														//get tile screen vec from world position
														int tileX = x * region.getWidthInTiles() + regionX * chunk.getWidth() + chunkX;
														int tileY = y * region.getHeightInTiles() + regionY * chunk.getHeight() + chunkY;
														
														Vector2 isoVec = IsoUtils.getIsoXY(tileX, tileY, tileWidth, tileHeight);
														
														//draw tile
														batch.draw(tileSprites.get(tile.id),
																isoVec.x, isoVec.y,tileWidth,tileHeight+(tileHeight/2));
														
														//draw tile transition
														if(tile.mask != null){
															overlay.drawMask(batch, isoVec.x, isoVec.y, tile.mask.overlay, tile.mask.dir);
															overlay.drawOverlay(batch, isoVec.x, isoVec.y, tileSprites.get(tile.mask.tileID));
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
		}
		batch.end();
	}
	
	
	public void drawDebug(SpriteBatch batch){
		batch.begin();
		
		//draws debug grid(tiles)
		//loops active regions
		int sum = activeRegions.length + activeRegions[0].length;

		for(int count = sum; count >= 0; count--){		//DEPTH COUNTER
			for(int y = 2; y >= 0; y--){
				for(int x = 2; x >= 0; x--){		//DIAGONAL MAP READ
					if(x + y - count == 0){
						
						//loops chunks in a region
						int regionSum = 3 + 3;

						for(int regionCount = regionSum; regionCount >= 0; regionCount--){		//DEPTH COUNTER
							for(int regionY = 2; regionY >= 0; regionY--){
								for(int regionX = 2; regionX >= 0; regionX--){		//DIAGONAL MAP READ
									if(regionX + regionY - regionCount == 0){
										
										//loops tiles in a chunk
										int chunkSum = 32 + 32;

										for(int chunkCount = chunkSum; chunkCount >= 0; chunkCount--){		//DEPTH COUNTER
											for(int chunkY = 31; chunkY >= 0; chunkY--){
												for(int chunkX = 31; chunkX >= 0; chunkX--){		//DIAGONAL MAP READ
													if(chunkX + chunkY - chunkCount == 0){

														//get tile vec
														Vector2 tileVec= Map.tileToWorld(chunkX, chunkY, regionX, regionY, x, y);

														Vector2 isoVec = IsoUtils.getIsoXY((int) tileVec.x, (int) tileVec.y, tileWidth, tileHeight);
														
														if(chunkX == 0 || chunkY == 0 || chunkX == 31 || chunkY == 31){
															batch.setColor(Color.MAGENTA);
														}
														
														//colour region boundries
														switch(regionX){
														case 0:
															if(chunkX == 0){
																batch.setColor(Color.BLUE);
															}
															break;
														case 2:
															if(chunkX == 31){
																batch.setColor(Color.BLUE);
															}
															break;
														}
														switch(regionY){
														case 0:
															if(chunkY == 0){
																batch.setColor(Color.BLUE);
															}
															break;
														case 2:
															if(chunkY == 31){
																batch.setColor(Color.BLUE);
															}
															break;
														}
														
														batch.draw(tileOutline, isoVec.x, isoVec.y, tileWidth, tileHeight);
														
														if(batch.getColor() != Color.WHITE){
															batch.setColor(Color.WHITE);
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
		}
		batch.end();
	}
	
	public MapRegion getRegion(int mapX, int mapY){
		Vector2 regionXY = worldVecToRegion(mapX, mapY);
		
		return activeRegions[(int) regionXY.x][(int) regionXY.y];
	}
	
	public Chunk getChunk(int mapX, int mapY){
		MapRegion region = getRegion(mapX, mapY);
		if(region == null){
//			Gdx.app.log("Map(getChunk): ", "Region==null");
			return null;
		}
		
		Vector2 chunk = worldVecToChunk(mapX, mapY);
		
		return region.chunks[(int) chunk.x][(int) chunk.y];
	}
	
	public static Vector2 worldVecToRegion(int mapX, int mapY){
		float regionX = (float) Math.floor(mapX / (3 * 32));
		
		float regionY = (float) Math.floor(mapY / (3 * 32));
		
		return new Vector2(regionX, regionY);
	}
	public static Vector2 worldVecToChunk(int mapX, int mapY){
		Vector2 region = worldVecToRegion(mapX, mapY);
		
		int chunkX = (int) Math.floor((mapX - (region.x * (3 * 32)) ) / 32);
		
		int chunkY = (int) Math.floor((mapY - (region.y * (3 * 32)) ) / 32);
		
		return region.set(chunkX, chunkY);
	}
	public static Vector2 worldVecToTile(int mapX, int mapY){
		Vector2 region = worldVecToRegion(mapX, mapY);
		Vector2 chunk = worldVecToChunk(mapX, mapY);
		
		int tileX = (int) Math.floor((mapX - (region.x * (3 * 32))) - chunk.x * 32);
		
		int tileY = (int) Math.floor((mapY - (region.y * (3 * 32))) - chunk.y * 32);
		
		return chunk.set(tileX, tileY);
	}
	public static Vector2 tileToWorld(int tX, int tY, int cX, int cY, int rX, int rY){
		int x = (int) Math.floor(tX + ((rX * (3 * 32)) + cX * 32));
		int y = (int) Math.floor(tY + ((rY * (3 * 32)) + cY * 32));
		
		return new Vector2(x, y);
	}
	
	@Override
	public boolean blocked(Mover mover,int srcX, int srcY, int x, int y) {
		Vector2 tgtVec = worldVecToRegion(x, y);
		
		MapRegion tgtRegion = activeRegions[(int) tgtVec.x][(int) tgtVec.y];
		
		tgtVec = worldVecToChunk(x, y);
		
		Chunk tgtChunk =  tgtRegion.chunks[(int) tgtVec.x][(int) tgtVec.y];
		
		TileLayer tgtLayer = tgtChunk.tiles;
		
		tgtVec = worldVecToTile(x, y);
		
		//completely blocked(water, large stones?)
		if(tgtLayer.map[(int) tgtVec.x][(int) tgtVec.y].travelCost > 0){
			return true;
		}
		
		if(tgtChunk.getWalls().isOccupied(x, y)){
			Entity wall = world.getEntity(tgtChunk.getWalls().map[x][y]);
			WallSprite wC = wall.getComponent(WallSprite.class);
			int lenX = (int) Math.signum(x - srcX);
			int lenY = (int) Math.signum(y - srcY);
			if(lenX!=0){
				switch(lenX){
				case -1:
					if(checkWallDirection(wC, Direction.SW)){
						return true;
					}
					break;
				case 1:
					if(checkWallDirection(wC, Direction.NE)){
						return true;
					}
					break;
				}
			}
			if(lenY!=0){
				switch(lenY){
				case -1:
					if(checkWallDirection(wC, Direction.SE)){
						return true;
					}
					break;
				case 1:
					if(checkWallDirection(wC, Direction.NW)){
						return true;
					}
					break;
				}
			}
		}
		return tgtChunk.getEntities().isOccupied(x, y);
	}
	

	private boolean checkWallDirection(WallSprite w, Direction dir){
		if(w.firstSprite.equals(dir)){
			return true;
		}else if(w.secondSprite != null){
			if(w.secondSprite.equals(dir)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public float getCost(Mover mover, int sx, int sy, int tx, int ty) {
		Vector2 srcVec = worldVecToRegion(sx, sy);
		Vector2 tgtVec = worldVecToRegion(tx, ty);
		
		MapRegion srcRegion = activeRegions[(int) srcVec.x][(int) srcVec.y];
		MapRegion tgtRegion = activeRegions[(int) tgtVec.x][(int) tgtVec.y];
		
		srcVec = worldVecToChunk(sx, sy);
		tgtVec = worldVecToChunk(tx, ty);
		
		TileLayer srcLayer = srcRegion.chunks[(int) srcVec.x][(int) srcVec.y].tiles;
		TileLayer tgtLayer = tgtRegion.chunks[(int) tgtVec.x][(int) tgtVec.y].tiles;
		
		srcVec = worldVecToTile(sx, sy);
		tgtVec = worldVecToTile(tx, ty);
		
		return srcLayer.map[(int) srcVec.x][(int) srcVec.y].travelCost + tgtLayer.map[(int) tgtVec.x][(int) tgtVec.y].travelCost; 
	}
	public int getWidthInTiles() {
		//chunk tile width * (region chunk width * active region map width)
		int w = 32 * (3 * activeRegions.length);
		return w;
	}
	public int getHeightInTiles() {
		int h = 32 * (3 * activeRegions[1].length);
		return h;
	}
	public int getTileWidth() {
		return tileWidth;
	}
	public int getTileHeight() {
		return tileHeight;
	}
	public MapRegion[][] getRegionMap(){
		return activeRegions;
	}
	public TileCollection getTilePool() {
		return tilePool;
	}
	public TileMaskCollection getTileMasks(){
		return overlay;
	}
	public Array<Sprite> loadTileSprites(TextureAtlas atlas){
		Array<Sprite> sprites = atlas.createSprites("terrain/tile");
		return sprites;
	}
	public Array<Sprite> getTileSprites() {
		return tileSprites;
	}
	public World getWorld(){
		return world;
	}
}
