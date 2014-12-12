package com.ado.trader.utils.placement;

import com.ado.trader.screens.GameScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

abstract class Placeable {
	public boolean delete;
	GameScreen game;
	
	protected Placeable(GameScreen game){
		this.game = game;
	}
	                                                                                                                                                  
	abstract void place(int x, int y);
	abstract void dragPlace(Vector2 start, Vector2 widthHeight);
	abstract void remove(int x, int y);
	abstract void renderPreview(SpriteBatch batch);

}
