package com.ado.trader.entities.components;

import com.ado.trader.pathfinding.Mover;
import com.ado.trader.pathfinding.Path;
import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

//Stores an entitys path and destionation

public class Movement extends Component implements Mover{
	Path path;
	int stepCount;		//step in path
	Vector2 direction;
	float velocity;

	public Movement() {
	}

	public void init(float f) {
		this.velocity = f;
		direction = new Vector2();
	}
	public float getVelocity() {
		return velocity;
	}
	public Path getPath() {
		return path;
	}
	public void setPath(Path path) {
		this.path = path;
		stepCount = 0;
	}
	public int getStep(){
		return stepCount;
	}
	public void setStep(int step){
		stepCount = step;
	}
	public Vector2 getDirection() {
		return direction;
	}
	public void setDirection(Vector2 dir) {
		this.direction = dir;
	}
}
