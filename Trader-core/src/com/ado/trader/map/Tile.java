package com.ado.trader.map;

import com.ado.trader.map.TileOverlay.Mask;
import com.badlogic.gdx.math.Vector2;

//Tile object class. Tiles have a sprite id, travel cost and a blocked flag.
public class Tile{
	
	Vector2 pos;
	public int id, travelCost, dmg;
	public Integer overlayId;
	public Mask mask;
	
	public Tile(){
		pos = new Vector2();
		travelCost = 0;
		mask = null;
	}
	public Tile(int id, int cost){
		this.id = id;
		pos = new Vector2();
		travelCost = cost;
		mask = null;
	}
	
	public Tile(float x, float y, int id, int cost){
		this(id, cost);
		pos.set(x, y);
	}
	public Tile(Tile t){
		this.id = t.id;
		pos = new Vector2(t.getX(), t.getY());
		travelCost = t.travelCost;
		mask = null;
	}
	public Vector2 getPosition(){
		return pos;
	}
	public void setPosition(float x, float y ){
		pos.set(x,y);
	}
	public float getX(){
		return pos.x;
	}
	public float getY(){
		return pos.y;
	}
}