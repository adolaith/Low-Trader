package com.ado.trader.entities.components;

import com.ado.trader.items.Item;
import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

public class Target extends Component {
	Item i;
	Integer entityId;
	Vector2 target;

	public Target() {
		i = null;
		target = null;
	}
	public void setTarget(Item i, Vector2 vec){
		this.i = i;
		target = vec;
		entityId = null;
	}
	public void setTarget(int id, Vector2 vec){
		entityId = id;
		target = vec;
		i = null;
	}
	public Item getItem() {
		return i;
	}
	public Integer getEntityId() {
		return entityId;
	}
	public Vector2 getTargetVector() {
		return target;
	}
	
	public void setItem(Item i) {
		this.i = i;
	}
	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}
	public void resetTarget(){
		i = null;
		entityId = null;
		target = null;
	}
	public void setTargetVector(int x, int y){
		target = new Vector2(x, y);
	}
}
