package com.ado.trader.entities.components;

import com.ado.trader.items.Item;
import com.artemis.Component;
import com.badlogic.gdx.utils.Array;

public class Inventory extends Component {
	Array<Item> items;
	public int max;

	public Inventory() {
		items = new Array<Item>();
	}
	public Inventory(int i) {
		items = new Array<Item>();
		max = i;
	}
	public void add(Item i){
		items.add(i);
	}
	public void removeItem(Item i){
		items.removeValue(i, true);
	}
	public Array<Item> getItems(){
		return items;
	}
}
