package com.ado.trader.map;

public interface Layer {
	
	public boolean isOccupied(int x, int y, int h);
	public void deleteFromMap(int x, int y, int h);
}
