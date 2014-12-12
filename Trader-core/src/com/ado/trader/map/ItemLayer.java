package com.ado.trader.map;

import com.ado.trader.items.Item;
import com.ado.trader.items.ItemData;
import com.ado.trader.screens.GameScreen;
import com.ado.trader.utils.FileParser;
import com.badlogic.gdx.utils.Array;

public class ItemLayer implements Layer {
	public Item[][] map;
	GameScreen game;

	public ItemLayer(GameScreen game, int w, int h) {
		map = new Item[w][h];
		this.game = game;
	}
	public void addToMap(Item i, int x, int y) {
		map[x][y] = i;
	}
	public void deleteFromMap(int x, int y) {
		map[x][y] = null;
	}
	public Array<Item> getNeighborItems(int x, int y, int n, Class<? extends ItemData> type) {
		Array<Item> neighbours = new Array<Item>();

		for(int i = x-n; i<x+n+n; i++){
			for(int j = y-n; j<y+n+n; j++){
				if(i==x&&j==y){
					continue;
				}
				if(map[i][j]!=null){
					if(map[i][j].hasDataType(type)){
						neighbours.add(map[i][j]);
					}
				}
			}
		}
		return neighbours;		  
	}
	
	//searches in an outward spiral from x,y to a depth of 'n' for item type
	public Item getClosestItem(int x, int y,int n, Class<? extends ItemData> type){
		for(int d = 1; d <= n; d++){
			int i = x - d;
			int j = y + d;
			
			if(checkMap(i, j, type)){
				return map[i][j];
			}
			
			for(int q = 0; q < d*2; q++){
				i++;
				if(checkMap(i, j, type)){
					return map[i][j];
				}
			}
			for(int q = 0; q < d*2; q++){
				j--;
				if(checkMap(i, j, type)){
					return map[i][j];
				}
			}
			for(int q = 0; q < d*2; q++){
				i--;
				if(checkMap(i, j, type)){
					return map[i][j];
				}
			}
			for(int q = 0; q < d*2-1; q++){
				j++;
				if(checkMap(i, j, type)){
					return map[i][j];
				}
			}
		}
		return null;
	}
	private boolean checkMap(int x, int y, Class<? extends ItemData> type){
		if(x < 0 || y < 0 || x >= map.length || y >= map[x].length){
			return false;
		}
		if(map[x][y]!=null){
			if(map[x][y].hasDataType(type)){
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean isOccupied(int x, int y) {
		return map[x][y] != null;
	}
	public void saveMap(GameScreen game, StringBuilder str){
		FileParser p = game.getParser();  
		p.string = str;

		for(int x=0; x<map.length; x++){
			for(int y=0; y<map[x].length; y++){
				if(map[x][y]==null)continue;
				Item i = map[x][y];
				p.addElement("id", i.getId());
				p.addElement("pos", x+","+y);
				p.newNode();
			}
		}
	}
}
