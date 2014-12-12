package com.ado.trader.entities.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Area extends Component {
	public Array<Vector2> area;
	
	public Area(){
		area = new Array<Vector2>();
	}
}
