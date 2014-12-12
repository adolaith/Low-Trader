package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.LeafTask;
import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.Target;
import com.ado.trader.map.EntityLayer;
import com.ado.trader.systems.AiSystem;
import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;

public class TargetHuman extends LeafTask {

	public TargetHuman(AiSystem aiSys) {
		super(aiSys);
	}

	public TargetHuman(AiSystem aiSys, String name) {
		super(aiSys, name);
	}

	@Override
	public void end() {

	}

	@Override
	public void doTask() {
		EntityLayer eLayer = aiSys.game.getMap().getCurrentLayerGroup().entityLayer;
		Position pos = aiSys.currentEntity.getComponent(Position.class);
		Entity e = eLayer.getClosestEntity(pos.getX(), pos.getY(), 4, "human");
		if(e != null){
			Position tPos = e.getComponent(Position.class);
			Target t = aiSys.currentEntity.getComponent(Target.class);
			t.setTarget(e.getId(), new Vector2(tPos.getX(), tPos.getY()));
			control.finishWithSuccess();
			return;
		}
		control.finishWithFailure();
	}

	@Override
	public boolean checkConditions() {
		return true;
	}

}
