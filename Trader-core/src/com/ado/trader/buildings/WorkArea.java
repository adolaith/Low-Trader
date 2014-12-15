package com.ado.trader.buildings;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class WorkArea {
	public Vector2 vec;
	public Array<Vector2> area;
	public Integer entityId;
	public Array<Integer> allEntities;
	public String aiWorkProfile;
	
	public WorkArea(Vector2 vec, String aiProfile){
		this.vec = vec;
		this.entityId = null;
		this.aiWorkProfile = aiProfile;
	}
	public WorkArea(Array<Vector2> area, String aiProfile){
		this.area = area;
		this.aiWorkProfile = aiProfile;
		this.allEntities = new Array<Integer>();
	}

}
