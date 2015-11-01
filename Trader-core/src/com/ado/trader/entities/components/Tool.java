package com.ado.trader.entities.components;

import com.artemis.Component;

public class Tool extends Component {
	public int current, max;

	public Tool() {
	}
	public void init(int max){
		current = max;
		this.max = max;
	}

}
