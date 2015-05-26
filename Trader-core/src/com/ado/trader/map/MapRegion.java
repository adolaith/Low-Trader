package com.ado.trader.map;

import com.badlogic.gdx.utils.Json;

public class MapRegion {
	Chunk[][] chunks;

	public MapRegion() {
		chunks = new Chunk[3][3];
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
