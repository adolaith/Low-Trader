package com.ado.trader.map;

import com.artemis.World;
import com.badlogic.gdx.utils.Json;

public class Chunk {
	int width, height;
	TileLayer tiles;
	WallLayer walls;
	ItemLayer items;
	EntityLayer entities;

	public Chunk(World world) {
		width = 32;
		height = 32;
		tiles = new TileLayer(width, height);
		items = new ItemLayer(width, height, 5, world);
		walls = new WallLayer(width, height, world);
		entities = new EntityLayer(width, height, 4, world);
	}
	public void saveChunk(Json chunkJson){
		tiles.saveLayer(chunkJson);
		walls.saveLayer(chunkJson);
		items.saveLayer(chunkJson);
		entities.saveLayer(chunkJson);
	}
	public TileLayer getTiles() {
		return tiles;
	}
	public WallLayer getWalls() {
		return walls;
	}
	public ItemLayer getItems() {
		return items;
	}
	public EntityLayer getEntities() {
		return entities;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
}
