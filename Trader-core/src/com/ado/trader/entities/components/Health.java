package com.ado.trader.entities.components;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Health extends SerializableComponent {
	public int current, max;

	public Health() {
	}
	
	public Health(int max){
		this.max = max;
		current = max;
	}

	@Override
	public void save(Json writer) {
		writer.writeArrayStart("hp");
		writer.writeValue(current);
		writer.writeValue(max);
		writer.writeArrayEnd();
	}

	@Override
	public void load(JsonValue data) {
		String[] list = data.asStringArray();
		if(list.length > 1){
			this.current = Integer.valueOf(list[0]);
			this.max = Integer.valueOf(list[1]);
		}else{
			this.current = Integer.valueOf(list[0]);
			this.max = Integer.valueOf(list[0]);
		}
	}

}
