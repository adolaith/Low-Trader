package com.ado.trader.map;

import java.io.FileWriter;
import java.io.IOException;

import com.ado.trader.utils.IsoUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.TimeUtils;

public class EditorStreamer extends MapStreamer {
	FileHandle dir;
	int shiftX, shiftY;

	public EditorStreamer(Map map) {
		super(map);
		FileHandle tmpDir = Gdx.files.external("adoGame/editor/maps/tmp");
		if(tmpDir.exists()){
			tmpDir.deleteDirectory();
		}
	}
	public void streamMap(OrthographicCamera cam){
//		long t = TimeUtils.nanoTime();
		
		Vector2 camRegion = IsoUtils.getColRow((int) cam.position.x, (int) cam.position.y, Map.tileWidth, Map.tileHeight);
		camRegion = Map.worldVecToRegion((int) camRegion.x, (int) camRegion.y);
		
		if(camRegion.x == 1 && camRegion.y == 1) return;
		
		shiftX = 1 - (int) camRegion.x;
		shiftY = 1 - (int) camRegion.y;
//		Gdx.app.log("Streamer: ", "========================");
//		Gdx.app.log("Streamer: ", "SHIFT REGION XY BY: "+ shiftX +", "+ shiftY);
		
		Array<MapRegion> unloadRegions = new Array<MapRegion>();
		MapRegion[][] tmp = new MapRegion[3][3];
		
		if(shiftX < 0){
			for(int x = 0; x < 3; x++){
				for(int y = 0; y < 3; y++){
					shiftRegion(x, y, unloadRegions, tmp);
				}
			}
		}else if(shiftX > 0){
			for(int x = 2; x >= 0; x--){
				for(int y = 2; y >= 0; y--){
					shiftRegion(x, y, unloadRegions, tmp);
				}
			}
		}else if(shiftY < 0){
			for(int x = 0; x < 3; x++){
				for(int y = 0; y < 3; y++){
					shiftRegion(x, y, unloadRegions, tmp);
				}
			}
		}else if(shiftY > 0){
			for(int x = 2; x >= 0; x--){
				for(int y = 2; y >= 0; y--){
					shiftRegion(x, y, unloadRegions, tmp);
				}
			}
		}
		
		//empty check
		boolean isEmpty = true;
		for(int x = 0; x < 3; x++){
			for(int y = 0; y < 3; y++){
				if(tmp[x][y] != null){
					isEmpty = false;
				}
			}
		}
		
		if(isEmpty){
//			Gdx.app.log("Streamer: ", "DONT SHIFT"); 
			return;
		}
		
		//copy tmp to actual map
		map.activeRegions = tmp;
		
//		Gdx.app.log("Streamer: ", "OLD Cam region:"+ camRegion.x +", "+camRegion.y);
		
		//write unloaded regions to file
		writeUnloadedRegions(unloadRegions);
		
		JsonValue c;
		//load connected regions
		for(int x = 0; x < 3; x++){
			for(int y = 0; y < 3; y++){
				if(map.getRegionMap()[x][y] == null) continue;
				
				MapRegion r = map.getRegionMap()[x][y];
				
				for(String d: r.getConnections().keys()){
					switch(d){
					case "n":
						if(y + 1 < map.getRegionMap()[x].length){
							if(map.getRegionMap()[x][y + 1] != null) continue;
//							Gdx.app.log("Streamer: ", "loading NORTH");
							
							c = j.fromJson(null, dir.child(r.getConnections().get(d).toString()));
							loadRegion(c, x, y + 1);
							
							MapRegion n = map.getRegionMap()[x][y + 1];
							
							if(x + 1 < map.getRegionMap().length){
								if(map.getRegionMap()[x + 1][y + 1] != null || !n.getConnections().containsKey("e")) continue;
								
								c = j.fromJson(null, dir.child(n.getConnections().get("e").toString()));
								loadRegion(c, x + 1, y + 1);
							}
							if(x - 1 > 0){
								if(map.getRegionMap()[x - 1][y + 1] != null || !n.getConnections().containsKey("w")) continue;
								
								c = j.fromJson(null, dir.child(n.getConnections().get("w").toString()));
								loadRegion(c, x - 1, y + 1);
							}
						}
					break;
					case "s":
						if(y - 1 >= 0){
							if(map.getRegionMap()[x][y - 1] != null) continue;
//							Gdx.app.log("Streamer: ", "loading SOUTH"); 
							
							c = j.fromJson(null, dir.child(r.getConnections().get(d).toString()));
							loadRegion(c, x, y - 1);
							
							MapRegion n = map.getRegionMap()[x][y - 1];
							
							if(x + 1 < map.getRegionMap().length){
								if(map.getRegionMap()[x + 1][y - 1] != null || !n.getConnections().containsKey("e")) continue;
								
								c = j.fromJson(null, dir.child(n.getConnections().get("e").toString()));
								loadRegion(c, x + 1, y - 1);
							}
							if(x - 1 > 0){
								if(map.getRegionMap()[x - 1][y - 1] != null || !n.getConnections().containsKey("w")) continue;
								
								c = j.fromJson(null, dir.child(n.getConnections().get("w").toString()));
								loadRegion(c, x - 1, y - 1);
							}
						}
					break;
					case "e":
						if(x + 1 < map.getRegionMap().length){
							if(map.getRegionMap()[x + 1][y] != null) continue;
//							Gdx.app.log("Streamer: ", "loading EAST"); 
							
							c = j.fromJson(null, dir.child(r.getConnections().get(d).toString()));
							loadRegion(c, x + 1, y);
							
							MapRegion n = map.getRegionMap()[x + 1][y];
							
							if(y + 1 < map.getRegionMap()[x].length){
								if(map.getRegionMap()[x + 1][y + 1] != null || !n.getConnections().containsKey("n")) continue;
								
								c = j.fromJson(null, dir.child(n.getConnections().get("n").toString()));
								loadRegion(c, x + 1, y + 1);
							}
							if(y - 1 > 0){
								if(map.getRegionMap()[x + 1][y - 1] != null || !n.getConnections().containsKey("s")) continue;
								
								c = j.fromJson(null, dir.child(n.getConnections().get("s").toString()));
								loadRegion(c, x + 1, y - 1);
							}
						}
					break;
					case "w":
						if(x - 1 >= 0){
							if(map.getRegionMap()[x - 1][y] != null) continue;
//							Gdx.app.log("Streamer: ", "loading WEST"); 
							
							c = j.fromJson(null, dir.child(r.getConnections().get(d).toString()));
							loadRegion(c, x - 1, y);
							
							MapRegion n = map.getRegionMap()[x - 1][y];
							
							if(y + 1 < map.getRegionMap()[x].length){
								if(map.getRegionMap()[x - 1][y + 1] != null || !n.getConnections().containsKey("n")) continue;
								
								c = j.fromJson(null, dir.child(n.getConnections().get("n").toString()));
								loadRegion(c, x - 1, y + 1);
							}
							if(y - 1 > 0){
								if(map.getRegionMap()[x - 1][y - 1] != null || !n.getConnections().containsKey("s")) continue;
								
								c = j.fromJson(null, dir.child(n.getConnections().get("s").toString()));
								loadRegion(c, x - 1, y - 1);
							}
						}
					break;
					}
				}
			}
		}
		
		repositionCamera(cam);
		
//		System.out.println("MapStream TIME: " + TimeUtils.timeSinceNanos(t));
	}
	private void shiftRegion(int x, int y, Array<MapRegion> unloadRegions, MapRegion[][] tmp){
		if(map.getRegionMap()[x][y] == null) return;
		
		if(x + shiftX < 0 || x + shiftX > 2 || y + shiftY < 0 || y + shiftY > 2){
			unloadRegions.add(map.getRegionMap()[x][y]);
//			Gdx.app.log("Streamer: ", "Region to UNLOAD: "+ map.getRegionMap()[x][y].getId());
			return;
		}
			
		tmp[x + shiftX][y + shiftY] = map.getRegionMap()[x][y];
//		Gdx.app.log("Streamer: ", "Map region: "+ x +", "+ y + ". to : "+ (x + shiftX) +", "+ (y + shiftY) + ". ID: "+ map.getRegionMap()[x][y].getId());
	}
	private void repositionCamera(OrthographicCamera cam){
		Vector2 camRegion = IsoUtils.getColRow((int) cam.position.x, (int) cam.position.y, Map.tileWidth, Map.tileHeight);
		Vector2 camChunk = Map.worldVecToChunk((int) camRegion.x, (int) camRegion.y);
		Vector2 camTile = Map.worldVecToTile((int) camRegion.x, (int) camRegion.y);
		
		camRegion = Map.tileToWorld((int) camTile.x, (int) camTile.y, (int) camChunk.x, (int) camChunk.y, 1, 1);
		
		Vector2 tmp = IsoUtils.getIsoXY((int) camRegion.x, (int) camRegion.y, map.getTileWidth(), map.getTileHeight());
		cam.position.x = tmp.x;
		cam.position.y = tmp.y;
		
//		Gdx.app.log("Streamer: ", "CAM RE-POS");
	}
	private void writeUnloadedRegions(Array<MapRegion> regions){
		if(regions.size == 0) return;
		if(saveDir == null){
			dir = Gdx.files.external("adoGame/editor/maps/tmp");
			if(!dir.exists()){
				dir.mkdirs();
			}
		}else{
			dir = saveDir;
		}
		
		//write regions to be unloaded
		for(MapRegion r: regions){
//			Gdx.app.log("Streamer: ", "writing region: "+ r.getId() + "...to file");

			try {
				j.setWriter(new FileWriter(dir.file().getPath() + 
						"/" + r.getId()));
			} catch (IOException e) {
				e.printStackTrace();
			}

			saveRegion(r, j);
		}
	}
	
}
