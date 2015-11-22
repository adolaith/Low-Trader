package com.ado.trader.entities.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Position extends SerializableComponent {
	int tileX, tileY;
	Vector2 isoOffset;
	
	public Position(){
		isoOffset = new Vector2();
	}
	
	public void setPosition(int tileX, int tileY){
		this.tileX = tileX;
		this.tileY = tileY;
	}
	public int getTileX() {
		return tileX;
	}
	public int getTileY() {
		return tileY;
	}
	public Vector2 getIsoOffset() {
		return isoOffset;
	}

	@Override
	public void save(Json writer) {
		writer.writeObjectStart("position");
		
		writer.writeArrayStart("t");
		writer.writeValue(tileX);
		writer.writeValue(tileY);
		writer.writeArrayEnd();
		
		writer.writeArrayStart("i");
		writer.writeValue(isoOffset.x);
		writer.writeValue(isoOffset.y);
		writer.writeArrayEnd();
		
		writer.writeObjectEnd();
	}

	@Override
	public void load(JsonValue data) {
		String[] tile = data.get("t").asStringArray();
		
		this.tileX = Integer.valueOf(tile[0]);
		this.tileY = Integer.valueOf(tile[1]);
		
		String[] iso = data.get("i").asStringArray();
		
		if(this.isoOffset == null){
			this.isoOffset = new Vector2();
		}
		this.isoOffset.x = Float.valueOf(iso[0]);
		this.isoOffset.y = Float.valueOf(iso[1]);
	}
}
