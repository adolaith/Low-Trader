package com.ado.trader.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

//Contains tile templates and basic tile pooling
public class TileCollection {
	ArrayMap<Integer, JsonValue> tileProfiles;

	public TileCollection() {
		loadTiles("data/tiles");
	}
	
	public void loadTiles(String fileName){
		tileProfiles = new ArrayMap<Integer, JsonValue>();
		
		Json j = new Json();
		JsonValue l = j.fromJson(null, Gdx.files.internal(fileName));
		l = l.child;
		
		for(JsonValue v = l.child(); v != null; v = v.next){
			tileProfiles.put(v.getInt("id"), v);
		}
	}
	
	//copys given tile
	public Tile createTile(Tile tile){
		return new Tile(tile);
	}
	
	//Takes entityName, gets entityProfile from master collection and creates entity accordingly
	public Tile createTile(int index){
		JsonValue profile = tileProfiles.get(index);
		Tile t = new Tile(index);
		for(JsonValue v = profile.child(); v != null; v = v.next){
			switch(v.name){
			//sets travel cost
			case "travel":
				t.travelCost = v.asInt();
				break;
			}
		}
		return t;
	}
	public ArrayMap<Integer, JsonValue> getTileProfiles() {
		return tileProfiles;
	}
	public JsonValue getTileProfile(int id){
		return tileProfiles.get(id);
	}
}
