package com.ado.trader.buildings;

import com.ado.trader.buildings.BuildingEnum.BuildingType;
import com.ado.trader.screens.GameScreen;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ArrayMap.Values;

public class BuildingCollection {
	ArrayMap<BuildingType, Array<Building>> allBuildings;
	ArrayMap<String, ArrayMap<String, String>> buildingProfiles;
	GameScreen game;
	BuildingLoader loader;

	public BuildingCollection(GameScreen game) {
		this.game = game;
		allBuildings = new ArrayMap<BuildingEnum.BuildingType, Array<Building>>();
		buildingProfiles = new ArrayMap<String, ArrayMap<String, String>>();
	}

	public Array<Building> getBuildings(BuildingType type){
		return allBuildings.get(type);
	}
	
	public Building getBuilding(int id){
		for(Array<Building> bList: allBuildings.values()){
			for(Building b: bList){
				if(b.getBuildingId() == id){
					return b;
				}
			}
		}
		return null;
	}
	public Values<Array<Building>> getAllBuildings(){
		return allBuildings.values();
	}
	
}
