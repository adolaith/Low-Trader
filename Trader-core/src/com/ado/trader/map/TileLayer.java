package com.ado.trader.map;

import com.ado.trader.utils.FileParser;

public class TileLayer implements Layer{
	public Tile[][][] map;
	
	public TileLayer(int w, int h){
		map = new Tile[w][h][8];
	}
	
	public void saveMap(FileParser p, int h, StringBuilder str){
		p.string = str;

		for(int x=0; x<map.length; x++){
			for(int y=0; y<map[x].length; y++){
				Tile t = map[x][y][h];
				if(h > 0 && t == null){
					return;
				}
				p.addElement("id", String.valueOf(t.id));
				p.newNode();
			}
		}
	}
	@Override
	public boolean isOccupied(int x, int y, int h) {
		return false;
	}

	@Override
	public void deleteFromMap(int x, int y, int h) {
	}
}
