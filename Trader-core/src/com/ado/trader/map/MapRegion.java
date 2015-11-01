package com.ado.trader.map;

import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;

public class MapRegion {
	ArrayMap<String, Integer> connections;
	Chunk[][] chunks;
	int id;

	public MapRegion(){
		chunks = new Chunk[3][3];
		connections = new ArrayMap<String, Integer>();
	}
	public MapRegion(int id) {
		chunks = new Chunk[3][3];
		this.id = id;
		connections = new ArrayMap<String, Integer>();
	}
	
	public void saveChunk(int chunkX, int chunkY, Json regionJson){
		regionJson.writeObjectStart(chunkX +","+ chunkY);
		
		chunks[chunkX][chunkY].saveChunk(regionJson);
		
		regionJson.writeObjectEnd();
	}
	
	public Chunk getFirstChunk(){
		for(int x = 0; x < getWidth(); x++){
			for(int y = 0; y < getHeight(); y++){
				if(chunks[x][y] != null){
					return chunks[x][y];
				}
			}
		}
		return null;
	}
	public ArrayMap<String, Integer> getConnections(){
		return connections;
	}
	public void addConnectedRegion(String dir, int id){
		connections.put(dir, id);
	}
	public void removeConnectedRegion(String dir){
		connections.removeKey(dir);
	}
	public void removeConnectedRegion(int id){
		connections.removeValue(id, true);
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getWidth(){
		return chunks.length;
	}
	public int getHeight(){
		return chunks[0].length;
	}
	public int getWidthInTiles(){
		return getFirstChunk().getWidth() * getWidth();
	}
	public int getHeightInTiles(){
		return getFirstChunk().getHeight() * getHeight();
	}
	public boolean isOccupied(int x, int y){
		return chunks[x][y] != null;
	}
	public Chunk getChunk(int x, int y){
		return chunks[x][y];
	}
	public void setChunk(int x, int y, Chunk chunk){
		chunks[x][y] = chunk;
	}
}
