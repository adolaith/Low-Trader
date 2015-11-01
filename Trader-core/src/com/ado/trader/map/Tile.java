package com.ado.trader.map;

//Tile object class. Tiles have a sprite id, travel cost and a blocked flag.
public class Tile{
	
	int id, travelCost;
	TileMask mask;
	
	public Tile(int id){
		this.id = id;
		travelCost = 0;
		mask = null;
	}
	public Tile(int id, int cost){
		this.id = id;
		travelCost = cost;
		mask = null;
	}
	public Tile(Tile t){
		this.id = t.id;
		travelCost = t.travelCost;
		mask = null;
	}
	public TileMask getMask() {
		return mask;
	}
	public void setMask(TileMask mask) {
		this.mask = mask;
	}
	public int getId() {
		return id;
	}
	public void setId(int id){
		this.id = id;
	}
	public int getTravelCost() {
		return travelCost;
	}
	public void setTravelCost(int cost){
		this.travelCost = cost;
	}
}
