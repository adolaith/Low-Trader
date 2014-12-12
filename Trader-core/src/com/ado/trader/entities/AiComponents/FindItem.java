package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.LeafTask;
import com.ado.trader.entities.components.AiProfile;
import com.ado.trader.entities.components.Inventory;
import com.ado.trader.entities.components.Locations;
import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.Target;
import com.ado.trader.items.Item;
import com.ado.trader.items.ItemData;
import com.ado.trader.items.ItemPosition;
import com.ado.trader.map.EntityLayer;
import com.ado.trader.map.ItemLayer;
import com.ado.trader.map.Zone;
import com.ado.trader.systems.AiSystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.managers.GroupManager;
import com.badlogic.gdx.math.Vector2;

public class FindItem extends LeafTask {
	Class<? extends ItemData> type;

	public FindItem(AiSystem aiSys, String type) {
		super(aiSys);
		
		try {
			Class<? extends ItemData> t = (Class<? extends ItemData>) Class.forName(type);
			this.type = t;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public FindItem(AiSystem aiSys, String name, String type) {
		super(aiSys, name);
		try {
			Class<? extends ItemData> t = (Class<? extends ItemData>) Class.forName(type);
			this.type = t;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doTask() {
		ComponentMapper<Inventory> invenMapper = aiSys.getWorld().getMapper(Inventory.class);
		ComponentMapper<AiProfile> aiMapper = aiSys.getAim();
		Position pos = aiSys.getPm().get(aiSys.currentEntity);
		EntityLayer eLayer = aiSys.game.getMap().getCurrentLayerGroup().entityLayer;
		ItemLayer iLayer = aiSys.game.getMap().getCurrentLayerGroup().itemLayer;
		
		Item i = iLayer.getClosestItem(pos.getX(), pos.getY(), 10, type);
		GroupManager gm = aiSys.getWorld().getManager(GroupManager.class);
		if(i == null && gm.isInGroup(aiSys.currentEntity, "human")){
			Zone z = aiSys.currentEntity.getComponent(Locations.class).getHome();
			if(z != null){
				//search home tiles
				for(Vector2 vec: z.getTileList()){
					//items
					if(iLayer.map[(int)vec.x][(int)vec.y] != null){
						if(iLayer.map[(int)vec.x][(int)vec.y].hasDataType(type)){
							i = iLayer.map[(int)vec.x][(int)vec.y];
							break;
						}
					}
					//containers
					if(eLayer.map[(int)vec.x][(int)vec.y] != null){
						Entity e = aiSys.getWorld().getEntity(eLayer.map[(int)vec.x][(int)vec.y]);
						if(invenMapper.has(e) && !aiMapper.has(e)){
							Inventory inven = invenMapper.get(e);
							for(Item item: inven.getItems()){
								if(item.hasDataType(type)){
									Target t = aiSys.currentEntity.getComponent(Target.class);
									t.setTarget(e.getId(), vec);
									t.setItem(item);
									
									control.finishWithSuccess();
									return;
								}
							}
						}
					}
				}
			}
		}
		
		if(i != null){
			Target t = aiSys.currentEntity.getComponent(Target.class);
			ItemPosition p = i.getData(ItemPosition.class);
			t.setTarget(i, p.position);
			control.finishWithSuccess();
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
