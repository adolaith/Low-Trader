package com.ado.trader.items;

public class ToolItem extends ItemData {
	int value;
	
	public ToolItem(int value){
		super("tool");
		this.value = value;
	}
	public void setValue(int value){
		this.value = value;
	}
	public int getValue(){
		return value;
	}
}
