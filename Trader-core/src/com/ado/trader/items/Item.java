package com.ado.trader.items;

import com.badlogic.gdx.utils.Array;

public class Item {
	String name;
	Array<ItemData> data;
	
	public Item(String name){
		this.name = name;
		data = new Array<ItemData>();
	}
	@SuppressWarnings("unchecked")
	public <T extends ItemData> T getData(Class<? extends ItemData> type){
		ItemData d = null;
		for(ItemData i: data){ 
			if(type.isInstance(i)){
				d = i;
			}
		}
		return (T) d;
	}
	public boolean hasDataType(Class<? extends ItemData> type){
		for(ItemData i: data){
			if(type.isInstance(i)){
				return true;
			}
		}
		return false;
	}
	public Array<ItemData> getAllData(){
		return data;
	}
	public void addData(ItemData d){
		data.add(d);
	}
	public String getId(){
		return name;
	}
}
