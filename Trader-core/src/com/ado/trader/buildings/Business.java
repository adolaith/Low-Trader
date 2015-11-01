package com.ado.trader.buildings;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Business extends Building {
	int nextWorkId;
	Integer ownerId;
	Array<WorkTile> workTiles;

	public Business(int id) {
		super(id);
		workTiles = new Array<Business.WorkTile>();
	}
	public Business(int id, Array<Vector3> tiles) {
		super(id, tiles);
		workTiles = new Array<Business.WorkTile>();
	}
	public boolean findWork(int id){
		for(WorkTile t: workTiles){
			if(t.hasWork(id)){
				return true;
			}
		}
		return false;
	}
	public WorkTile getWorkPlace(int id){
		for(WorkTile t: workTiles){
			if(t.getWorkId() == id){
				return t;
			}
		}
		return null;
	}
	public void addWorkTile(WorkTile work){
		workTiles.add(work);
	}
	public String getWorkerAiProfile(){
		return null;
	}
	public void removeWorker(Integer workTileId, int workerId){
		WorkTile t = getWorkPlace(workTileId);
		t.removeWorker(workerId);
	}
	public Array<WorkTile> getWorkTiles() {
		return workTiles;
	}
	public Integer getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}

	class WorkTile{
		int workId;
		Integer workerId;
		String aiProfile;
		Vector3 tile;
		Array<Vector3> area;
		Array<Integer> allWorkers;
		
		public WorkTile(int id, Vector3 vec, String aiProfile){
			workId = id;
			tile = vec;
			this.aiProfile = aiProfile;
		}
		public WorkTile(int id, Array<Vector3> area, String aiProfile){
			workId = id;
			this.area = area;
			allWorkers = new Array<Integer>();
			this.aiProfile = aiProfile;
		}
		public boolean hasWork(int id){
			if(tile != null){
				if(workerId != null){
					workerId = id;
					return true;
				}
			}
			if(area != null){
				if(allWorkers.size < area.size / 4){
					allWorkers.add(id);
					return true;
				}
			}
			return false;
		}
		public void removeWorker(int id){
			if(workerId != null){
				if(workerId == id){
					workerId = null;
					return;
				}
			}
			if(area != null){
				if(allWorkers.contains(id, true)){
					allWorkers.removeValue(id, true);
				}
			}
		}
		public int getWorkId() {
			return workId;
		}
		public String getAiProfile() {
			return aiProfile;
		}
		public Vector3 getTile() {
			return tile;
		}
		public Array<Vector3> getArea() {
			return area;
		}
		public Array<Integer> getAllWorkers() {
			return allWorkers;
		}
	}
}
