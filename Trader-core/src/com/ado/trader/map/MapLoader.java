package com.ado.trader.map;

import com.ado.trader.GameMain;
import com.ado.trader.items.Item;
import com.ado.trader.items.ItemCollection;
import com.ado.trader.items.ItemPosition;
import com.ado.trader.systems.GameTime;
import com.ado.trader.utils.FileParser;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

public class MapLoader {
	Map map;
	FileParser parser;
	GameTime time;
	ItemCollection items;
	
	public MapLoader(Map map, ItemCollection items, FileParser parser, GameTime time) {
		this.map = map;
		this.parser = parser;
		this.time = time;
		this.items = items;
	}
	public void loadMap(String dirName){
		parser.initParser("saves/"+dirName+"/map.sav", false, true);
		if(parser.getFile().readString().isEmpty()){Gdx.app.log(GameMain.LOG, "Save file is empty"); return;}
		Array<ArrayMap<String, String>> data = parser.readFile();
		
		ArrayMap<String, String> timeData = data.removeIndex(0);
		time.loadSettings(Integer.valueOf(timeData.get("days")), GameTime.Time.valueOf(timeData.get("ToD")), Integer.valueOf(timeData.get("time")));
		map.currentLayer = Integer.valueOf(data.removeIndex(0).get("layer"));
		
		parser.initParser("saves/"+dirName+"/zone.sav", false, true);
		Array<ArrayMap<String, String>> zoneData = parser.readFile(); 
		zoneData.removeIndex(0);
		parser.initParser("saves/"+dirName+"/items.sav", false, true);
		Array<ArrayMap<String, String>> itemsData = parser.readFile(); 
		itemsData.removeIndex(0);
		
		//loop saved map layers and load tiles, zones and items
		boolean loading = true;
		while(loading){
			loading = loadTileLayer(data);
			loadItems(itemsData);
		}
	}
	
	private boolean loadTileLayer(Array<ArrayMap<String, String>> data){
		Tile[][][] savedMap = new Tile[map.worldWidth][map.worldHeight][];
		
		int count = 0;
		for(int x=0; x<savedMap.length; x++){
			for(int y=0; y<savedMap[x].length; y++){
				if(data.get(count).containsKey("layer")){
					map.tileLayer.map = savedMap;
					map.currentLayer = Integer.valueOf(data.get(count).get("layer"));
					data.removeRange(0, count);
					return true;
				}
				Tile t = map.tilePool.createTile(Integer.valueOf(data.get(count).get("id")), x, y, map.currentLayer);
				savedMap[x][y][map.currentLayer] = t;
				count++;
			}
		}
		map.tileLayer.map = savedMap;
		return false;
	}
	
	private void loadItems(Array<ArrayMap<String, String>> data){
		int count = 0;
		for(ArrayMap<String, String> i: data){
			if(i.containsKey("layer")){
				data.removeRange(0, count);
				return;
			}
			String[] list = i.get("pos").split(",");
			Item item = items.createItem(i.get("id"));
			ItemPosition pos = item.getData(ItemPosition.class);
			pos.position.set(Integer.valueOf(list[0]), Integer.valueOf(list[1]), map.currentLayer);
			map.itemLayer.addToMap(item, Integer.valueOf(list[0]), Integer.valueOf(list[1]), map.currentLayer);
			count++;
		}
	}
	
	public void saveMap(String dir){
		StringBuilder tileString= new StringBuilder();
		StringBuilder itemString= new StringBuilder();
		
		parser.string = tileString;
		parser.addElement("ToD", String.valueOf(time.getTimeOfDay()));
		parser.addElement("time", String.valueOf(time.getTime()));
		parser.addElement("days", String.valueOf(time.getDays()));
		parser.newNode();
		for(int x=0; x < map.tileLayer.map[0][0].length; x++){
			saveLayer(tileString, x);
			saveLayer(itemString, x);
			
			map.tileLayer.saveMap(parser, x, tileString);
			map.itemLayer.saveMap(parser, x, itemString);
		}
		writeLayers(dir, "map", tileString);
		writeLayers(dir, "items", itemString);
	}
	private void writeLayers(String dir,String name, StringBuilder str){
		parser.initParser("saves/"+dir+"/"+name+".sav", true, true);
		parser.string = str;
		parser.writeToFile();
	}
	private void saveLayer(StringBuilder str, int layer){
		parser.string = str;
		parser.addElement("layer", String.valueOf(layer));
		parser.newNode();
	}
}
