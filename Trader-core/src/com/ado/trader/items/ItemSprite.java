package com.ado.trader.items;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class ItemSprite extends ItemData {
	public Sprite sprite;
	public int id;
	
	public ItemSprite(int id, Sprite sprite){
		super("Sprite");
		this.id = id;
		this.sprite = sprite;
	}
}
