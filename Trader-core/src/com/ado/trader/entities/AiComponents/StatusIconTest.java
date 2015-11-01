package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.LeafTask;
import com.ado.trader.systems.AiSystem;
import com.ado.trader.systems.StatusIconSystem;

public class StatusIconTest extends LeafTask {

	public StatusIconTest(AiSystem aiSys) {
		super(aiSys);
	}

	public StatusIconTest(AiSystem aiSys, String name) {
		super(aiSys, name);
	}

	@Override
	public void end() {
	}

	@Override
	public void doTask() {
		StatusIconSystem iconSys = aiSys.getWorld().getSystem(StatusIconSystem.class);
		iconSys.newIconAnimation("iconBored", aiSys.currentEntity);
		control.finishWithSuccess();
	}

	@Override
	public boolean checkConditions() {
		return true;
	}

}
