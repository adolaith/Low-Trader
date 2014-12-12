package com.ado.trader.map;

import com.ado.trader.screens.GameScreen;
import com.ado.trader.utils.FileParser;

public class TileLayer implements Layer{
	public Tile[][] map;
	
	public TileLayer(GameScreen game,int w, int h){
		map = new Tile[w][h];
	}
	
	public void saveMap(GameScreen game, StringBuilder str){
		FileParser p = game.getParser();  
		p.string = str;

		for(int x=0; x<map.length; x++){
			for(int y=0; y<map[x].length; y++){
				Tile t = map[x][y];
				p.addElement("id", String.valueOf(t.id));
				p.newNode();
			}
		}
	}
	@Override
	public boolean isOccupied(int x, int y) {
		return false;
	}

	@Override
	public void deleteFromMap(int x, int y) {
	}
}
