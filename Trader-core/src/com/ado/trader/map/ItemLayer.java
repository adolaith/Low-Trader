package com.ado.trader.map;

import com.ado.trader.items.Item;
import com.ado.trader.items.ItemData;
import com.ado.trader.utils.FileParser;
import com.badlogic.gdx.utils.Array;

public class ItemLayer implements Layer {
	public Item[][][] map;

	public ItemLayer(int w, int h) {
		map = new Item[w][h][];
	}
	public void addToMap(Item i, int x, int y, int h) {
		map[x][y][h] = i;
	}
	public void deleteFromMap(int x, int y, int h) {
		map[x][y][h] = null;
	}
	public Array<Item> getNeighborItems(int x, int y, int h, int n, Class<? extends ItemData> type) {
		Array<Item> neighbours = new Array<Item>();

		for(int i = x-n; i<x+n+n; i++){
			for(int j = y-n; j<y+n+n; j++){
				if(i==x&&j==y){
					continue;
				}
				if(map[i][j]!=null){
					if(map[i][j][h].hasDataType(type)){
						neighbours.add(map[i][j][h]);
					}
				}
			}
		}
		return neighbours;		  
	}
	
	//searches in an outward spiral from x,y to a depth of 'n' for item type
	public Item getClosestItem(int x, int y, int h,int n, Class<? extends ItemData> type){
		for(int d = 1; d <= n; d++){
			int i = x - d;
			int j = y + d;
			
			if(checkMap(i, j, h, type)){
				return map[i][j][h];
			}
			
			for(int q = 0; q < d*2; q++){
				i++;
				if(checkMap(i, j, h, type)){
					return map[i][j][h];
				}
			}
			for(int q = 0; q < d*2; q++){
				j--;
				if(checkMap(i, j, h, type)){
					return map[i][j][h];
				}
			}
			for(int q = 0; q < d*2; q++){
				i--;
				if(checkMap(i, j, h, type)){
					return map[i][j][h];
				}
			}
			for(int q = 0; q < d*2-1; q++){
				j++;
				if(checkMap(i, j, h, type)){
					return map[i][j][h];
				}
			}
		}
		return null;
	}
	private boolean checkMap(int x, int y, int h, Class<? extends ItemData> type){
		if(x < 0 || y < 0 || x >= map.length || y >= map[x].length){
			return false;
		}
		if(map[x][y]!=null){
			if(map[x][y][h].hasDataType(type)){
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean isOccupied(int x, int y, int h) {
		return map[x][y][h] != null;
	}
	public void saveMap(FileParser p, int h, StringBuilder str){
		p.string = str;

		for(int x=0; x<map.length; x++){
			for(int y=0; y<map[x].length; y++){
				if(map[x][y]==null)continue;
				Item i = map[x][y][h];
				p.addElement("id", i.getId());
				p.addElement("pos", x+","+y);
				p.newNode();
			}
		}
	}
}
