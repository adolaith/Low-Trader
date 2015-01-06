package com.ado.trader.buildings;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

//needs to interact with a building system to allocate builders for construction
public class HardwareStore extends Business {
	Array<Integer> builders;
	Array<Integer> availableBuilders;
	final String aiProfile = "builder"; 

	public HardwareStore(int id) {
		super(id);
		builders = new Array<Integer>();
		availableBuilders = new Array<Integer>();
	}
	public HardwareStore(int id, Array<Vector3> tiles) {
		super(id, tiles);
		builders = new Array<Integer>();
		availableBuilders = new Array<Integer>();
	}
	public int getAvailableBuilders(){
		return availableBuilders.size;
	}
	
	@Override
	public boolean findWork(int id){
		if(!super.findWork(id)){
			if(builders.size < 8){
				builders.add(id);
				availableBuilders.add(id);
				return true;
			}
		}else{
			return true;
		}
		return false;
	}
	@Override
	public void removeWorker(Integer workTileId, int workerId){
		if(workTileId == null){
			builders.removeValue(workTileId, true);
			availableBuilders.removeValue(id, true);
		}else{
			super.removeWorker(workTileId, workerId);
		}
	}
	@Override
	public String getWorkerAiProfile(){
		return aiProfile;
	}
}
