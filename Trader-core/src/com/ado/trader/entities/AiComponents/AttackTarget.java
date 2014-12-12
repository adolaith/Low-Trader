package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.LeafTask;
import com.ado.trader.entities.components.Animation;
import com.ado.trader.entities.components.Health;
import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.Target;
import com.ado.trader.systems.AiSystem;
import com.ado.trader.systems.StatusIconSystem;
import com.artemis.Entity;

public class AttackTarget extends LeafTask {
	int count, numberOfAttacks;
	float animCount, interval;

	public AttackTarget(AiSystem aiSys, String numberOfAttacks) {
		super(aiSys);
		this.numberOfAttacks = Integer.valueOf(numberOfAttacks);
	}

	public AttackTarget(AiSystem aiSys, String name, String numberOfAttacks) {
		super(aiSys, name);
		this.numberOfAttacks = Integer.valueOf(numberOfAttacks);
	}
	@Override
	public void start() {
		Animation a = aiSys.currentEntity.getComponent(Animation.class);
		a.getMainState().setAnimation(0, "attack", true);
		
		count = 0;
		animCount = 0;
		interval = a.getSkeleton().getData().findAnimation("attack").getDuration() * 2;
	}
	@Override
	public void end() {
	}

	@Override
	public void doTask() {
		Animation a = aiSys.currentEntity.getComponent(Animation.class);
		
		//check if target still at pos
		Target t = aiSys.currentEntity.getComponent(Target.class);
		Position tPos  = aiSys.getWorld().getEntity(t.getEntityId()).getComponent(Position.class);
		if(tPos.getX() != t.getTargetVector().x || tPos.getY() != t.getTargetVector().y){
			control.finishWithFailure();
			return;
		}
		
		//change combatants stats
		animCount += aiSys.game.getWorld().getDelta();
		if(animCount >= interval){
			animCount = 0;
			count++;
			if(count % 2 == 0){
				Entity e = aiSys.getWorld().getEntity(t.getEntityId());
				StatusIconSystem iconSys = aiSys.getWorld().getSystem(StatusIconSystem.class);
				iconSys.newIconAnimation("iconImportant", e);
				
				Health hp = e.getComponent(Health.class);
				hp.value--;
				
				//reached max num attacks
				if(count % (2 * numberOfAttacks) == 0){
					a.resetAnimation();
					control.finishWithSuccess();
					return;
				}
			}
		}
		
	}

	@Override
	public boolean checkConditions() {
		return true;
	}

}
