package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.Task;
import com.ado.trader.entities.AiComponents.base.TaskDecorator;
import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.Target;
import com.ado.trader.systems.AiSystem;
import com.badlogic.gdx.math.Vector2;

public class CheckTargetsLocation extends TaskDecorator {

	public CheckTargetsLocation(AiSystem aiSys, Task task) {
		super(aiSys, task);
	}

	public CheckTargetsLocation(AiSystem aiSys, Task task, String name) {
		super(aiSys, task, name);
	}

	@Override
	public void doTask() {
		Target t = aiSys.currentEntity.getComponent(Target.class);
		Position tPos  = aiSys.getWorld().getEntity(t.getEntityId()).getComponent(Position.class);
		if(tPos.getX() != t.getTargetVector().x || tPos.getY() != t.getTargetVector().y){
			t.setTarget(t.getEntityId(), new Vector2(tPos.getX(), tPos.getY()));
			task.start();
			return;
		}
		task.doTask();
	}
}
