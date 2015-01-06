package com.ado.trader.map;

import com.ado.trader.map.TileOverlay.Mask;
import com.badlogic.gdx.math.Vector3;

//Tile object class. Tiles have a sprite id, travel cost and a blocked flag.
public class Tile{
	
	Vector3 pos;
	public int id, travelCost, dmg;
	public Integer overlayId;
	public Mask mask;
	
	public Tile(){
		pos = new Vector3();
		travelCost = 0;
		mask = null;
	}
	public Tile(int id, int cost){
		this.id = id;
		pos = new Vector3();
		travelCost = cost;
		mask = null;
	}
	
	public Tile(float x, float y, int h, int id, int cost){
		this(id, cost);
		pos.set(x, y, h);
	}
	public Tile(Tile t){
		this.id = t.id;
		pos = new Vector3(t.getX(), t.getY(), t.getHight());
		travelCost = t.travelCost;
		mask = null;
	}
	public Vector3 getPosition(){
		return pos;
	}
	public void setPosition(float x, float y, int h){
		pos.set(x,y,h);
	}
	public float getX(){
		return pos.x;
	}
	public float getY(){
		return pos.y;
	}
	public int getHight(){
		return (int)pos.z;
	}
}
