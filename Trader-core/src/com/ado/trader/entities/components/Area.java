package com.ado.trader.entities.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Area extends SerializableComponent {
	public Array<Vector2> area;
	
	public Area(){
		area = new Array<Vector2>();
	}

	@Override
	public void save(Json writer) {
		writer.writeArrayStart("area");
		for(Vector2 v: area){
			writer.writeArrayStart();
			writer.writeValue(v.x);
			writer.writeValue(v.y);
			writer.writeArrayEnd();
		}
		writer.writeArrayEnd();
	}

	@Override
	public void load(JsonValue data) {
		for(JsonValue v = data.child; v != null; v = v.next){
			String[] vec = v.asStringArray();
			area.add(new Vector2(Float.valueOf(vec[0]), Float.valueOf(vec[1])));
		}
	}
}
