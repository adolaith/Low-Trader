package com.ado.trader.buildings;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Building {
	int id;
	Array<Vector3> tiles;

	public Building(int id) {
		this.id = id;
		tiles = new Array<Vector3>();
	}
	public Building(int id, Array<Vector3> tiles){
		this.id = id;
		this.tiles = tiles;
	}
	public boolean hasTile(int x, int y, int h){
		for(Vector3 vec: tiles){
			if(vec.x == x && vec.y == y && vec.z == h){
				return true;
			}
		}
		return false;
	}
	public void addTile(int x, int y, int h){
		tiles.add(new Vector3(x, y, h));
	}
	public void removeTile(Vector3 vec){
		tiles.removeValue(vec, false);
	}
	public int getBuildingId(){
		return id;
	}
}
