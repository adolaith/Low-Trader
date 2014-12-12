package com.ado.trader.map;

import com.ado.trader.GameMain;
import com.ado.trader.entities.components.Wall;
import com.ado.trader.items.Item;
import com.ado.trader.items.ItemPosition;
import com.ado.trader.map.Zone.ZoneType;
import com.ado.trader.screens.GameScreen;
import com.ado.trader.systems.GameTime;
import com.ado.trader.systems.EntityRenderSystem.Direction;
import com.ado.trader.utils.FileParser;
import com.ado.trader.utils.IsoUtils;
import com.ado.trader.utils.pathfinding.Mover;
import com.ado.trader.utils.pathfinding.TileBasedMap;
import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

//Tile map class. Contain tile map array, world width/height, tile width/height
public class Map implements TileBasedMap{
	GameScreen game;
	TileCollection tilePool;
	TileOverlay overlay;
	final int tileWidth =64*2;
	final int tileHeight =32*2;
	int worldWidth = 25;
	int worldHeight = 25;
	Array<Sprite> tileSprites;
	public int currentLayer;
	Array<LayerGroup> layerGroups;
	
	public Map(GameScreen game) {
		this.game = game;
		currentLayer = 0;
		layerGroups = new Array<LayerGroup>();
		layerGroups.add(new LayerGroup(game, worldWidth, worldHeight));
		overlay = new TileOverlay(game);
		tileSprites = loadTileSprites();
		tilePool = new TileCollection(game);
		createMap();
	}
	
	public Map(GameScreen game, String loadName) {
		this.game = game;
		currentLayer = 0;
		layerGroups = new Array<LayerGroup>();
		layerGroups.add(new LayerGroup(game, worldWidth, worldHeight));
		overlay = new TileOverlay(game);
		tileSprites = loadTileSprites();
		tilePool = new TileCollection(game);
		loadMap(loadName);
	}

	//creates a map with tile in an isometric layout
	private void createMap(){
		for(int x=0; x<worldWidth; x++){
			for(int y=0; y<worldHeight; y++){
				if(x==0||y==0){
					layerGroups.get(0).tileLayer.map[x][y] = tilePool.createTile(0, x, y);
				}else{
					layerGroups.get(0).tileLayer.map[x][y] = tilePool.createTile(1, x, y);
				}
			}
		}
	}
	
	private int zoneId;
	public int getNextZoneId(){
		return zoneId;
	}
	
	private void loadMap(String dirName){
		zoneId = 0;
		FileParser p = game.getParser();  
		p.initParser("saves/"+dirName+"/map.sav", false, true);
		if(p.getFile().readString().isEmpty()){Gdx.app.log(GameMain.LOG, "Save file is empty"); return;}
		Array<ArrayMap<String, String>> data = p.readFile();
		
		ArrayMap<String, String> time = data.removeIndex(0);
		game.getWorld().getSystem(GameTime.class).loadSettings(Integer.valueOf(time.get("days")),
				GameTime.Time.valueOf(time.get("ToD")), Integer.valueOf(time.get("time")));
		currentLayer = Integer.valueOf(data.removeIndex(0).get("layer"));
		
		p.initParser("saves/"+dirName+"/zone.sav", false, true);
		Array<ArrayMap<String, String>> zoneData = p.readFile(); 
		zoneData.removeIndex(0);
		p.initParser("saves/"+dirName+"/items.sav", false, true);
		Array<ArrayMap<String, String>> itemsData = p.readFile(); 
		itemsData.removeIndex(0);
		
		//loop saved map layers and load tiles, zones and items
		boolean loading = true;
		while(loading){
			loading = loadMapLayer(data);
			loadZoneLayer(zoneData);
			loadItems(itemsData);
		}
		
		currentLayer = 0;
	}
	
	public Zone zoneIdSearch(int id){
		for(LayerGroup lGroup: layerGroups){
			for(Array<Zone> typeGroup: lGroup.zoneLayer.zoneList.values()){
				for(Zone z: typeGroup){
					if(z.getId() == id){
						return z;
					}
				}
			}
		}
		return null;
	}
	
