package com.ado.trader.entities.components;

import com.artemis.Component;

public class Type extends Component {
	int typeID;

	public Type() {
	}
	public Type(int typeID) {
		this.typeID = typeID;
	}
	//Entity type
	public int getTypeID() {
		return typeID;
	}
	public void setTypeID(int typeID) {
		this.typeID = typeID;
	}
}
