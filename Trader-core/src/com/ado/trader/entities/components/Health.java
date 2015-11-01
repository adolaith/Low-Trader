package com.ado.trader.entities.components;

import com.artemis.Component;

public class Health extends Component {
	public int current, max;

	public Health() {
	}
	
	public Health(int max){
		this.max = max;
		current = max;
	}

}
