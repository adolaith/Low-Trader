package com.ado.trader.map;


public class WallLayer extends IntMapLayer {

	public WallLayer(int w, int h) {
		super(w, h);
	}
	@Override
	public void addToMap(Integer id, int x, int h, int y) {
		map[x][y][h] = id;
	}
	public void deleteFromMap(int x, int y, int h) {
		map[x][y][h] = null;
	}
}
