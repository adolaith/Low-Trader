package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.Task;
import com.ado.trader.entities.AiComponents.base.TaskDecorator;
import com.ado.trader.entities.components.Inventory;
import com.ado.trader.items.Item;
import com.ado.trader.systems.AiSystem;

public class IsInventoryFull extends TaskDecorator {

	public IsInventoryFull(AiSystem aiSys, Task task) {
		super(aiSys, task);
	}

	public IsInventoryFull(AiSystem aiSys, Task task, String name) {
		super(aiSys, task, name);
	}

	@Override
	public void doTask() {
		task.doTask();
	}
	@Override
	public boolean checkConditions(){
		Inventory inven = aiSys.currentEntity.getComponent(Inventory.class);
		boolean isFull = false;
		
		if(inven.getItems().size == inven.max){
			isFull = true;
			for(Item i: inven.getItems()){
				if(i == null){
					isFull = false;
					break;
				}
			}
		}
		
		return task.checkConditions() && isFull;
	}
}
