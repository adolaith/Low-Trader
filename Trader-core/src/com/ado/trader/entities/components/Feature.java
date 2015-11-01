package com.ado.trader.entities.components;

import com.artemis.Component;

public class Feature extends Component {
	public int spriteIndex;
	public String featureName;
	
	public Feature(String featureName, int spriteIndex){
		this.featureName = featureName;
		this.spriteIndex = spriteIndex;
	}
}
