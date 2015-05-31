package com.ado.trader.map;

import java.io.FileWriter;
import java.io.IOException;

import com.ado.trader.items.ItemFactory;
import com.ado.trader.utils.IsoUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Json;

public class EditorStreamer extends MapStreamer {
	Json j;
	FileHandle dir;

	public EditorStreamer(Map map, ItemFactory items) {
		super(map, items);
		j = new Json();
		FileHandle tmpDir = Gdx.files.external("adoGame/maps/tmp");
		if(tmpDir.exists()){
			tmpDir.deleteDirectory();
		}
	}
	public void streamMap(OrthographicCamera cam){
		Vector2 camRegion = IsoUtils.getColRow((int) cam.position.x, (int) cam.position.y, Map.tileWidth, Map.tileHeight);
		camRegion = Map.worldVecToRegion((int) camRegion.x, (int) camRegion.y);
		
		if(camRegion.x == 1 && camRegion.y == 1) return;
		
		int shiftX = 1 - (int) camRegion.x;
		int shiftY = 1 - (int) camRegion.y;
		Gdx.app.log("Streamer: ", "REGION SHIFT XY: "+ shiftX +", "+ shiftY);
		
		for(int x = 0; x < 3; x++){
			for(int y = 0; y < 3; y++){
				if(map.getRegionMap()[x][y] == null) continue;
				
				Gdx.app.log("Streamer: ", "Map regions: "+ x +", "+ y + " = "+ map.getRegionMap()[x][y] + ". ID: "+ map.getRegionMap()[x][y].getId());
			}
		}
		
		//dont unload the last region
		if(isLastRegion(shiftX, shiftY)) return;
		
		Gdx.app.log("Streamer: ", "Cam region:"+ camRegion.x +", "+camRegion.y);
		
		writeUnloadedRegions(shiftX, shiftY);
		
		//shift regions
		IntArray shiftedRegions = new IntArray();
		if(shiftX < 0 || shiftY < 0){
			for(int x = 0; x < 3; x++){
				for(int y = 0; y < 3; y++){
					if(x + shiftX < 0 || x + shiftX > 2 || y + shiftY < 0 || y + shiftY > 2) continue;
					if(map.getRegionMap()[x][y] == null) continue;
					if(shiftedRegions.contains(map.getRegionMap()[x][y].getId())) continue;
					
					Gdx.app.log("Streamer: ", "Shift region: "+ x +", "+ y +"...to..."+ (x + shiftX) +", "+ (y + shiftY));
					
					map.getRegionMap()[x + shiftX][y + shiftY] = map.getRegionMap()[x][y];
					shiftedRegions.add(map.getRegionMap()[x + shiftX][y + shiftY].getId());
					map.getRegionMap()[x][y] = null;
				}
			}
		}else if(shiftX > 0 || shiftY > 0){
			for(int x = 2; x >= 0; x--){
				for(int y = 2; y >= 0; y--){
					if(x + shiftX < 0 || x + shiftX > 2 || y + shiftY < 0 || y + shiftY > 2) continue;
					if(map.getRegionMap()[x][y] == null) continue;
					if(shiftedRegions.contains(map.getRegionMap()[x][y].getId())) continue;
					
					Gdx.app.log("Streamer: ", "Shift region: "+ x +", "+ y +"...to..."+ (x + shiftX) +", "+ (y + shiftY));
					
					map.getRegionMap()[x + shiftX][y + shiftY] = map.getRegionMap()[x][y];
					shiftedRegions.add(map.getRegionMap()[x + shiftX][y + shiftY].getId());
					map.getRegionMap()[x][y] = null;
				}
			}
		}
		
		//load connected regions
		for(int x = 0; x < 3; x++){
			for(int y = 0; y < 3; y++){
				if(map.getRegionMap()[x][y] == null) continue;
				
				MapRegion r = map.getRegionMap()[x][y];
				
				for(String d: r.getConnections().keys()){
					switch(d){
					case "n":
						if(y + 1 < map.getRegionMap()[x].length){
							writeRegion(x, y + 1, d, r);
						}
					break;
					case "s":
						if(y - 1 >= 0){
							writeRegion(x, y - 1, d, r);
						}
					break;
					case "e":
						if(x + 1 < map.getRegionMap().length){
							writeRegion(x + 1, y, d, r);
						}
					break;
					case "w":
						if(x - 1 >= 0){
							writeRegion(x - 1, y, d, r);
						}
					break;
					}
				}
			}
		}
		
		repositionCamera(cam);
	}
	private void repositionCamera(OrthographicCamera cam){
		Vector2 camRegion = IsoUtils.getColRow((int) cam.position.x, (int) cam.position.y, Map.tileWidth, Map.tileHeight);
		Vector2 camChunk = Map.worldVecToChunk((int) camRegion.x, (int) camRegion.y);
		Vector2 camTile = Map.worldVecToTile((int) camRegion.x, (int) camRegion.y);
		
		camRegion = Map.tileToWorld((int) camTile.x, (int) camTile.y, (int) camChunk.x, (int) camChunk.y, 1, 1);
		
		Vector2 tmp = IsoUtils.getIsoXY((int) camRegion.x, (int) camRegion.y, map.getTileWidth(), map.getTileHeight());
		cam.position.x = tmp.x;
		cam.position.y = tmp.y;
	}
	private void writeRegion(int x, int y, String d, MapRegion r){
		if(map.getRegionMap()[x][y] == null){
			try {
				j.setWriter(new FileWriter(dir.file().getPath() + 
						"/" + r.getConnections().get(d)));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			saveRegion(x, y, j);
		}
	}
	private void writeUnloadedRegions(int shiftX, int shiftY){
		if(saveDir == null){
			dir = Gdx.files.external("adoGame/maps/tmp");
			if(!dir.exists()){
				dir.mkdirs();
			}
		}else{
			dir = saveDir;
		}
		
		//write regions to be unloaded
		for(int x = 0; x < 3; x++){
			for(int y = 0; y < 3; y++){
				if(x + shiftX < 0 || x + shiftX > 2 || y + shiftY < 0 || y + shiftY > 2){
					if(map.getRegionMap()[x][y] == null) continue;
					Gdx.app.log("Streamer: ", "writing region: "+ x +", "+ y + "...to file");

					try {
						j.setWriter(new FileWriter(dir.file().getPath() + 
								"/" + map.getRegionMap()[x][y].getId()));
					} catch (IOException e) {
						e.printStackTrace();
					}

					saveRegion(x, y, j);
				}
			}	
		}
	}
	
	private boolean isLastRegion(int shiftX, int shiftY){
		int regionCount = 0;
		for(int x = 0; x < 3; x++){
			for(int y = 0; y < 3; y++){
				if(map.getRegionMap()[x][y] != null){
					regionCount++;
				}
			}
		}
		
		if(regionCount == 1){
			for(int x = 0; x < 3; x++){
				for(int y = 0; y < 3; y++){
					if(map.getRegionMap()[x][y] != null){
						Gdx.app.log("Streamer: ", "LAST REGION!");
						if(x + shiftX < 0 || x + shiftX > 2 || y + shiftY < 0 || y + shiftY > 2){
//						if(x == 0 || x == 2 || y == 0 || y == 2){
							Gdx.app.log("Streamer: ", "DONT SHIFT REGIONS!");
							return true;
						}
					}
				}
			}
		}
		
		MapRegion[][] tmp = new MapRegion[3][3];
		IntArray shiftedRegions = new IntArray();
		if(shiftX < 0){
			for(int x = 0; x < 3; x++){
				for(int y = 0; y < 3; y++){
					if(x + shiftX < 0 || x + shiftX > 2 || y + shiftY < 0 || y + shiftY > 2) continue;
					if(map.getRegionMap()[x][y] == null) continue;
					if(shiftedRegions.contains(map.getRegionMap()[x][y].getId())) continue;

					tmp[x + shiftX][y + shiftY] = map.getRegionMap()[x][y];
					shiftedRegions.add(tmp[x + shiftX][y + shiftY].getId());
				}
			}
		}else if(shiftX > 0){
			for(int x = 2; x >= 0; x--){
				for(int y = 2; y >= 0; y--){
					if(x + shiftX < 0 || x + shiftX > 2 || y + shiftY < 0 || y + shiftY > 2) continue;
					if(map.getRegionMap()[x][y] == null) continue;
					if(shiftedRegions.contains(map.getRegionMap()[x][y].getId())) continue;

					tmp[x + shiftX][y + shiftY] = map.getRegionMap()[x][y];
					shiftedRegions.add(tmp[x + shiftX][y + shiftY].getId());
				}
			}
		}
		
		for(int x = 0; x < 3; x++){
			for(int y = 0; y < 3; y++){
				if(tmp[x][y] != null){
					Gdx.app.log("Streamer: ", "DONT SHIFT REGIONS!");
					return true;
				}
			}
		}
		
		return false;
	}
}
