package com.ado.trader.entities.components;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/*FORMAT: 00.000000
 * First unit = Type code
 * 		00 = default entity
 * 		01 = default item/consumable
 * 		02 - 99 = Additional 
 * 
 * Second unit = unique id from the last 6 digits of nanoTime
 */
public class BaseId extends SerializableComponent {
	String id;

	public BaseId() {
		
	}
	
	public BaseId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void save(Json writer) {
		writer.writeValue("baseid", id);
	}

	@Override
	public void load(JsonValue data) {
		this.id = data.asString();
	}
}
