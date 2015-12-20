package com.ado.trader.entities;

public enum WallDirection {
	NW(3),
	NE(2),
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
