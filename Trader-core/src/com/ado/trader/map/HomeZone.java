package com.ado.trader.map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class HomeZone extends Zone {
	public FarmZone garden;
	public Array<Integer> occupants;
	public int maxOccupants;

	public HomeZone(int id, Vector2 zone, ZoneType type) {
		super(id, zone, type);
		occupants = new Array<Integer>();
		garden = null;
	}
	public HomeZone(int id, Array<Vector2> area, ZoneType type) {
		super(id, area, type);
		occupants = new Array<Integer>();
		garden = null;
	}
	public boolean houseIsFull(){
		if(occupants.size >= maxOccupants){
			return true;
		}
		return false;
	}
	public void addOccupant(int id){
		occupants.add(id);
	}
}
