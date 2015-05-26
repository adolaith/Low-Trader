package com.ado.trader.entities.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

public class Position extends Component {
	int mapX, mapY;
	Vector2 isoOffset;
	
	public Position(){
		isoOffset = new Vector2();
	}
	
	public void setPosition(int mapX, int mapY){
		this.mapX = mapX;
		this.mapY = mapY;
	}
	public int getMapX() {
		return mapX;
	}
	public int getMapY() {
		return mapY;
	}
	public Vector2 getIsoOffset() {
		return isoOffset;
	}
}
