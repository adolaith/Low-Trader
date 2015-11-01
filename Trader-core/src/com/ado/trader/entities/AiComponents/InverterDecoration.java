package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.Task;
import com.ado.trader.entities.AiComponents.base.TaskDecorator;
import com.ado.trader.systems.AiSystem;

public class InverterDecoration extends TaskDecorator {

	public InverterDecoration(AiSystem aiSys, Task task) {
		super(aiSys, task);
	}

	public InverterDecoration(AiSystem aiSys, Task task, String name) {
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
