package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.Task;
import com.ado.trader.entities.AiComponents.base.TaskDecorator;
import com.ado.trader.entities.components.Hunger;
import com.ado.trader.systems.AiSystem;

public class CheckHunger extends TaskDecorator {
	float percentage;

	public CheckHunger(AiSystem aiSys, Task task, String percentageInt) {
		super(aiSys, task);
		this.percentage = Float.valueOf(percentage);
	}
	
	public CheckHunger(AiSystem aiSys, Task task, String name, String percentage) {
		super(aiSys, task, name);
		this.percentage = Float.valueOf(percentage);
	}

	@Override
	public void doTask() {
		task.doTask();
	}
    @Override
    public boolean checkConditions(){
    	Hunger h = aiSys.currentEntity.getComponent(Hunger.class);
    	float value = percentage*h.max;
    	return task.checkConditions() && h.value <= value;
    }
}
