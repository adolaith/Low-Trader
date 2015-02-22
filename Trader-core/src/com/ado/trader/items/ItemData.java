package com.ado.trader.items;

public class ItemData {
	String name;
	
	protected ItemData(String componentName){
		name = componentName;
	}
	public String getComponentName(){
		return name;
	}
}