	private void loadItems(Array<ArrayMap<String, String>> data){
		int count = 0;
		for(ArrayMap<String, String> i: data){
			if(i.containsKey("layer")){
				data.removeRange(0, count);
				return;
			}
			String[] list = i.get("pos").split(",");
			Item item = game.getItems().createItem(i.get("id"));
			ItemPosition pos = item.getData(ItemPosition.class);
			pos.position.set(Integer.valueOf(list[0]), Integer.valueOf(list[1]));
			getCurrentLayerGroup().itemLayer.addToMap(item, Integer.valueOf(list[0]), Integer.valueOf(list[1]));
			count++;
		}
	}
	
	private void loadZoneLayer(Array<ArrayMap<String, String>> data){
		int count = 0;
		for(ArrayMap<String, String> z: data){
			if(z.containsKey("layer")){
				data.removeRange(0, count);
				return;
			}
			//load up vectors
			String[] list = z.get("pos").split(",");
			Array<Vector2> area = new Array<Vector2>();
			for(String pos: list){
				String[] vec = pos.split("'");
				Vector2 tmp = new Vector2(Float.valueOf(vec[0]), Float.valueOf(vec[1]));
				area.add(tmp);
			}
			//check if id > zoneId
			int id = Integer.valueOf(z.get("id"));
			if(id > zoneId){
				zoneId = id;
			}
			
			//create zone
			Zone n = game.getPlaceManager().getZonePl().createNewZone(id, area, ZoneType.valueOf(z.get("type")), getCurrentLayerGroup());
			
			//configure work areas
			if(n instanceof WorkZone){
				WorkZone wZone = (WorkZone) n;
				//work tiles
				if(z.containsKey("workTiles")){
					wZone.workTiles = new ArrayMap<Vector2, Integer>();
					String[] tiles = z.get("workTiles").split(",");
					for(String t: tiles){
						String[] xy = t.split("'");
						Vector2 vec = new Vector2(Float.valueOf(xy[0]), Float.valueOf(xy[1]));
						wZone.workTiles.put(vec, null);
					}
				}
				if(z.containsKey("workArea")){
					wZone.workArea = new ArrayMap<Array<Vector2>, Array<Integer>>();
					wZone.workArea.put(new Array<Vector2>(), new Array<Integer>());
					String[] wArea = z.get("workArea").split(",");
					for(String t: wArea){
						
						Array<Vector2> vecArr = new Array<Vector2>();
						String[] vecs = t.split("-");
						for(String v: vecs){
							String[] xy = v.split("'");
							Vector2 vec = new Vector2(Float.valueOf(xy[0]), Float.valueOf(xy[1]));
							vecArr.add(vec);
						}
						
						wZone.workArea.put(vecArr, new Array<Integer>());
					}
				}
			}
			
			//configure zone
			switch(n.type){
			case FARM:
				FarmZone f = (FarmZone) n;
				if(z.containsKey("item")){
					f.itemName = z.get("item");
				}
				f.daysGrowing = Integer.valueOf(z.get("days"));
				f.growScore = Float.valueOf(z.get("grow"));
				f.maintenance = Float.valueOf(z.get("maintenance"));
				break;
			case HOME:
				HomeZone h = (HomeZone) n;
				h.maxOccupants = Integer.valueOf(z.get("maxOccupants"));
				if(z.containsKey("garden")){
					h.garden = (FarmZone) zoneIdSearch(Integer.valueOf(z.get("garden")));
				}
				break;
			}
			
			count++;
		}
	}
	
	private boolean loadMapLayer(Array<ArrayMap<String, String>> data){
		Tile[][] savedMap = new Tile[worldWidth][worldHeight];
		
		int count = 0;
		for(int x=0; x<savedMap.length; x++){
			for(int y=0; y<savedMap[x].length; y++){
				if(data.get(count).containsKey("layer")){
					getCurrentLayerGroup().tileLayer.map = savedMap;
					currentLayer = Integer.valueOf(data.get(count).get("layer"));
					data.removeRange(0, count);
					return true;
				}
				Tile t = tilePool.createTile(Integer.valueOf(data.get(count).get("id")), x, y);
				savedMap[x][y] = t;
				count++;
			}
		}
		getCurrentLayerGroup().tileLayer.map = savedMap;
		return false;
	}
	
