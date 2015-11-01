package com.ado.trader.entities.components;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

public class Inventory extends Component implements Serializable{
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
	public void write(Json json) {
		
	}
	@Override
	public void read(Json json, JsonValue jsonData) {
		
	}
}
