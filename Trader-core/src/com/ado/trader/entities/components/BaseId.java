package com.ado.trader.entities.components;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

//stores the id of the base entity profile/archetype to extend
public class BaseId extends SerializableComponent {
	String[] id;

	public BaseId() {
		
	}
	
	public BaseId(String[] id) {
		this.id = id;
	}
	
	public String getLocId() {
		return id[0];
	}
	
	public String getId() {
		return id[1];
	}

	public void setId(String[] id) {
		this.id = id;
	}

	@Override
	public void save(Json writer) {
		writer.writeValue("baseid", id[0] + "." + id[1]);
	}

	@Override
	public void load(JsonValue data) {
		this.id = data.asString().split("\\.");
	}
}
