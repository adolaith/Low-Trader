package com.ado.trader.placement;

import com.ado.trader.map.Map;
import com.ado.trader.rendering.EntityRenderSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

abstract class Placeable {
	public boolean delete;
	Map map;
	EntityRenderSystem entityRenderer;

	protected Placeable(Map map, EntityRenderSystem entityRenderer){
		this.map = map;
		this.entityRenderer = entityRenderer;
	}
	                                                                                                                                                  
	abstract void place(int x, int y);
	abstract void dragPlace(Vector2 start, Vector2 widthHeight);
	abstract void remove(int x, int y);
	abstract void rotateSelection();
	abstract void renderPreview(SpriteBatch batch);

}
