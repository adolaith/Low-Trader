 package com.ado.trader.buildings;

import com.ado.trader.buildings.BuildingEnum.BuildingType;
import com.ado.trader.buildings.BuildingEnum.SubType;
import com.badlogic.gdx.utils.Array;

public class Building {
	Integer ownerId;
	int id, x, y, w, h;
	int numOfFloors;
	Array<WorkArea> workAreas;
	
	BuildingType type;
	SubType subType;

	public Building() {

	}
	
	
	public void setDimensions(int minX, int minY, int maxX, int maxY){
		this.x = minX;
		this.y = minY;
		this.w = maxX - minX;
		this.h = maxY - minY;
	}
	public int getId(){
		return id;
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public int getWidth(){
		return w;
	}
	public int getHeight(){
		return h;
	}
	public Integer getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}
	public int getNumOfFloors() {
		return numOfFloors;
	}
	public void setNumOfFloors(int numOfFloors) {
		this.numOfFloors = numOfFloors;
	}
	public BuildingType getType() {
		return type;
	}
	public void setType(BuildingType type) {
		this.type = type;
	}
	public SubType getSubType() {
		return subType;
	}
	public void setSubType(SubType subType) {
		this.subType = subType;
	}
}
