package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.Task;
import com.ado.trader.entities.AiComponents.base.TaskDecorator;
import com.ado.trader.systems.AiSystem;

public class LoopOnFail extends TaskDecorator {

	public LoopOnFail(AiSystem aiSys, Task task) {
		super(aiSys, task);
	}

	public LoopOnFail(AiSystem aiSys, Task task, String name) {
		super(aiSys, task, name);
	}

	@Override
	public void doTask() {
		task.doTask();
		if(task.getControl().failed()){
			task.start();
			return;
		}
		
	}

}
