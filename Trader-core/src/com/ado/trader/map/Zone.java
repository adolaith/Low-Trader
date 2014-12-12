package com.ado.trader.map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Zone {
	Array<Vector2> zoneTiles;
	public ZoneType type;
	int id;
	
	public Zone(int id, Vector2 zone, ZoneType type){
		zoneTiles = new Array<Vector2>();
		zoneTiles.add(zone);
		this.type = type;
		this.id = id;
	}
	public Zone(int id, Array<Vector2> area, ZoneType type){
		zoneTiles = area;
		this.type = type;
		this.id = id;
	}
	public int getId(){
		return id;
	}
	public void addZoneTile(Vector2 tile){
		zoneTiles.add(tile);
	}
	public void removeTile(Vector2 tile){
		zoneTiles.removeValue(tile, false);
	}
	public boolean zoneHasTile(Vector2 tile) {
		return zoneTiles.contains(tile, false);
	}
	public Array<Vector2> getTileList(){
		return zoneTiles;
	}
	public enum ZoneType{
		HOME, FARM, STORE, ENTERTAINMENT, EATERY;
	}
}
