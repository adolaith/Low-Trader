package com.ado.trader.buildings;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Farm extends Building {
	public String itemName;
	public int daysGrowing;
	public float maintenance, growScore;

	public Farm(int id) {
		super(id);
		itemName = "";
	}
	public Farm(int id, Array<Vector3> tiles) {
		super(id, tiles);
		itemName = "";
	}
}
