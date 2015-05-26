package com.ado.trader.map;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.entities.components.Wall;
import com.ado.trader.items.ItemFactory;
import com.ado.trader.rendering.EntityRenderSystem.Direction;
import com.ado.trader.systems.GameTime;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class MapStreamer {
	Map map;
	ItemFactory items;
	
	public MapStreamer(Map map, ItemFactory items) {
		this.map = map;
		this.items = items;
	}
	public void loadMap(String dirName){
		
//		if(data.first().containsKey("ToD")){
//			ArrayMap<String, String> timeData = data.removeIndex(0);
//			GameTime time = map.getWorld().getSystem(GameTime.class);
//			time.loadSettings(Integer.valueOf(timeData.get("days")), GameTime.Time.valueOf(timeData.get("ToD")), Integer.valueOf(timeData.get("time")));
//		}
	}
	
	public void saveMap(String dir){
		GameTime time = map.getWorld().getSystem(GameTime.class);
		
//		parser.addElement("ToD", String.valueOf(time.getTimeOfDay()));
//		parser.addElement("time", String.valueOf(time.getTime()));
//		parser.addElement("days", String.valueOf(time.getDays()));
	}
	public MapRegion loadRegion(JsonValue region, int rX, int rY){
		MapRegion r = new MapRegion();
		
		JsonValue chunks = region.get("chunks");
		for(JsonValue v = chunks.child; v != null; v = v.next){
			Chunk c = new Chunk(map.world);
			
			String[] xy = v.name.split(":");
			r.setChunk(Integer.valueOf(xy[0]), Integer.valueOf(xy[1]), c);
			
			//tiles now
			c.getTiles().loadLayer(v.get("tiles"), map);
			
			//walls
			c.getWalls().loadLayer(v.get("walls"), rX, rY, 
					Integer.valueOf(xy[0]).intValue(), Integer.valueOf(xy[1]).intValue());
			
			//items
			c.getItems().loadLayer(v.get("items"), map);
			
			//entities
			c.getEntities().loadLayer(v.get("entities"), rX, rY, 
					Integer.valueOf(xy[0]).intValue(), Integer.valueOf(xy[1]).intValue());
			
		}
		
		return r;
	}
	//Json writer writes to tmp dir or straight to .zip
	public void saveRegion(int x, int y, Json writer){
		MapRegion r = map.activeRegions[x][y];
		
		writer.writeObjectStart();
		
		checkConnections(x, y, writer);
		
//		checkChunkAmt(r);
		
		checkBorderChunks(r, writer);
		
		saveChunks(r, writer);
		
		writer.writeObjectEnd();
		try{
			writer.getWriter().close();
		}catch(Exception ex){
			System.out.println("Error:" +ex);
		}
	}
	
	private void checkConnections(int x, int y, Json writer){
		writer.writeObjectStart("conn");
		
		if(y + 1 < map.activeRegions[x].length){
			if(map.activeRegions[x][y + 1] != null){
				writer.writeValue("n", x +":"+ (y+1));
			}
		}
		if(y - 1 >= 0){
			if(map.activeRegions[x][y - 1] != null){
				writer.writeValue("s", x +":"+ (y-1));
			}
		}
		if(x + 1 < map.activeRegions.length){
			if(map.activeRegions[x + 1][y] != null){
				writer.writeValue("e", (x+1) +":"+ y);
			}
		}
		if(x - 1 >= 0){
			if(map.activeRegions[x - 1][y] != null){
				writer.writeValue("w", (x-1) +":"+ y);
			}
		}
		writer.writeObjectEnd();
	}

	private void checkBorderChunks(MapRegion r, Json writer){
		//list the regions border chunks
		writer.writeArrayStart("openChunks");
		for(int j = 0; j < r.chunks.length; j++){
			for(int k = 0; k < r.chunks[j].length; k++){
				if(r.isOccupied(j, k)){
					writer.writeValue(j +":"+ k);
				}
			}
		}
		writer.writeArrayEnd();
	}
	
	private void checkChunkAmt(MapRegion r){
		//check if region has only 1 chunk
		boolean single = true;
		for(int j = 0; j < r.chunks.length; j++){
			for(int k = 0; k < r.chunks[j].length; k++){
				if(r.isOccupied(j, k)){
					if(j != 1 && k != 1){
						single = false;
					}
				}
			}
		}

		//if only 1 chunk and its centred, shift to outside
		if(single){
			r.setChunk(1, 0, r.getChunk(1, 1));
			r.setChunk(1, 1, null);
		}
	}
	
	private void saveChunks(MapRegion r, Json writer){
		//write chunk data
		writer.writeObjectStart("chunks");
		for(int j = 0; j < r.chunks.length; j++){
			for(int k = 0; k < r.chunks[j].length; k++){
				if(r.isOccupied(j, k)){
					writer.writeObjectStart(j +":"+ k);
					r.getChunk(j, k).saveChunk(writer);
					writer.writeObjectEnd();
				}
			}
		}
		writer.writeObjectEnd();
	}
	
}
