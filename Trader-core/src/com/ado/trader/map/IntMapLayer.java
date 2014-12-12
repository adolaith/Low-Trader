package com.ado.trader.map;

import com.ado.trader.screens.GameScreen;

public abstract class IntMapLayer implements Layer{
	public Integer[][] map;
	GameScreen game;
	
	public IntMapLayer(GameScreen game, int w, int h){
		this.game = game;
		map = new Integer[w][h];
	}
	public boolean isOccupied(int x, int y){
		return map[x][y]!=null;
	}
	public void addToMap(Integer id, int x, int y) {}
	public void deleteFromMap(int x, int y){}
}
