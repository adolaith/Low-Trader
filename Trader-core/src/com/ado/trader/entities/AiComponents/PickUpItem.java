package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.LeafTask;
import com.ado.trader.entities.components.Animation;
import com.ado.trader.entities.components.Inventory;
import com.ado.trader.entities.components.Money;
import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.Target;
import com.ado.trader.items.Coins;
import com.ado.trader.items.Item;
import com.ado.trader.map.ItemLayer;
import com.ado.trader.systems.AiSystem;
import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;

public class PickUpItem extends LeafTask {
	float animCount, interval;

	public PickUpItem(AiSystem aiSys) {
		super(aiSys);
	}
	public PickUpItem(AiSystem aiSys, String name) {
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
	public void end() {
		aiSys.currentEntity.getComponent(Animation.class).resetAnimation();
	}

	@Override
	public void doTask() {
		animCount += aiSys.game.getWorld().getDelta();
		if(animCount >= interval){
			Target t = aiSys.currentEntity.getComponent(Target.class);
			if(t.getEntityId() != null){
				Entity e = aiSys.getWorld().getEntity(t.getEntityId());
				Inventory container = aiSys.getInvenm().get(e);
				for(Item i: container.getItems()){
					if(i.equals(t.getItem())){
						if(t.getItem().hasDataType(Coins.class)){
							Money m = aiSys.currentEntity.getComponent(Money.class);
							m.value += t.getItem().getData(Coins.class).value;
						}else{
							Inventory inven = aiSys.currentEntity.getComponent(Inventory.class);
							inven.add(t.getItem());
						}
						container.removeItem(i);
					}
				}
			}else{
				ItemLayer iLayer = aiSys.game.getMap().getCurrentLayerGroup().itemLayer;

				if(t.getItem().hasDataType(Coins.class)){
					Money m = aiSys.currentEntity.getComponent(Money.class);
					m.value += t.getItem().getData(Coins.class).value;
				}else{
					Inventory inven = aiSys.currentEntity.getComponent(Inventory.class);
					inven.add(t.getItem());
				}
				iLayer.map[(int)t.getTargetVector().x][(int)t.getTargetVector().y] = null;
			}
			t.resetTarget();
			control.finishWithSuccess();
		}
	}

	@Override
	public boolean checkConditions() {
		Target t = aiSys.currentEntity.getComponent(Target.class);
		Position p = aiSys.currentEntity.getComponent(Position.class);
		if(Math.abs(p.getX() - t.getTargetVector().x) > 2 ||
				Math.abs(p.getY() - t.getTargetVector().y) > 2){
			return false;
		}
		return true;
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
