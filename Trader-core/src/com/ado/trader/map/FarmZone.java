package com.ado.trader.map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class FarmZone extends WorkZone {
	public String itemName;
	public int daysGrowing;
	public float maintenance, growScore;
	
	public FarmZone(int id, Array<Vector2> area){
		super(id, area, ZoneType.FARM);
		addWorkArea(area, "workFarmer");
		itemName = "";
	}
	public FarmZone(int id, Vector2 zone){
		super(id, zone, ZoneType.FARM);
		Array<Vector2> a = new Array<Vector2>();
		a.add(zone);
		addWorkArea(a, "workFarmer");
		itemName = "";
	}
	
	public void addToZone(Vector2 vec, Zone z){
		super.addZoneTile(vec);
		updateWorkArea(zoneTiles);
	}
	public void removeTile(Vector2 tile){
		super.removeTile(tile);
		updateWorkArea(zoneTiles);
	}
}
