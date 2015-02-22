package com.ado.trader.entities.components;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;

public class AttributeTable extends Component {
	Array<Attribute> attributes;

	public AttributeTable() {
		attributes = new Array<Attribute>();
	}
	public void addAttribute(Attribute a){
		attributes.add(a);
	}
	public Attribute getAttribute(String name){
		for(Attribute a: attributes){
			if(a.getName().matches(name)){
				return a;
			}
		}
		return null;
	}
	public void removeAttribute(String name){
		for(Attribute a: attributes){
			if(a.getName().matches(name)){
				attributes.removeValue(a, true);
			}
		}
	}
}
