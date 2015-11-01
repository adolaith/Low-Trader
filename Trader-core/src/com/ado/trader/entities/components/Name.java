package com.ado.trader.entities.components;

import com.artemis.Component;

public class Name extends Component {
	String value;

	public Name(String name) {
		this.value = name;
	}
	public Name(){		
	}
	public void setName(String name){
		this.value = name;
	}
	public String getName(){
		return value;
	}
}
