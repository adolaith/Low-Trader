package com.ado.trader.map;


public abstract class IntMapLayer implements Layer{
	public Integer[][][] map;
	
	public IntMapLayer(int w, int h){
		map = new Integer[w][h][8];
	}
	public boolean isOccupied(int x, int y, int h){
		return map[x][y]!=null;
	}
	public void addToMap(Integer id, int x, int y, int h) {}
	public void deleteFromMap(int x, int y, int h){}
}
