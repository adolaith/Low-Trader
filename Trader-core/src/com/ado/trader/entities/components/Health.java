package com.ado.trader.entities.components;

import com.artemis.Component;

public class Health extends Component {
	public int value, max;

	public Health() {
	}
	public void setMax(int i){
		value = i;
		max = i;
	}
	public void loadValues(int value, int max){
		this.value = value;
		this.max = max;
	}
}
