package com.ado.trader.map;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class TileLayer implements Layer{
	public Tile[][] map;
	
	public TileLayer(int w, int h){
		map = new Tile[w][h];
	}
	public void loadLayer(JsonValue tiles, Map map){
		String[] xy;
		for(JsonValue t = tiles.child; t != null; t = t.next){
			xy = t.get("p").asString().split(",");
			this.map[Integer.valueOf(xy[0])][Integer.valueOf(xy[1])] = map.getTilePool().createTile(t.getInt("id"));
		}
	}
	public void saveLayer(Json chunkJson){
		chunkJson.writeArrayStart("tiles");
		
		for(int x=0; x<map.length; x++){
			for(int y=0; y<map[x].length; y++){
		
				Tile t = map[x][y];
				if(t == null){
					continue;
				}
				
				chunkJson.writeObjectStart();
				chunkJson.writeValue("p", x + "," + y);
				chunkJson.writeValue("id", t.id);
				
				if(t.mask != null){
					chunkJson.writeValue("m", t.mask.overlay + "," + t.mask.tileID);
				}
				
				chunkJson.writeObjectEnd();
			}
		}
		
		chunkJson.writeArrayEnd();
	}
	@Override
	public boolean isOccupied(int x, int y) {
		return false;
	}

	@Override
	public void deleteFromMap(int x, int y) {
	}
	public int getWidth() {
		return map.length;
	}
	public int getHeight() {
		return map[0].length;
	}
}
