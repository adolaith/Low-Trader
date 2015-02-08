package com.ado.trader.placement;

import com.ado.trader.items.Item;
import com.ado.trader.items.ItemFactory;
import com.ado.trader.items.ItemPosition;
import com.ado.trader.map.ItemLayer;
import com.ado.trader.map.Map;
import com.ado.trader.rendering.EntityRenderSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class ItemPlaceable extends Placeable {
	String itemId;
	
	public ItemPlaceable(Map map) {
		super(map);
	}

	public void place(int x, int y) {
		ItemLayer itemLayer = map.getItemLayer();
		if(itemLayer.isOccupied(x, y, map.currentLayer)){
			// Invalid placement
			return;
		}
		Item i = ItemFactory.createItem(itemId);
		itemLayer.addToMap(i, x, y, map.currentLayer);
		ItemPosition p = i.getData(ItemPosition.class);
		p.position.set(x, y, map.currentLayer);
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
	void rotateSelection(EntityRenderSystem entityRenderer) {}
}
