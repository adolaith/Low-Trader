package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.Task;
import com.ado.trader.entities.AiComponents.base.TaskDecorator;
import com.ado.trader.entities.components.Inventory;
import com.ado.trader.items.Item;
import com.ado.trader.systems.AiSystem;

public class CheckInventoryFull extends TaskDecorator {

	public CheckInventoryFull(AiSystem aiSys, Task task) {
		super(aiSys, task);
	}

	public CheckInventoryFull(AiSystem aiSys, Task task, String name) {
		super(aiSys, task, name);
	}

	@Override
	public void doTask() {
		task.doTask();
	}
	@Override
	public boolean checkConditions(){
		Inventory inven = aiSys.currentEntity.getComponent(Inventory.class);
		boolean hasSpace = true;
		
		if(inven.getItems().size == inven.max){
			hasSpace = false;
			for(Item i: inven.getItems()){
				if(i == null){
					hasSpace = true;
					break;
				}
			}
		}
		
		return task.checkConditions() && hasSpace;
	}
}
