package com.ado.trader.entities.components;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Name extends SerializableComponent {
	String value;

	public Name(String name) {
		this.value = name;
	}
	public Name(){		
	}
	public void setName(String name){
		this.value = name;
	}
	public String getName(){
		return value;
	}
	@Override
	public void save(Json writer) {
		writer.writeValue("name", value);
	}
	@Override
	public void load(JsonValue data) {
		this.value = data.asString();
	}
}
