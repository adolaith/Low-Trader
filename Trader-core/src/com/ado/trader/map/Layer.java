package com.ado.trader.map;

import com.badlogic.gdx.utils.Json;

public interface Layer {
	
	public int getWidth();
	public int getHeight();
	public boolean isOccupied(int x, int y);
	public void deleteFromMap(int x, int y);
	public void saveLayer(Json chunkJson);
}
