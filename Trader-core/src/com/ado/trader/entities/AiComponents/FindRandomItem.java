package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.LeafTask;
import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.Target;
import com.ado.trader.items.Coins;
import com.ado.trader.items.FoodItem;
import com.ado.trader.items.Item;
import com.ado.trader.items.ItemPosition;
import com.ado.trader.items.ToolItem;
import com.ado.trader.map.ItemLayer;
import com.ado.trader.systems.AiSystem;
import com.badlogic.gdx.Gdx;

//finds a random item laying on the ground(does not look in containers)
public class FindRandomItem extends LeafTask {

	public FindRandomItem(AiSystem aiSys) {
		super(aiSys);
	}

	public FindRandomItem(AiSystem aiSys, String name) {
		super(aiSys, name);
	}

	@Override
	public void doTask() {
		Position pos = aiSys.getPm().get(aiSys.currentEntity);
		ItemLayer iLayer = aiSys.game.getMap().getCurrentLayerGroup().itemLayer;
		
		int count = 0;
		Item i = null;
		while(count < 3){
			switch(count){
			case 0:
				i = iLayer.getClosestItem(pos.getX(), pos.getY(), 8, Coins.class);
				break;
			case 1:
				i = iLayer.getClosestItem(pos.getX(), pos.getY(), 10, FoodItem.class);
				break;
			case 2:
				i = iLayer.getClosestItem(pos.getX(), pos.getY(), 8, ToolItem.class);
				break;
			}
			if(i != null){
				break;
			}
			count++;
		}
		
		if(i != null){
			Target t = aiSys.currentEntity.getComponent(Target.class);
			ItemPosition p = i.getData(ItemPosition.class);
			t.setTarget(i, p.position);
			control.finishWithSuccess();
			LogTask("Found an ITEM!");
			return;
		}
		control.finishWithFailure();
	}

	@Override
	public boolean checkConditions() {
		return true;
	}
	@Override
	public void end() {
	}
}
