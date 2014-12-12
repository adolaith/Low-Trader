package com.ado.trader.items;

public class ItemValue extends ItemData {
	
	public ItemValue(int value){
		super("Value");
		this.value = value;
	}
	public ItemValue(int value, String name){
		super(name);
		this.value = value;
	}
}
