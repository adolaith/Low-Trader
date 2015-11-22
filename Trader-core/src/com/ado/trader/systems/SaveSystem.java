package com.ado.trader.systems;

import java.io.FileWriter;
import java.io.IOException;

import com.ado.trader.GameMain;
import com.ado.trader.entities.components.AiProfile;
import com.ado.trader.entities.components.Animation;
import com.ado.trader.entities.components.Area;
import com.ado.trader.entities.components.AttributeTable;
import com.ado.trader.entities.components.FeatureSprite;
import com.ado.trader.entities.components.Inventory;
import com.ado.trader.entities.components.Mask;
import com.ado.trader.entities.components.Money;
import com.ado.trader.entities.components.Movement;
import com.ado.trader.entities.components.Name;
import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.SpriteComp;
import com.ado.trader.entities.components.Type;
import com.ado.trader.entities.components.WallSprite;
import com.ado.trader.utils.GameServices;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;

@Wire
public class SaveSystem extends EntityProcessingSystem {
	ComponentMapper<Name> nameMap;
	ComponentMapper<SpriteComp> spriteMap;
	ComponentMapper<Animation> animMap;
	ComponentMapper<AiProfile> aiMap;
	ComponentMapper<Area> areaMap;
	ComponentMapper<Inventory> inventoryMap;
	ComponentMapper<Movement> movementMap;
	ComponentMapper<AttributeTable> attributeMap;
	ComponentMapper<Position> positionMap;
	ComponentMapper<Money> moneyMap;
	ComponentMapper<WallSprite> wallMap;
	ComponentMapper<Mask> maskMap;
	ComponentMapper<FeatureSprite> featureMap;
	
	Json json;
	String saveDir;

	@SuppressWarnings("unchecked")
	public SaveSystem() {
		super(Aspect.all(Type.class));
		
		setEnabled(false);
	}
	
	public void saveEntities(String saveDir){
		this.saveDir = saveDir;
		process();
	}
	
	@Override
	protected void begin(){
		json = new Json();
		try {
			json.setWriter(new FileWriter(saveDir+"/entities"));
		} catch (IOException e) {
			Gdx.app.log("SaveSystem: ", "Error writing file!");
			e.printStackTrace();
		}
		json.writeArrayStart("savedEntities");
	}

	@Override
	protected void process(Entity e) {
		json.writeObjectStart();
		
		json.writeValue("name", nameMap.get(e).getName());
		
		if(areaMap.has(e)){
			Area a = areaMap.get(e);
			json.writeArrayStart("area");
			for(Vector2 vec:a.area){
				json.writeArrayStart();
				json.writeValue(vec.x);
				json.writeValue(vec.y);
				json.writeArrayEnd();
			}
			json.writeArrayEnd();
		}
		
		Position p = positionMap.get(e);
		json.writeArrayStart("pos");
		json.writeValue(p.getIsoPosition().x);
		json.writeValue(p.getIsoPosition().y);
		json.writeValue(p.getHeightLayer());
		json.writeArrayEnd();
		
		if(spriteMap.has(e)){		//sprite
			SpriteComp sC = spriteMap.get(e);
			json.writeArrayStart("sprite");
			json.writeValue(sC.spriteIndex);
			
			if(sC.secondSprite != null){
				json.writeValue(sC.secondSprite);
			}
			
			json.writeArrayEnd();
		}
		
		if(animMap.has(e)){		//animation skin
			Animation a = animMap.get(e);
			json.writeArrayStart("animation");
			json.writeValue(a.getSkeleton().getSkin().getName());
			json.writeValue(a.getSkeleton().findSlot("head").getAttachment().getName());
			json.writeArrayEnd();
		}
		
		if(moneyMap.has(e)){		
			Money m = moneyMap.get(e);
			json.writeValue("money", m.value);
		}
		
		if(inventoryMap.has(e)){		
			Inventory i = world.getMapper(Inventory.class).get(e);
			json.writeArrayStart("inventory");
			json.writeValue(i.max);
			for(int itemId: i.getItems()){
				String item = nameMap.get(world.getEntity(itemId)).getName();
				json.writeValue(item);
			}
			json.writeArrayEnd();
		}
		
		if(wallMap.has(e)){			//wall props
			WallSprite w = wallMap.get(e);
			json.writeArrayStart("wall");
			json.writeValue(w.firstSprite.name());
			if(w.secondSprite != null){
				json.writeValue(w.secondSprite.name());
			}
			json.writeArrayEnd();
		}
		
		if(maskMap.has(e)){			//mask props
			Mask m = maskMap.get(e);
			json.writeArrayStart("Mask");
			json.writeValue(m.maskName);
			json.writeValue(m.maskIndex);
			json.writeArrayEnd();
		}
		
		if(featureMap.has(e)){
			FeatureSprite f = featureMap.get(e);
			json.writeArrayStart("feature");
			json.writeValue(f.featureName);
			json.writeValue(f.spriteIndex);
			json.writeArrayEnd();
		}
		
		json.writeObjectEnd();
	}
	@Override
	protected void end(){
		Gdx.app.log(GameMain.LOG, "SAVING ENTITIES");
		json.writeArrayEnd();
	}
}
