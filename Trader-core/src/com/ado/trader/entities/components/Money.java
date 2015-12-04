package com.ado.trader.entities.components;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Money extends SerializableComponent{
	public int value;

	public Money() {
	}

	@Override
	public void save(Json writer) {
		writer.writeValue("money", value);
	}

	@Override
	public void load(JsonValue data) {
		value = data.asInt();
	}
}
