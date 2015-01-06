package com.ado.trader.map;

import java.io.IOException;

import com.ado.trader.utils.FileParser;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

//Contains tile templates and basic tile pooling
public class TileCollection {
	ArrayMap<Integer, ArrayMap<String,String>> tileProfiles;

	public TileCollection(FileParser p) {
		try {
			loadTiles("data/tiles", p);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadTiles(String fileName, FileParser p) throws IOException{
		tileProfiles = new ArrayMap<Integer, ArrayMap<String,String>>();
		p.initParser(fileName, false, false);
		
		Array<ArrayMap<String, String>> array = p.readFile();
		
		for(ArrayMap<String, String> tile: array){
			int id = Integer.valueOf(tile.get("id"));
			tile.removeKey("id");
			tileProfiles.put(id, tile);
		}
	}
	
	//copys given tile
	public Tile createTile(Tile tile){
		return new Tile(tile);
	}
	
	//Takes entityName, gets entityProfile from master collection and creates entity accordingly
	public Tile createTile(int index, int x, int y, int h){
		ArrayMap<String, String> profile = tileProfiles.get(index);
		Tile t = new Tile();
		for(String key: profile.keys()){
			switch(key){
			//sets travel cost
			case "travel":
				t.travelCost = Integer.valueOf(profile.get(key));
				break;
			case "dmg":
				t.dmg = Integer.valueOf(profile.get(key));
				break;
			}
		}
		t.id = index;
		t.setPosition(x, y, h);
		return t;
	}
	public ArrayMap<Integer, ArrayMap<String, String>> getTileProfiles() {
		return tileProfiles;
	}
	public ArrayMap<String, String> getTileProfile(int id){
		return tileProfiles.get(id);
	}
}
