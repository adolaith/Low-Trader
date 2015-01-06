package com.ado.trader.items;

import com.badlogic.gdx.math.Vector3;

public class ItemPosition extends ItemData {
	public Vector3 position;

	public ItemPosition() {
		super("Position");
		position = new Vector3();
	}
}
