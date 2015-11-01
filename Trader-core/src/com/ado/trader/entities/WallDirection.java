package com.ado.trader.entities;

public enum WallDirection {
	NW(0),
	NE(1),
	SW(1),
	SE(0);
	
	private int index;
	
	WallDirection(int index){
		this.index = index;
	}
	public int index(){
		return index;
	}
}
