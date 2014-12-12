package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.Task;
import com.ado.trader.entities.AiComponents.base.TaskDecorator;
import com.ado.trader.systems.AiSystem;

public class InverterDeco extends TaskDecorator {

	public InverterDeco(AiSystem aiSys, Task task) {
		super(aiSys, task);
	}

	public InverterDeco(AiSystem aiSys, Task task, String name) {
		super(aiSys, task, name);
	}

	@Override
	public void doTask() {
		task.doTask();
	}
	@Override
	public boolean checkConditions(){
		return !task.checkConditions();
	}
}
