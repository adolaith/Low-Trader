package com.ado.trader.items;

public class FoodItem extends ItemData {
	int value;
	
	public FoodItem(int value){
		super("Food");
		this.value = value;
	}
	public void setValue(int value){
		this.value = value;
	}
	public int getValue(){
		return value;
	}
}
