package com.ado.trader.map;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.entities.components.Name;
import com.ado.trader.items.ItemFactory;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class ItemLayer extends IntMapLayer {
	ComponentMapper<Name> nameMap;
	World world;

	public ItemLayer(int w, int h, int c, World world) {
		super(w, h, c);
		this.world = world;
		nameMap = world.getMapper(Name.class);
	}
	public boolean addToMap(Integer i, int x, int y) {
		for(int c = 0; c < map[x][y].length; c++){
			if(map[x][y][c] == null){
				map[x][y][c] = i;
				return true;
			}
		}
		return false;
	}
	public void deleteFromMap(int x, int y, int id) {
		for(int c = 0; c < map[x][y].length; c++){
			if(map[x][y][c] == id){
				map[x][y][c] = null;
				return;
			}
		}
	}
	public Array<Integer> getNeighborItems(int x, int y, int n, Class<? extends Component> type) {
		Array<Integer> neighbours = new Array<Integer>();
		ComponentMapper<Component> mapper = (ComponentMapper<Component>) world.getMapper(type);

		for(int i = x-n; i<x+n+n; i++){
			for(int j = y-n; j<y+n+n; j++){
				if(i==x&&j==y){
					continue;
				}
				for(int c = 0; c < map[x][y].length; c++){
					if(map[i][j][c]!=null){
						if(mapper.has(world.getEntity(map[i][j][c]))){
							neighbours.add(map[i][j][c]);
						}
					}
				}
				
			}
		}
		return neighbours;		  
	}
	
	//searches in an outward spiral from x,y to a depth of 'n' for item type
	public Integer getClosestItem(int x, int y,int n, Class<? extends Component> type){
		ComponentMapper<Component> mapper = (ComponentMapper<Component>) world.getMapper(type);
		Integer item = null;
		for(int d = 1; d <= n; d++){
			int i = x - d;
			int j = y + d;
			
			item = checkMap(i, j, mapper);
			if(item != null){
				return item;
			}
			
			for(int q = 0; q < d*2; q++){
				i++;
				item = checkMap(i, j, mapper);
				if(item != null){
					return item;
				}
			}
			for(int q = 0; q < d*2; q++){
				j--;
				item = checkMap(i, j, mapper);
				if(item != null){
					return item;
				}
			}
			for(int q = 0; q < d*2; q++){
				i--;
				item = checkMap(i, j, mapper);
				if(item != null){
					return item;
				}
			}
			for(int q = 0; q < d*2-1; q++){
				j++;
				item = checkMap(i, j, mapper);
				if(item != null){
					return item;
				}
			}
		}
		return null;
	}
	private Integer checkMap(int x, int y, ComponentMapper<Component> mapper){
		if(x < 0 || y < 0 || x >= map.length || y >= map[x].length){
			return null;
		}
		for(int i = 0; i < map[x][y].length; i++){
			if(map[x][y][i]!=null){
				if(mapper.has(world.getEntity(map[x][y][i]))){
					return map[x][y][i];
				}
			}
		}
		return null;
	}
	@Override
	public boolean isOccupied(int x, int y) {
		return map[x][y][0] != null;
	}
	public int getWidth() {
		return map.length;
	}
	public int getHeight() {
		return map[0].length;
	}
	public void loadLayer(JsonValue items, Map map){
		String[] xy;
		for(JsonValue i = items.child; i != null; i = i.next){
			xy = i.getString("p").split(",");
			
			Entity e = ItemFactory.createItem(i.getString("id"));
			
			addToMap(e.getId(), Integer.valueOf(xy[0]), Integer.valueOf(xy[1]));
		}
	}
	@Override
	public void saveLayer(Json chunkJson) {
		chunkJson.writeArrayStart("items");

		for(int x=0; x<map.length; x++){
			for(int y=0; y<map[x].length; y++){
				for(int c = 0; c < map[x][y].length; c++){	
					if(map[x][y][c] == null){
						continue;
					}
					Entity i = world.getEntity(map[x][y][c]);
					
					chunkJson.writeObjectStart();
					chunkJson.writeValue("p", x +","+ y);
					chunkJson.writeValue("id", nameMap.get(i).getName());
					chunkJson.writeObjectEnd();
				}
			}
		}
		chunkJson.writeArrayEnd();
	}
}
