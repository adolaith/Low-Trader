package com.ado.trader.utils.placement;

import com.ado.trader.items.Item;
import com.ado.trader.items.ItemPosition;
import com.ado.trader.map.ItemLayer;
import com.ado.trader.screens.GameScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class ItemPlaceable extends Placeable {
	String itemId;
	
	public ItemPlaceable(GameScreen game) {
		super(game);
	}

	public void place(int x, int y) {
		ItemLayer itemLayer = game.getMap().getCurrentLayerGroup().itemLayer;
		if(itemLayer.isOccupied(x, y)){
			game.getGui().getNewsWindow().newMessage("Invalid placement");
			return;
		}
		Item i = game.getItems().createItem(itemId);
		itemLayer.addToMap(i, x, y);
		ItemPosition p = i.getData(ItemPosition.class);
		p.position.set(x, y);
	}

	@Override
	public void dragPlace(Vector2 start, Vector2 widthHeight) {}

	public void remove(int x, int y) {
		ItemLayer itemLayer = game.getMap().getCurrentLayerGroup().itemLayer;
		if(itemLayer.isOccupied(x, y)){
			itemLayer.deleteFromMap(x, y);
		}
	}
	public void renderPreview(SpriteBatch batch) {
	}
	
}