package com.ado.trader.map;

import com.ado.trader.utils.FileParser;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;

public class ItemLayer extends IntMapLayer {
	World world;

	public ItemLayer(int w, int h, World world) {
		super(w, h);
		this.world = world;
	}
	public void addToMap(int i, int x, int y, int h) {
		map[x][y][h] = i;
	}
	public void deleteFromMap(int x, int y, int h) {
		map[x][y][h] = null;
	}
	public Array<Integer> getNeighborItems(int x, int y, int h, int n, Class<? extends Component> type) {
		Array<Integer> neighbours = new Array<Integer>();
		ComponentMapper<Component> mapper = (ComponentMapper<Component>) world.getMapper(type);

		for(int i = x-n; i<x+n+n; i++){
			for(int j = y-n; j<y+n+n; j++){
				if(i==x&&j==y){
					continue;
				}
				if(map[i][j][h]!=null){
					if(mapper.has(world.getEntity(map[i][j][h]))){
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
				Item i = map[x][y][h];
				if(h > 0 && i == null){
					return;
				}else if(i == null){
					continue;
				}
				p.addElement("id", i.getId());
				p.addElement("pos", x+","+y);
				p.newNode();
			}
		}
	}
}
