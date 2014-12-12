package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.LeafTask;
import com.ado.trader.entities.components.Animation;
import com.ado.trader.entities.components.Locations;
import com.ado.trader.entities.components.Position;
import com.ado.trader.systems.AiSystem;
import com.ado.trader.utils.GameWorldTime.Time;
import com.badlogic.gdx.math.Vector2;

public class Sleep extends LeafTask {

	public Sleep(AiSystem aiSys) {
		super(aiSys);
	}

	@Override
	public void start() {
		Position p =aiSys.currentEntity.getComponent(Position.class); 
		Vector2 bed = aiSys.currentEntity.getComponent(Locations.class).getHome();
		p.setPosition((int)bed.x, (int)bed.y);
		p.getIsoPosition().y = p.getIsoPosition().y+28;
		aiSys.currentEntity.getComponent(Animation.class).getSkeleton().getRootBone().setRotation(-60);
	}

	@Override
	public void end() {

	}

	@Override
	public void doTask() {
		if(aiSys.game.getTime().getTimeOfDay()==Time.MORNING){
			Position p =aiSys.currentEntity.getComponent(Position.class);
			Vector2 bed = aiSys.currentEntity.getComponent(Locations.class).getHome();
			if(!aiSys.game.getDepth().isOccupied((int)bed.x-1, (int)bed.y)){
				p.setPosition((int)bed.x, (int)bed.y-1);
			}else{
				p.setPosition((int)bed.x, (int)bed.y+1);
			}
			aiSys.currentEntity.getComponent(Animation.class).getSkeleton().setToSetupPose();
			control.finishWithSuccess();
		}
	}

	@Override
	public boolean checkConditions() {
		return true;
	}

}
