package com.ado.trader.map;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class EntityLayer extends IntMapLayer {

	public EntityLayer(int w, int h) {
		super(w, h);
	}
	public void addToMap(Integer id, int x, int y, int h) {
		map[x][y][h] = id;
	}
	public void deleteFromMap(int x, int y, int h) {
		map[x][y][h] = null;
	}
	
	//finds neighbours of x,y to a depth of n with the desired tag.
	public Array<Integer> getNeighborEntitys(int x, int y, int h, int n, String tag, World world) {
		Array<Integer> neighbours = new Array<Integer>();
		GroupManager gm = world.getManager(GroupManager.class);
		for(int i = x-n; i<x+n+n; i++){
			for(int j = y-n; j<y+n+n; j++){
				if((i==x && j==y) || i < 0 || j < 0 || i >= map.length || j >= map[i].length){
					continue;
				}
				if(map[i][j]!=null){
					Entity e = world.getEntity(map[i][j][h]);
					if(gm.isInGroup(e, tag)){
						neighbours.add(map[i][j][h]);
					}
				}
			}
		}
		return neighbours;		  
	}
	public Entity getClosestEntity(int x, int y, int h,int n, String tag, World world){
		GroupManager gm = world.getManager(GroupManager.class);
		for(int d = 1; d <= n; d++){
			int i = x - d;
			int j = y + d;
			
			if(outOfMap(i, j, h)){
				continue;
			}
			
			if(map[i][j]!=null){
				Entity e = world.getEntity(map[i][j][h]);
				if(gm.isInGroup(e, tag)){
					return e;
				}
			}
			
			for(int q = 0; q < d*2; q++){
				i++;
				if(outOfMap(i, j, h)){
					continue;
				}
				if(map[i][j]!=null){
					Entity e = world.getEntity(map[i][j][h]);
					if(gm.isInGroup(e, tag)){
						return e;
					}
				}
			}
			for(int q = 0; q < d*2; q++){
				j--;
				if(outOfMap(i, j, h)){
					continue;
				}
				if(map[i][j]!=null){
					Entity e = world.getEntity(map[i][j][h]);
					if(gm.isInGroup(e, tag)){
						return e;
					}
				}
			}
			for(int q = 0; q < d*2; q++){
				i--;
				if(outOfMap(i, j, h)){
					continue;
				}
				if(map[i][j]!=null){
					Entity e = world.getEntity(map[i][j][h]);
					if(gm.isInGroup(e, tag)){
						return e;
					}
				}
			}
			for(int q = 0; q < d*2-1; q++){
				j++;
				if(outOfMap(i, j, h)){
					continue;
				}
				if(map[i][j]!=null){
					Entity e = world.getEntity(map[i][j][h]);
					if(gm.isInGroup(e, tag)){
						return e;
					}
				}
			}
		}
		return null;
	}
	private boolean outOfMap(int x, int y, int h){
		if(x < 0 || x >= map.length ||
				y < 0 || y >= map[x].length){
			return true;
		}
		return false;
	}
	//returns the first entity found inside given area
	public Integer getFirstEntity(Vector2 origin, Vector2 widthHeight, int h){
		for(int x=(int)origin.x; x<=(int)widthHeight.x; x++){
			for(int y=(int)origin.y; y<=(int)widthHeight.y; y++){
				if(map[x][y]!=null){
					return map[x][y][h];
				}
			}
		}
		return null;
	}
}
