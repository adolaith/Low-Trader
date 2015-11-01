package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.LeafTask;
import com.ado.trader.entities.components.AiProfile;
import com.ado.trader.entities.components.Inventory;
import com.ado.trader.entities.components.Locations;
import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.Target;
import com.ado.trader.map.EntityLayer;
import com.ado.trader.map.Zone;
import com.ado.trader.systems.AiSystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class FindContainer extends LeafTask {

	public FindContainer(AiSystem aiSys) {
		super(aiSys);
	}

	public FindContainer(AiSystem aiSys, String name) {
		super(aiSys, name);
	}

	@Override
	public void end() {

	}

	@Override
	public void doTask() {
		Position pos = aiSys.getPm().get(aiSys.currentEntity);
		EntityLayer eLayer = aiSys.game.getMap().getCurrentLayerGroup().entityLayer;
		ComponentMapper<Inventory> inventoryMapper = aiSys.getWorld().getMapper(Inventory.class);
		ComponentMapper<AiProfile> aiMapper = aiSys.getWorld().getMapper(AiProfile.class);
		Entity container = null;
		
		Zone z = aiSys.currentEntity.getComponent(Locations.class).getHome();
		if(z != null){
			for(Vector2 vec: z.getTileList()){
				if(eLayer.map[(int)vec.x][(int)vec.y] != null){
					Entity e = aiSys.getWorld().getEntity(eLayer.map[(int)vec.x][(int)vec.y]);
					if(inventoryMapper.has(e) && !aiMapper.has(e)){
						Inventory inven = inventoryMapper.get(e);
						if(inven.getItems().size < inven.max){
							container = e;
							break;
						}
					}
				}
			}
		}
		
		if(container == null){
			Array<Integer> entities = eLayer.getNeighborEntitys(pos.getX(), pos.getY(), 10, "container");
			for(int i: entities){
				Entity e = aiSys.getWorld().getEntity(i);
				if(!aiMapper.has(e)){
					Inventory inven = inventoryMapper.get(e);
					if(inven.getItems().size < inven.max){
						container = e;
						break;
					}
				}
			}
		}
		
		if(container != null){
			Target t = aiSys.currentEntity.getComponent(Target.class);
			Position p = container.getComponent(Position.class);
			t.setTarget(container.getId(), new Vector2(p.getX(), p.getY()));
			control.finishWithSuccess();
			return;
		}
		control.finishWithFailure();
	}

	@Override
	public boolean checkConditions() {
		return true;
	}

}
