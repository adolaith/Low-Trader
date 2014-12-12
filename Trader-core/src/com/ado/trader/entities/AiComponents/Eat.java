package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.LeafTask;
import com.ado.trader.entities.components.Animation;
import com.ado.trader.entities.components.Hunger;
import com.ado.trader.entities.components.Inventory;
import com.ado.trader.entities.components.Target;
import com.ado.trader.items.FoodItem;
import com.ado.trader.items.Item;
import com.ado.trader.items.ItemPosition;
import com.ado.trader.map.ItemLayer;
import com.ado.trader.systems.AiSystem;
import com.artemis.managers.GroupManager;

public class Eat extends LeafTask {
	float animCount, interval;

	public Eat(AiSystem aiSys) {
		super(aiSys);
	}
	
	public Eat(AiSystem aiSys, String name) {
		super(aiSys, name);
	}

	@Override
	public void start() {
		super.start();
		GroupManager gm = aiSys.getWorld().getManager(GroupManager.class);
		Animation a = aiSys.currentEntity.getComponent(Animation.class);
		if(gm.isInGroup(aiSys.currentEntity, "human")){
			setHumanEating(a);
		}else{
			a.resetAnimation();
			a.getMainState().setAnimation(0, "eat", false); 
			interval = a.getSkeleton().getData().findAnimation("eat").getDuration();
			Target t = aiSys.currentEntity.getComponent(Target.class);
			ItemPosition iPos = t.getItem().getData(ItemPosition.class);
			ItemLayer iLayer = aiSys.game.getMap().getCurrentLayerGroup().itemLayer;
			iLayer.map[(int)iPos.position.x][(int)iPos.position.y] = null;
			t.resetTarget();
		}
		animCount = 0;
	}

	@Override
	public void end() {
		Animation a = aiSys.currentEntity.getComponent(Animation.class);
		a.resetAnimation();
	}

	@Override
	public void doTask() {
		animCount += aiSys.game.getWorld().getDelta();
		if(animCount >= interval){
			control.finishWithSuccess();
		}
	}

	@Override
	public boolean checkConditions() {
		return true;
	}
	private void setHumanEating(Animation a){
		Item food = null;
		
		Inventory inven = aiSys.currentEntity.getComponent(Inventory.class);
		for(Item i: inven.getItems()){
			if(i.hasDataType(FoodItem.class)){
				food = i;
				break;
			}
		}
		a.resetAnimation();
		a.getMainState().setAnimation(0, "eat", false); 
		interval = a.getSkeleton().getData().findAnimation("eat").getDuration();
		Hunger h = aiSys.currentEntity.getComponent(Hunger.class);
		h.value += food.getData(FoodItem.class).value;
		
		inven.getItems().removeValue(food, false);
	}
}
