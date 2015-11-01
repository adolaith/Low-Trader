package com.ado.trader.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class MapStreamer {
	Map map;
	
	Json j;
	FileHandle saveDir;
	
	public MapStreamer(Map map) {
		this.map = map;
		this.j = new Json();

		JsonValue cfg = j.fromJson(null, Gdx.files.internal("data/entities/classTags.lst"));
		
		//load entity component class tags
		for(JsonValue v = cfg.child; v != null; v = v.next){
			try {
				j.addClassTag(v.name, Class.forName(v.asString()));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
	}

	public void streamMap(){
		
	}
	
	public MapRegion loadRegion(JsonValue region, int rX, int rY){
		MapRegion r = new MapRegion();
		
		r.setId(region.getInt("id"));
		map.getRegionMap()[rX][rY] = r;
		
		for(JsonValue c = region.get("conn").child; c != null; c = c.next()){
			r.addConnectedRegion(c.name, c.asInt());
		}
		
		JsonValue chunks = region.get("chunks");
		for(JsonValue v = chunks.child; v != null; v = v.next){
			Chunk c = new Chunk(map.world);
			
			String[] xy = v.name.split(":");
			r.setChunk(Integer.valueOf(xy[0]), Integer.valueOf(xy[1]), c);
			
			//tiles now
			c.getTiles().loadLayer(v.get("tiles"), map);
			
			//walls
//			c.getWalls().loadLayer(v.get("walls"));
			
			//items
//			c.getItems().loadLayer(v.get("items"), map);
			
			//entities
//			c.getEntities().loadLayer(v.get("entities"));
			
		}
		
		return r;
	}
	//Json writer writes to tmp dir or straight to .zip
	public void saveRegion(MapRegion r, Json writer){
		writer.writeObjectStart();
		
		writer.writeValue("id", r.getId());
		
		writeConnections(r, writer);
		
		checkChunkAmt(r);
		
		checkBorderChunks(r, writer);
		
		saveChunks(r, writer);
		
		writer.writeObjectEnd();
		try{
			writer.getWriter().close();
		}catch(Exception ex){
			System.out.println("Error saving region:" +ex);
		}
	}
	
	private void writeConnections(MapRegion r, Json writer){
		writer.writeObjectStart("conn");
		
		for(String dir: r.getConnections().keys()){
			writer.writeValue(dir, r.getConnections().get(dir));
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
		//check if region has only 1 chunk(in the centre)
		for(int j = 0; j < 3; j++){
			for(int k = 0; k < 3; k++){
				if(r.isOccupied(j, k)){
					if(!(j == 1 && k == 1)){
						return;
					}
				}
			}
		}
		r.setChunk(1, 0, r.getChunk(1, 1));
		r.setChunk(1, 1, null);
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
	public void setSaveDir(FileHandle dir){
		this.saveDir = dir;
	}
}