	public void saveMap(String dir){
		StringBuilder tileString= new StringBuilder();
		StringBuilder zoneString= new StringBuilder();
		StringBuilder itemString= new StringBuilder();
		
		FileParser p = game.getParser();	//save time  
		p.string = tileString;
		p.addElement("ToD", String.valueOf(game.getWorld().getSystem(GameTime.class).getTimeOfDay()));
		p.addElement("time", String.valueOf(game.getWorld().getSystem(GameTime.class).getTime()));
		p.addElement("days", String.valueOf(game.getWorld().getSystem(GameTime.class).getDays()));
		p.newNode();
		for(LayerGroup layer: layerGroups){
			saveLayer(tileString, layerGroups.indexOf(layer, false));
			saveLayer(zoneString, layerGroups.indexOf(layer, false));
			saveLayer(itemString, layerGroups.indexOf(layer, false));
			layer.saveLayers(game, tileString, zoneString, itemString);
		}
		writeLayers(dir, "map", tileString);
		writeLayers(dir, "zone", zoneString);
		writeLayers(dir, "items", itemString);
	}
	private void writeLayers(String dir,String name, StringBuilder str){
		FileParser p = game.getParser();
		p.initParser("saves/"+dir+"/"+name+".sav", true, true);
		p.string = str;
		p.writeToFile();
	}
	private void saveLayer(StringBuilder str, int layer){
		FileParser p = game.getParser();  
		p.string = str;
		p.addElement("layer", String.valueOf(layer));
		p.newNode();
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
				if(layerGroups.get(currentLayer).tileLayer.map[x][y].mask!=null){
					batch.draw(tileSprites.get(layerGroups.get(currentLayer).tileLayer.map[x][y].id),
							tmp.x, tmp.y,tileWidth,tileHeight+(tileHeight/2));
					overlay.drawMask(batch, tmp.x, tmp.y,
							tileWidth,tileHeight+(tileHeight/2), layerGroups.get(currentLayer).tileLayer.map[x][y].mask);
					overlay.drawOverlay(batch, tmp.x, tmp.y, 
							tileWidth,tileHeight+(tileHeight/2), tileSprites.get(layerGroups.get(currentLayer).tileLayer.map[x][y].overlayId));
					batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
					continue;
				}
				batch.draw(tileSprites.get(layerGroups.get(currentLayer).tileLayer.map[x][y].id),
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
				if(layerGroups.get(currentLayer).entityLayer.isOccupied(x, y)){
					sr.setColor(Color.RED);
					sr.rect(tmp.x, tmp.y, tileWidth, tileHeight);
				}
			}
		}
		sr.end();
	}
	
	@Override
	public boolean blocked(Mover mover,int srcX, int srcY, int x, int y) {
		LayerGroup group = layerGroups.get(currentLayer);
		
		//completely blocked
		if(group.tileLayer.map[x][y].travelCost > 0){
			return true;
		}
		
		if(group.wallLayer.isOccupied(x, y)){
			Entity wall = game.getWorld().getEntity(group.wallLayer.map[x][y]);
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
		return group.entityLayer.isOccupied(x, y);
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
	
	public LayerGroup getLayer(int layer){
		LayerGroup tmp = layerGroups.get(layer);
		if(tmp==null){
			tmp = new LayerGroup(game, worldWidth, worldHeight);
			layerGroups.insert(layer, tmp);
		}
		return tmp;
	}
	
	@Override
	public float getCost(Mover mover, int sx, int sy, int tx, int ty) {
		TileLayer tLayer = getCurrentLayerGroup().tileLayer; 
		return tLayer.map[sx][sx].travelCost + tLayer.map[tx][tx].travelCost; 
	}
	public LayerGroup getCurrentLayerGroup(){
		return layerGroups.get(currentLayer);
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
	public Array<Sprite> loadTileSprites(){
		Array<Sprite> sprites = game.getAtlas().createSprites("terrain/tile");
		return sprites;
	}
	public Array<Sprite> getTileSprites() {
		return tileSprites;
	}
}
