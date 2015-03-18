package com.ado.trader.entities.components;

import com.ado.trader.utils.IsoUtils;
import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

public class Position extends Component {
	int x,y, h;
	Vector2 isoPosition;
	int width, height;
	
	public Position(){
		
	}
	public Position(int width, int height){
		this.width =width;
		this.height=height;
		isoPosition = new Vector2();
	}
	public void setPosition(int x, int y, int h){
		this.x =x;
		this.y =y;
		this.h = h;
		isoPosition = IsoUtils.getIsoXY(x, y, width, height);
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getHeightLayer(){
		return h;
	}
	public Vector2 getIsoPosition() {
		return isoPosition;
	}
}
