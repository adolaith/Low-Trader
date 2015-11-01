package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.Task;
import com.ado.trader.entities.AiComponents.base.TaskDecorator;
import com.ado.trader.entities.components.Inventory;
import com.ado.trader.entities.components.Target;
import com.ado.trader.items.Item;
import com.ado.trader.map.EntityLayer;
import com.ado.trader.map.ItemLayer;
import com.ado.trader.systems.AiSystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;

public class CheckTarget extends TaskDecorator{

	//use a switch function to check targetType(item or entity)
	public CheckTarget(AiSystem aiSys, String targetType, Task task) {
		super(aiSys, task);
	}

	public CheckTarget(AiSystem aiSys, Task task, String name) {
		super(aiSys, task, name);
	}
	
	@Override
	public void doTask() {
		Target t = aiSys.currentEntity.getComponent(Target.class);
		
		if(t.getEntityId() != null){
			EntityLayer eLayer = aiSys.game.getMap().getCurrentLayerGroup().entityLayer;
			if(!eLayer.isOccupied((int) t.getTargetVector().x,(int) t.getTargetVector().y)){
				t.resetTarget();
				task.getControl().finishWithFailure();
				LogTask("CHECK TARGET FAILED");
				return;
			}else if(t.getItem() != null){
				ComponentMapper<Inventory> invenMapper = aiSys.getWorld().getMapper(Inventory.class);
				Entity e = aiSys.getWorld().getEntity(eLayer.map[(int) t.getTargetVector().x][(int) t.getTargetVector().y]);
				Inventory inven = invenMapper.get(e);
				boolean hasItem = false;
				for(Item i: inven.getItems()){
					if(i.equals(t.getItem())){
						hasItem = true;
					}
				}
				if(!hasItem){
					t.resetTarget();
					task.getControl().finishWithFailure();
					LogTask("CHECK TARGET FAILED");
					return;
				}
			}
		}else if(t.getItem() != null){
			ItemLayer iLayer = aiSys.game.getMap().getCurrentLayerGroup().itemLayer;
			if(!iLayer.isOccupied((int) t.getTargetVector().x,(int) t.getTargetVector().y)){
				t.resetTarget();
				task.getControl().finishWithFailure();
				LogTask("CHECK TARGET FAILED");
				return;
			}
		}
		LogTask("TARGET STILL VALID");
		task.doTask();
	}

}
