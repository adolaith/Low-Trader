package com.ado.trader.entities.components;

public class Attribute {
	String name;
	int current, max;

	public Attribute() {
	}
	
	public Attribute(String name, int max){
		this.name = name;
		this.max = max;
		current = max;
	}
	
	public int getCurrent() {
		return current;
	}
	public void setCurrent(int current) {
		this.current = current;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public String getName() {
		return name;
	}
}
