package com.ado.trader.entities.components;

import com.ado.trader.utils.IsoUtils;
import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

public class Position extends Component {
	int x,y;
	public int layer;
	Vector2 isoPosition;
	int width, height;
	
	public Position(int width, int height){
		this.width =width;
		this.height=height;
		isoPosition = new Vector2();
	}
	public void setPosition(int x, int y, int layer){
		this.x =x;
		this.y =y;
		this.layer = layer;
		isoPosition = IsoUtils.getIsoXY(x, y, width, height);
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public Vector2 getIsoPosition() {
		return isoPosition;
	}
}
