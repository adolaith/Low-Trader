package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.Task;
import com.ado.trader.entities.AiComponents.base.TaskDecorator;
import com.ado.trader.entities.components.Health;
import com.ado.trader.systems.AiSystem;

public class CheckHealth extends TaskDecorator {
	int percentage;

	public CheckHealth(AiSystem aiSys, Task task, String percentage) {
		super(aiSys, task);
		this.percentage = Integer.valueOf(percentage);
	}
	
	public CheckHealth(AiSystem aiSys, Task task, String name, String percentage) {
		super(aiSys, task, name);
		this.percentage = Integer.valueOf(percentage);
	}

	@Override
	public void doTask() {
		task.doTask();
	}
    @Override
    public boolean checkConditions(){
    	Health h = aiSys.currentEntity.getComponent(Health.class);
    	int value = (percentage/100)*h.max;
    	return task.checkConditions() && h.value <= value;
    }
}
