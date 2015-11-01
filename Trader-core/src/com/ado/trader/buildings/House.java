package com.ado.trader.buildings;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class House extends Building {
	

	public House(int id) {
		super(id);
	}
	public House(int id, Array<Vector3> tiles) {
		super(id, tiles);
	}
}
