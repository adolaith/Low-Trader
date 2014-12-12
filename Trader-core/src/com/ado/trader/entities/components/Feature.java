package com.ado.trader.entities.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Feature extends Component {
	public int spriteId;
	public Sprite sprite;
	
	public Feature(Sprite sprite, int spriteId){
		this.sprite = sprite;
		this.spriteId = spriteId;
	}
}
