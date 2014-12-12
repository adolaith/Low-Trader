package com.ado.trader.map;

import com.ado.trader.screens.GameScreen;

public class WallLayer extends IntMapLayer {

	public WallLayer(GameScreen game, int w, int h) {
		super(game, w, h);
	}
	@Override
	public void addToMap(Integer id, int x, int y) {
		map[x][y] = id;
	}
	public void deleteFromMap(int x, int y) {
		map[x][y] = null;
	}
}
