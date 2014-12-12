package com.ado.trader.map;

public interface Layer {
	
	public boolean isOccupied(int x, int y);
	public void deleteFromMap(int x, int y);
}
