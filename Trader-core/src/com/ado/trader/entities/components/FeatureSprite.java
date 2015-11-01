package com.ado.trader.entities.components;

import com.artemis.Component;

public class FeatureSprite extends Component {
	public int spriteIndex;
	public String featureName;
	
	public FeatureSprite(String featureName, int spriteIndex){
		this.featureName = featureName;
		this.spriteIndex = spriteIndex;
	}
}
