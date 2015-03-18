package com.ado.trader.placement;

import com.ado.trader.entities.components.Position;
import com.ado.trader.items.ItemFactory;
import com.ado.trader.map.ItemLayer;
import com.ado.trader.map.Map;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

@Wire
public class ItemPlaceable extends Placeable {
	ComponentMapper<Position> positionMapper;
	String itemId;
	
	public ItemPlaceable(Map map) {
		super(map, null);
	}

	public void place(int x, int y) {
		ItemLayer itemLayer = map.getItemLayer();
		if(itemLayer.isOccupied(x, y, map.currentLayer)){
			// Invalid placement
			return;
		}
		Entity i = ItemFactory.createItem(itemId);
		itemLayer.addToMap(i, x, y, map.currentLayer);
		positionMapper.get(i).setPosition(x, y, map.currentLayer);
	}

	@Override
	public void dragPlace(Vector2 start, Vector2 widthHeight) {}

	public void remove(int x, int y) {
		ItemLayer itemLayer = map.getItemLayer();
		if(itemLayer.isOccupied(x, y, map.currentLayer)){
			itemLayer.deleteFromMap(x, y, map.currentLayer);
		}
	}
	public void renderPreview(SpriteBatch batch) {
		
	}

	@Override
	void rotateSelection() {}
}
