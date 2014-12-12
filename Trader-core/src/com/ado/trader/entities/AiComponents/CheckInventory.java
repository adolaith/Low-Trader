package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.Task;
import com.ado.trader.entities.AiComponents.base.TaskDecorator;
import com.ado.trader.entities.components.Inventory;
import com.ado.trader.items.Item;
import com.ado.trader.items.ItemData;
import com.ado.trader.systems.AiSystem;

public class CheckInventory extends TaskDecorator {
	Class<? extends ItemData> type;

	public CheckInventory(AiSystem aiSys, Task task, String type) {
		super(aiSys, task);
		
		try {
			Class<? extends ItemData> t = (Class<? extends ItemData>)Class.forName(type);
			this.type = t;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public CheckInventory(AiSystem aiSys, Task task, String name, String type) {
		super(aiSys, task, name);
		try {
			Class<? extends ItemData> t = (Class<? extends ItemData>)Class.forName(type);
			this.type = t;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void doTask() {
		task.doTask();
	}
	@Override
    public boolean checkConditions(){
		Inventory inven = aiSys.currentEntity.getComponent(Inventory.class);
		for(Item i: inven.getItems()){
			if(i.hasDataType(type)){
				return task.checkConditions() && true;
			}
		}
		return false;
	}
}
