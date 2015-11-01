package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.LeafTask;
import com.ado.trader.entities.AiComponents.base.ParentTask;
import com.ado.trader.entities.AiComponents.base.ParentTaskController;
import com.ado.trader.entities.AiComponents.base.Task;
import com.ado.trader.entities.components.AiProfile;
import com.ado.trader.entities.components.Locations;
import com.ado.trader.entities.components.Position;
import com.ado.trader.map.LayerGroup;
import com.ado.trader.map.WorkZone;
import com.ado.trader.map.WorkZone.WorkArea;
import com.ado.trader.map.Zone;
import com.ado.trader.systems.AiSystem;
import com.badlogic.gdx.utils.Array;

public class FindWork extends LeafTask {

	public FindWork(AiSystem aiSys) {
		super(aiSys);
	}

	public FindWork(AiSystem aiSys, String name) {
		super(aiSys, name);
	}

	@Override
	public void end() {

	}

	@Override
	public void doTask() {
		LayerGroup lGroup = aiSys.game.getMap().getCurrentLayerGroup();
		Position pos = aiSys.currentEntity.getComponent(Position.class);
		Array<Zone> zones = lGroup.zoneLayer.getNeighborZones(pos.getX(), pos.getY(), 12);
		for(Zone z: zones){
			if(z instanceof WorkZone){
				WorkArea a = ((WorkZone)z).findWork(aiSys.currentEntity.getId());
				if(a != null){
					//create work ai profile
					Task workProfile = aiSys.getAiProfile(a.aiWorkProfile);
					
					AiProfile entityProfile = aiSys.currentEntity.getComponent(AiProfile.class);
					ParentTaskController root = (ParentTaskController) (((ParentTask)entityProfile.getTaskProfile()).getControl());
					ParentTaskController med = (ParentTaskController) (((ParentTask) root.getSubTask("med")).getControl());
					
					//add work profile to medium priority ai group
					med.Add(workProfile);
					
					control.finishWithSuccess();
					return;
				}
			}
		}
		
		control.finishWithFailure();
	}

	@Override
	public boolean checkConditions() {
		Locations l = aiSys.currentEntity.getComponent(Locations.class);
		if(l.getWork() == null){
			return true;
		}
		return false;
	}
}
