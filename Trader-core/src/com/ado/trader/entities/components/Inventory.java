package com.ado.trader.entities.components;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;

public class Inventory extends Component {
	Array<Integer> items;
	public int max;

	public Inventory() {
	}
	public void init(int max) {
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
}
