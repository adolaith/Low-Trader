package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.Task;
import com.ado.trader.entities.AiComponents.base.TaskDecorator;
import com.ado.trader.systems.AiSystem;

public class RegulatorDecorator extends TaskDecorator {
	float updateTime;	//in mills
	long lastUpdate;

	public RegulatorDecorator(AiSystem aiSys, Task task, String updateTimeMills) {
		super(aiSys, task);
		this.updateTime = Float.valueOf(updateTimeMills);
		lastUpdate = System.currentTimeMillis();
	}
	
	public RegulatorDecorator(AiSystem aiSys, Task task, String name, float updateTime) {
		super(aiSys, task, name);
		this.updateTime = updateTime;
		lastUpdate = System.currentTimeMillis() - (long)updateTime;
	}

	@Override
	public void doTask() {
		task.doTask();
	}
	
    /**
     * Starts the task and the regulator
     */
    @Override 
    public void start(){
            task.start();
    }
    @Override
    public boolean checkConditions(){
		if(System.currentTimeMillis() - lastUpdate >= updateTime){
			lastUpdate = System.currentTimeMillis();
			return true;
		}
		return false;
    }
}
