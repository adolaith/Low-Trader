package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.LeafTask;
import com.ado.trader.entities.components.Animation;
import com.ado.trader.systems.AiSystem;
import com.artemis.managers.GroupManager;

public class Idle extends LeafTask {
	float animCount, interval;

	public Idle(AiSystem aiSys) {
		super(aiSys);
	}
	public Idle(AiSystem aiSys, String name) {
		super(aiSys, name);
	}
	
	@Override
	public void doTask() {
		animCount += aiSys.game.getWorld().getDelta();
		if(animCount >= interval){
			control.finishWithSuccess();
		}
	}
	@Override
	public void start() {
		super.start();
		Animation a = aiSys.currentEntity.getComponent(Animation.class);
		GroupManager gm = aiSys.getWorld().getManager(GroupManager.class);
		if(gm.isInGroup(aiSys.currentEntity, "human")){
			a.resetAnimation();
		}
		animCount = 0;
		interval = a.getSkeleton().getData().findAnimation("idle").getDuration();
		a.getMainState().setAnimation(0, "idle", true);
	}
	@Override
	public void end() {
		aiSys.currentEntity.getComponent(Animation.class).resetAnimation();
	}
	@Override
	public boolean checkConditions() {
		return true;
	}
}
