package com.ado.trader.entities.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

public class Position extends Component {
	int tileX, tileY;
	Vector2 isoOffset;
	
	public Position(){
		isoOffset = new Vector2();
	}
	
	public void setPosition(int tileX, int tileY){
		this.tileX = tileX;
		this.tileY = tileY;
	}
	public int getTileX() {
		return tileX;
	}
	public int getTileY() {
		return tileY;
	}
	public Vector2 getIsoOffset() {
		return isoOffset;
	}
}
