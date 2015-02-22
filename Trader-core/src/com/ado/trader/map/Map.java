package com.ado.trader.map;

import com.ado.trader.entities.components.Wall;
import com.ado.trader.items.ItemFactory;
import com.ado.trader.pathfinding.Mover;
import com.ado.trader.pathfinding.TileBasedMap;
import com.ado.trader.rendering.EntityRenderSystem.Direction;
import com.ado.trader.systems.GameTime;
import com.ado.trader.systems.SaveSystem;
import com.ado.trader.utils.FileParser;
import com.ado.trader.utils.GameServices;
import com.ado.trader.utils.IsoUtils;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

//Tile map class. Contain tile map array, world width/height, tile width/height
public class Map implements TileBasedMap{
	public static int tileWidth = 64*2;
	public static int tileHeight = 32*2;
	int worldWidth = 25;
	int worldHeight = 25;
	public int currentLayer;
	
	Array<Sprite> tileSprites;

	TileCollection tilePool;
	TileOverlay overlay;
	MapLoader loader;
	
	EntityLayer entityLayer;
	ItemLayer itemLayer;
	TileLayer tileLayer;
	WallLayer wallLayer;
	World world;
	
	public Map(GameServices gameRes) {
		init(gameRes.getAtlas(), gameRes.getParser(), gameRes.getWorld(), gameRes.getItems());
		createMap();
	}
	
	public Map(String loadName, GameServices gameRes) {
		init(gameRes.getAtlas(), gameRes.getParser(), gameRes.getWorld(), gameRes.getItems());
		loader.loadMap(loadName);
		
		currentLayer = 0;
	}
	private void init(TextureAtlas atlas, FileParser parser, World world, ItemFactory items){
		this.world = world;
		world.setSystem(new GameTime(1.0f));
		world.initialize();
		
		loader = new MapLoader(this, items, parser);
		
		currentLayer = 0;
		tileLayer = new TileLayer(worldWidth, worldHeight);
		entityLayer = new EntityLayer(worldWidth, worldHeight, world);
		itemLayer = new ItemLayer(worldWidth, worldHeight);
		wallLayer = new WallLayer(worldWidth, worldHeight);
		
		overlay = new TileOverlay(atlas);
		tileSprites = loadTileSprites(atlas);
		tilePool = new TileCollection(parser);
	}

	//creates a map with tile in an isometric layout
	private void createMap(){
		for(int x=0; x<worldWidth; x++){
			for(int y=0; y<worldHeight; y++){
				if(x==0||y==0){
					tileLayer.map[x][y][0] = tilePool.createTile(0, x, y,0);
				}else{
					tileLayer.map[x][y][0] = tilePool.createTile(1, x, y,0);
				}
			}
		}
	}
	
	public void saveGameState(String dirName){
		loader.saveMap(dirName);
		world.getSystem(SaveSystem.class).saveEntities(dirName);
	}
	
	//draws tile backgrounds
	public void draw(SpriteBatch batch){
		if(batch.isDrawing()){
			batch.end();
		}
		batch.begin();
		//loops and draws isometric tiles
		for(int x=worldWidth-1; x>-1; x--){
			for(int y=worldHeight-1; y>-1; y--){
				Vector2 tmp = IsoUtils.getIsoXY(x, y, tileWidth, tileHeight);
				if(tileLayer.map[x][y][currentLayer].mask!=null){
					batch.draw(tileSprites.get(tileLayer.map[x][y][currentLayer].id),
							tmp.x, tmp.y,tileWidth,tileHeight+(tileHeight/2));
					overlay.drawMask(batch, tmp.x, tmp.y,
							tileWidth,tileHeight+(tileHeight/2), tileLayer.map[x][y][currentLayer].mask);
					overlay.drawOverlay(batch, tmp.x, tmp.y, 
							tileWidth,tileHeight+(tileHeight/2), tileSprites.get(tileLayer.map[x][y][currentLayer].overlayId));
					batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
					continue;
				}
				batch.draw(tileSprites.get(tileLayer.map[x][y][currentLayer].id),
						tmp.x, tmp.y,tileWidth,tileHeight+(tileHeight/2));
			}
		}
		batch.end();
	}
	
	public void drawDebug(ShapeRenderer sr){
		sr.begin(ShapeType.Filled);
		sr.setColor(Color.CYAN);
		//draws debug grid(tiles)
		for(int x=0; x<worldWidth; x++){
			for(int y=0; y<worldHeight; y++){
				Vector2 tmp = IsoUtils.getIsoXY(x, y, tileWidth, tileHeight);
				sr.setColor(Color.YELLOW);
				sr.rect(tmp.x, tmp.y, 4, 4);
				if(entityLayer.isOccupied(x, y, currentLayer)){
					sr.setColor(Color.RED);
					sr.rect(tmp.x, tmp.y, tileWidth, tileHeight);
				}
			}
		}
		sr.end();
	}
	
	@Override
	public boolean blocked(Mover mover,int srcX, int srcY, int srcH, int x, int y, int h) {
		//height check
		if(srcH != h){
			return true;
		}
		
		//completely blocked
		if(tileLayer.map[x][y][h].travelCost > 0){
			return true;
		}
		
		if(wallLayer.isOccupied(x, y, h)){
			Entity wall = world.getEntity(wallLayer.map[x][y][h]);
			Wall wC = wall.getComponent(Wall.class);
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
		return entityLayer.isOccupied(x, y, h);
	}
	

	private boolean checkWallDirection(Wall w, Direction dir){
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
	public float getCost(Mover mover, int sx, int sy, int sh, int tx, int ty, int th) {
		return tileLayer.map[sx][sx][sh].travelCost + tileLayer.map[tx][tx][th].travelCost; 
	}
	public EntityLayer getEntityLayer() {
		return entityLayer;
	}
	public ItemLayer getItemLayer() {
		return itemLayer;
	}
	public TileLayer getTileLayer() {
		return tileLayer;
	}
	public WallLayer getWallLayer() {
		return wallLayer;
	}
	public int getWidthInTiles() {
		return worldWidth;
	}
	public int getHeightInTiles() {
		return worldHeight;
	}
	public int getTileWidth() {
		return tileWidth;
	}
	public int getTileHeight() {
		return tileHeight;
	}
	public TileCollection getTilePool() {
		return tilePool;
	}
	public Array<Sprite> loadTileSprites(TextureAtlas atlas){
		Array<Sprite> sprites = atlas.createSprites("terrain/tile");
		return sprites;
	}
	public Array<Sprite> getTileSprites() {
		return tileSprites;
	}
	public MapLoader getMapLoader(){
		return loader;
	}
	public World getWorld(){
		return world;
	}
}
