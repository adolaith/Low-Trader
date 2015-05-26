package com.ado.trader.placement;

import com.ado.trader.entities.components.Inventory;
import com.ado.trader.items.ItemFactory;
import com.ado.trader.map.Chunk;
import com.ado.trader.map.Map;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

@Wire
public class ItemPlaceable extends Placeable {
	ComponentMapper<Inventory> invenMap;
	String itemId;
	
	public ItemPlaceable(Map map) {
		super(map, null);
	}

	@Override
	void place(int mapX, int mapY) {
		Entity i = ItemFactory.createItem(itemId);
		
		Gdx.app.log("itemPlace: ", "mapVec: "+ mapX +", "+ mapY);
		
		Chunk c = map.getChunk(mapX, mapY);
		Gdx.app.log("itemPlace: ", "=========");
		Vector2 t = map.worldVecToTile(mapX, mapY);
		
		Gdx.app.log("itemPlace: ", "chunk: "+c);
		Gdx.app.log("itemPlace: ", "tileVec: "+t);
		Gdx.app.log("itemPlace: ", "entities: "+c.getEntities());
		
		if(c.getEntities().map[(int) t.x][(int) t.y][0] != null){
			Entity e = map.getWorld().getEntity(c.getEntities().map[(int) t.x][(int) t.y][0]);
			
			if(invenMap.has(e)){
				Inventory inventory = invenMap.get(e);
				inventory.add(i.id);
				return;
			}
		}
		if(!c.getItems().addToMap(i.id, (int) t.x, (int) t.y)){
			Gdx.app.log("ItemPlaceable: ", "Tile is full and cannot hold any more items");
		}
	}

	public void renderPreview(SpriteBatch batch) {
	}
	
	@Override
	void rotateSelection() {}

	@Override
	void dragPlace(Vector2 start, Vector2 widthHeight) {
	}

	@Override
	void clearSettings() {
	}
}
