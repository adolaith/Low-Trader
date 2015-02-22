package com.ado.trader.items;

public class ItemValue extends ItemData {
	int value;
	
	public ItemValue(int value){
		super("Value");
		this.value = value;
	}
	public ItemValue(int value, String name){
		super(name);
		this.value = value;
	}
	public void setValue(int value){
		this.value = value;
	}
	public int getValue(){
		return value;
	}
}
