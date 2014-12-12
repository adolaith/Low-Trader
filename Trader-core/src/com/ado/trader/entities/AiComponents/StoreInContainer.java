package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.LeafTask;
import com.ado.trader.entities.components.Animation;
import com.ado.trader.entities.components.Inventory;
import com.ado.trader.entities.components.Locations;
import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.Target;
import com.ado.trader.items.FoodItem;
import com.ado.trader.items.Item;
import com.ado.trader.items.ToolItem;
import com.ado.trader.systems.AiSystem;
import com.badlogic.gdx.math.Vector2;

public class StoreInContainer extends LeafTask {
	float animCount, interval;

	public StoreInContainer(AiSystem aiSys) {
		super(aiSys);
	}

	public StoreInContainer(AiSystem aiSys, String name) {
		super(aiSys, name);
	}

	@Override
	public void start() {
		super.start();
		Animation a = aiSys.currentEntity.getComponent(Animation.class);
		Target t = aiSys.currentEntity.getComponent(Target.class);
		Position p = aiSys.currentEntity.getComponent(Position.class);
		animCount = 0;
		interval = a.getSkeleton().getData().findAnimation("pickUpFront").getDuration();
		setAnimation(a, p, t.getTargetVector());
	}
	@Override 
	public void doTask() {
		animCount += aiSys.game.getWorld().getDelta();
		if(animCount >= interval){
			Inventory npc = aiSys.currentEntity.getComponent(Inventory.class);
			
			Target t = aiSys.currentEntity.getComponent(Target.class);
			
			Inventory container = aiSys.getWorld().getEntity(t.getEntityId()).getComponent(Inventory.class);
			
			Item food = null;
			Item tool = null;
			for(Item i: npc.getItems()){
				if(i.hasDataType(FoodItem.class)){
					if(food == null){
						food = i;
					}else if(i.getData(FoodItem.class).value > i.getData(FoodItem.class).value){
						food = i;
					}
				}
			}
			Locations l = aiSys.currentEntity.getComponent(Locations.class);
			if(l.getWork() != null){
				for(Item i: npc.getItems()){
					if(i.hasDataType(ToolItem.class)){
						if(tool == null){
							tool = i;
						}else if(i.getData(FoodItem.class).value > i.getData(FoodItem.class).value){
							tool = i;
						}
					}
				}
			}
			
			for(Item i: npc.getItems()){
				if(food != null){
					if(i == food){
						continue;
					}
				}
				if(tool != null){
					if(i == tool){
						continue;
					}
				}
				
				container.add(i);
			}
			npc.getItems().clear();
			npc.add(food);
			npc.add(tool);
			
			t.resetTarget();
			control.finishWithSuccess();
		}
	}

	@Override
	public boolean checkConditions() {
		return true;
	}
	@Override
	public void end() {
		aiSys.currentEntity.getComponent(Animation.class).resetAnimation();
	}
	public void setAnimation(Animation a,Position p,  Vector2 target){
		float srcX = p.getX();
		float srcY = p.getY();
		int directionX = (int)(target.x-srcX);
		int directionY = (int)(target.y-srcY);
		String headName = a.getSkeleton().findSlot("head").getAttachment().getName();
		String bodyName = a.getSkeleton().getSkin().getName().substring(1, a.getSkeleton().getSkin().getName().indexOf("_"));
		
		switch(directionX){
		case 1:
			a.getSkeleton().setSkin("m"+bodyName+"_Rear");
			a.getSkeleton().setAttachment("head", "human/guyR_head"+Integer.valueOf(headName.substring(headName.length()-1)));
			a.getSkeleton().setFlipX(false);
			a.getMainState().setAnimation(0, "pickUpRear", false);
			break;
		case -1:
			a.getSkeleton().setSkin("m"+bodyName+"_Front");
			a.getSkeleton().setAttachment("head", "human/guyF_head"+Integer.valueOf(headName.substring(headName.length()-1)));
			a.getSkeleton().setFlipX(false);
			a.getMainState().setAnimation(0, "pickUpFront", false);
			break;
		}
		switch(directionY){
		case 1:
			a.getSkeleton().setSkin("m"+bodyName+"_Rear");
			a.getSkeleton().setAttachment("head", "human/guyR_head"+Integer.valueOf(headName.substring(headName.length()-1)));
			a.getSkeleton().setFlipX(true);
			a.getMainState().setAnimation(0, "pickUpRear", false);
			break;
		case -1:
			a.getSkeleton().setSkin("m"+bodyName+"_Front");
			a.getSkeleton().setAttachment("head", "human/guyF_head"+Integer.valueOf(headName.substring(headName.length()-1)));
			a.getSkeleton().setFlipX(true);
			a.getMainState().setAnimation(0, "pickUpFront", false);
			break;
		}
	}
}
