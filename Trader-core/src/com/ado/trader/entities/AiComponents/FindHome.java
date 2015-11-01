package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.LeafTask;
import com.ado.trader.entities.components.Locations;
import com.ado.trader.map.HomeZone;
import com.ado.trader.map.Zone;
import com.ado.trader.map.ZoneLayer;
import com.ado.trader.map.Zone.ZoneType;
import com.ado.trader.systems.AiSystem;
import com.ado.trader.systems.StatusIconSystem;

public class FindHome extends LeafTask {

	public FindHome(AiSystem aiSys) {
		super(aiSys);
	}

	public FindHome(AiSystem aiSys, String name) {
		super(aiSys, name);
	}

	@Override
	public void end() {

	}

	@Override
	public void doTask() {
		ZoneLayer zLayer = aiSys.game.getMap().getCurrentLayerGroup().zoneLayer;
		for(Zone zone: zLayer.getZoneList(ZoneType.HOME)){
			HomeZone z = (HomeZone) zone;
			if(!z.houseIsFull()){
				Locations loc = aiSys.currentEntity.getComponent(Locations.class);
				loc.setHome(z);
				z.addOccupant(aiSys.currentEntity.getId());
				StatusIconSystem iconSys = aiSys.getWorld().getSystem(StatusIconSystem.class);
				iconSys.newIconAnimation("iconHouse", aiSys.currentEntity);
				control.finishWithSuccess();
				return;
			}
			
		}
		control.finishWithFailure();
	}

	@Override
	public boolean checkConditions() {
		Locations loc = aiSys.currentEntity.getComponent(Locations.class);
		if(loc.getHome() == null){
			return true;
		}
		return false;
	}

}
