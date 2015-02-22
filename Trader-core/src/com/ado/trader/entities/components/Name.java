package com.ado.trader.entities.components;

import com.artemis.Component;

public class Name extends Component {
	String name;

	public Name(String name) {
		this.name = name;
	}
	public Name(){		
	}
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
}
