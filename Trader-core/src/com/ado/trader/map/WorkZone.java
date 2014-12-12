package com.ado.trader.map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

public class WorkZone extends Zone {
	public ArrayMap<Vector2, Integer> workTiles;
	public ArrayMap<Array<Vector2>, Array<Integer>> workArea;

	public WorkZone(int id, Vector2 zone, ZoneType type) {
		super(id, zone, type);
	}

	public WorkZone(int id, Array<Vector2> area, ZoneType type) {
		super(id, area, type);
	}
	
	public void addWorkTile(Vector2 vec){
		if(workTiles == null){
			workTiles = new ArrayMap<Vector2, Integer>();
		}
		workTiles.put(vec, null);
	}
	
	public void removeWorkTile(Vector2 click){
		workTiles.removeKey(click);
	}
	
	public void addWorkArea(Array<Vector2> area){
		if(workArea == null){
			workArea = new ArrayMap<Array<Vector2>, Array<Integer>>();
			workArea.put(new Array<Vector2>(), new Array<Integer>());
		}
		workArea.put(area, new Array<Integer>());
	}
	
	public void updateWorkArea(Array<Vector2> area){
		workArea.firstKey().clear();
		workArea.firstKey().addAll(area);
	}
	
	public boolean findWork(int id){
		if(workTiles != null){
			for(Vector2 key: workTiles.keys){
				if(workTiles.get(key) == null){
					int index = workTiles.indexOfKey(key);
					workTiles.setValue(index, id);
					return true;
				}
			}
		}
		if(workArea != null){
			for(Array<Vector2> key: workArea.keys()){
				Array<Integer> workers = workArea.get(key);
				if(workers.size < key.size / 4){
					workers.add(id);
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isWorkTile(int x, int y){
		if(workTiles != null){
			for(Vector2 key: workTiles.keys()){
				if(key.x == x && key.y == y){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void removeWorker(int id){
		if(workTiles != null){
			for(Vector2 key: workTiles.keys()){
				if(workTiles.get(key) == id){
					int index = workTiles.indexOfKey(key);
					workTiles.setValue(index, null);
					return;
				}
			}
		}
		if(workArea != null){
			for(Array<Vector2> key: workArea.keys()){
				Array<Integer> workers = workArea.get(key);
				if(workers.removeValue(id, true)){
					return;
				}
			}
		}
	}
	public class WorkArea{
		public Vector2 vec;
		public Array<Vector2> area;
		public Integer entityId;
		public Array<Integer> allEntities;
		public String aiWorkProfile;
		
		public WorkArea(Vector2 vec, String aiProfile){
			this.vec = vec;
			this.entityId = null;
			this.aiWorkProfile = aiProfile;
		}
		public WorkArea(Array<Vector2> area, String aiProfile){
			this.area = area;
			this.aiWorkProfile = aiProfile;
			this.allEntities = new Array<Integer>();
		}
	}
}