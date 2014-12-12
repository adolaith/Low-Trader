package com.ado.trader.map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class StoreZone extends WorkZone {

	public StoreZone(int id, Vector2 zone, ZoneType type) {
		super(id, zone, type);
	}

	public StoreZone(int id, Array<Vector2> area, ZoneType type) {
		super(id, area, type);
	}

}
