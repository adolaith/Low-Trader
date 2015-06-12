package com.ado.trader.entities.components;

import com.artemis.Component;

public class Health extends Component {
	int current, max;

	public Health() {
	}
	
	public Health(int max){
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

}
