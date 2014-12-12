package com.ado.trader.systems;

import com.artemis.systems.VoidEntitySystem;

public class VoidIntervalSystem extends VoidEntitySystem {
	float counter;
	final float interval;

	public VoidIntervalSystem(float interval) {
		this.interval = interval;
	}
	
	@Override
	protected boolean checkProcessing() {
		counter += world.getDelta();
		if(counter >= interval) {
			counter -= interval;
			return true;
		}
		return false;
	}

	@Override
	protected void processSystem() {
	}

}
