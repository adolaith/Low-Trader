package com.ado.trader.systems;

import com.ado.trader.map.FarmZone;
import com.ado.trader.map.Tile;
import com.ado.trader.map.TileLayer;
import com.ado.trader.map.Zone;
import com.ado.trader.map.ZoneLayer;
import com.ado.trader.map.Zone.ZoneType;
import com.ado.trader.screens.GameScreen;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

public class FarmSystem extends VoidEntitySystem{
	ArrayMap<String, ArrayMap<String, Integer>> farmProfiles;
	GameScreen game;
	
	public FarmSystem(GameScreen game){
		super();
		this.game = game;
	}

	@Override
	protected void processSystem(){
		ZoneLayer zLayer = game.getMap().getLayer(0).zoneLayer;
		Array<Zone> zList = zLayer.getZoneList(ZoneType.FARM);
		for(Zone z: zList){
			FarmZone f = (FarmZone)z;
			if(f.itemName == null) continue;
			
			ArrayMap<String, Integer> profile = farmProfiles.get(f.itemName);
			
			f.daysGrowing++;
			
			f.growScore++;
//			if(f.maintenance > 0.6f){
//				f.growScore++;
//				f.maintenance = 0;
//			}else{
//				f.maintenance = 0;
//			}
			
			TileLayer tLayer = game.getMap().getLayer(0).tileLayer;
			if(f.daysGrowing>=profile.get("growTime")/2 &&		//mid growth point
					f.daysGrowing != profile.get("growTime")){
				for(Vector2 vec: f.getTileList()){
					Tile t = tLayer.map[(int)vec.x][(int)vec.y];
					t.id = profile.get("stage1");
				}
			}else if(f.daysGrowing == profile.get("growTime")){	//final growth point (harvest)
				for(Vector2 vec: f.getTileList()){
					Tile t = tLayer.map[(int)vec.x][(int)vec.y];
					t.id = profile.get("stage2");
				}
			}else if(f.daysGrowing == profile.get("growTime") + 2){		//farm resets(unharvested are crops lost)
				for(Vector2 vec: f.getTileList()){
					Tile t = tLayer.map[(int)vec.x][(int)vec.y];
					t.id = 2;
				}
				f.itemName = "";
			}
			
		}
	}
	public void loadProfiles(ArrayMap<String, ArrayMap<String, Integer>> farmProfiles){
		this.farmProfiles = farmProfiles;
	}
	public ArrayMap<String, Integer> getProfile(String key){
		return farmProfiles.get(key);
	}
	public ArrayMap<String, ArrayMap<String, Integer>> getProfiles() {
		return farmProfiles;
	}
}
