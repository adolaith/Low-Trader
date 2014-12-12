package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.LeafTask;
import com.ado.trader.entities.components.Animation;
import com.ado.trader.entities.components.Movement;
import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.Target;
import com.ado.trader.map.EntityLayer;
import com.ado.trader.systems.AiSystem;
import com.ado.trader.systems.MovementSystem;
import com.ado.trader.utils.pathfinding.Path;
import com.badlogic.gdx.math.Vector2;

public class WalkToTarget extends LeafTask {
	Movement m;
	
	public WalkToTarget(AiSystem aiSys) {
		super(aiSys);
	}
	public WalkToTarget(AiSystem aiSys, String name) {
		super(aiSys, name);
	}
	@Override
	public void doTask() {
		if(m.getPath() == null){
			LogTask("END OF PATH");
			control.finishWithSuccess();
		}
	}
	@Override
	public boolean checkConditions() {
		LogTask("Pathfinding");
		return true;
	}
	@Override
	public void start() {
		super.start();
		m = aiSys.getMm().get(aiSys.currentEntity);
		Position pos = aiSys.getPm().get(aiSys.currentEntity);
		Path p=null;
		Vector2 target = aiSys.currentEntity.getComponent(Target.class).getTargetVector();
		EntityLayer eLayer = aiSys.game.getMap().getCurrentLayerGroup().entityLayer;

		for(int i = 0; i < 4; i++){
			switch(i){
			case 0:
				if((int)target.x-1 > 0){
					if(eLayer.map[(int)target.x-1][(int)target.y] == null){
						p = aiSys.game.getPathfinder().findPath(m, pos.getX(), pos.getY(), (int)target.x-1, (int)target.y);
					}
				}
				break;
			case 1:
				if((int)target.x+1 < eLayer.map.length){
					if(eLayer.map[(int)target.x+1][(int)target.y] == null){
						p = aiSys.game.getPathfinder().findPath(m, pos.getX(), pos.getY(), (int)target.x+1, (int)target.y);
					}
				}
				break;
			case 2:
				if((int)target.y-1 > 0){
					if(eLayer.map[(int)target.x][(int)target.y-1] == null){
						p = aiSys.game.getPathfinder().findPath(m, pos.getX(), pos.getY(), (int)target.x, (int)target.y-1);
					}
				}
				break;
			case 3:
				if((int)target.y+1 < eLayer.map[(int)target.x].length){
					if(eLayer.map[(int)target.x][(int)target.y+1] == null){
						p = aiSys.game.getPathfinder().findPath(m, pos.getX(), pos.getY(), (int)target.x-1, (int)target.y+1);
					}
				}
				break;
			}
			
			if(p != null){
				break;
			}
		}
		
		if(p==null){
			control.finishWithFailure();
			LogTask("COULDNT FIND A PATH");
			return;
		}
		LogTask("FOUND A PATH");
		m.setPath(p);
		MovementSystem mSys = aiSys.getWorld().getSystem(MovementSystem.class);
		mSys.findDirection(aiSys.currentEntity.getComponent(Animation.class), pos, m);
	}
	@Override
	public void end() {
		LogTask("PATHING FAILED");
		m.setPath(null);
	}
}
