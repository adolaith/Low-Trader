package com.ado.trader.entities.components;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Inventory extends SerializableComponent{
	Array<Integer> items;
	public int max;

	public Inventory() {
		items = new Array<Integer>();
	}
	public Inventory(int max) {
		items = new Array<Integer>();
		this.max = max;
	}
	public void add(int id){
		items.add(id);
	}
	public void removeItem(int id){
		items.removeValue(id, true);
	}
	public Array<Integer> getItems(){
		return items;
	}
	@Override
	public void save(Json writer) {
		
	}
	@Override
	public void load(JsonValue data) {
		
	}
}
