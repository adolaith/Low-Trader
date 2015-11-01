package com.ado.trader.map;

public abstract class IntMapLayer implements Layer{
	public Integer[][][] map;
	
	//map width, height and tile capacity
	public IntMapLayer(int w, int h, int c){
		map = new Integer[w][h][c];
	}
	public boolean isOccupied(int x, int y){
		return map[x][y][0]!=null;
	}
	public boolean addToMap(Integer id, int x, int y) {
		return false;
	}
	public void deleteFromMap(int x, int y){}
}
