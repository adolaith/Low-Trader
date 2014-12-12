package com.ado.trader.items;

import com.badlogic.gdx.math.Vector2;

public class ItemPosition extends ItemData {
	public Vector2 position;

	public ItemPosition() {
		super("Position");
		position = new Vector2();
	}
}
