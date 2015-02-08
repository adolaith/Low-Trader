package com.ado.trader.systems;

import com.ado.trader.GameMain;
import com.ado.trader.entities.components.Animation;
import com.ado.trader.entities.components.Area;
import com.ado.trader.entities.components.Feature;
import com.ado.trader.entities.components.Health;
import com.ado.trader.entities.components.Hunger;
import com.ado.trader.entities.components.Inventory;
import com.ado.trader.entities.components.Mask;
import com.ado.trader.entities.components.Money;
import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.SpriteComp;
import com.ado.trader.entities.components.Type;
import com.ado.trader.entities.components.Wall;
import com.ado.trader.items.Item;
import com.ado.trader.rendering.MaskingSystem;
import com.ado.trader.utils.FileParser;
import com.ado.trader.utils.GameServices;
import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class SaveSystem extends EntityProcessingSystem {
	FileParser parser;
	GameServices gameRes;
	String saveDir;

	@SuppressWarnings("unchecked")
	public SaveSystem(GameServices gameRes) {
		super(Aspect.getAspectForAll(Type.class));
		this.gameRes = gameRes;
		parser = gameRes.getParser();
	}
	public void setPassive(boolean passive){
		super.setPassive(passive);
	}
	public void saveEntities(String saveDir){
		this.saveDir = saveDir;
		process();
	}
	@Override
	protected void begin(){
		parser.initParser(saveDir+"/entities", true, true);
	}

	@Override
	protected void process(Entity e) {
		Type t = world.getMapper(Type.class).get(e);
		parser.addElement("id", Integer.toString(t.getTypeID()));
		
		if(world.getMapper(Area.class).has(e)){
			Area a = world.getMapper(Area.class).get(e);
			String s = "";
			for(Vector2 vec:a.area){
				s += vec.x+","+vec.y+"'";
			}
			parser.addElement("area", s);
		}
		
		parser.addElement("pos", (int)world.getMapper(Position.class).get(e).getIsoPosition().x+
				","+(int)world.getMapper(Position.class).get(e).getIsoPosition().y+
				","+world.getMapper(Position.class).get(e).getHeightLayer());
		
		if(world.getMapper(SpriteComp.class).has(e)){		//sprite
			SpriteComp sC = world.getMapper(SpriteComp.class).get(e);
			String s = ""+sC.mainId+","+sC.mainSprite.isFlipX();
			if(sC.secondarySprite!=null){
				s += ","+sC.secondId+","+sC.secondarySprite.isFlipX();
			}
			parser.addElement("sprite", s);
		}
		
		if(world.getMapper(Animation.class).has(e)){		//animation skin
			Array<String> a = new Array<String>();
			a.add(e.getComponent(Animation.class).getSkeleton().getSkin().getName());
			a.add(e.getComponent(Animation.class).getSkeleton().findSlot("head").getAttachment().getName());
			parser.addElement("animation", a);
		}
		
		if(world.getMapper(Health.class).has(e)){		
			Health h = world.getMapper(Health.class).get(e);
			parser.addElement("health", h.value+","+h.max);
		}
		
		if(world.getMapper(Hunger.class).has(e)){		
			Hunger h = world.getMapper(Hunger.class).get(e);
			parser.addElement("hunger", h.value+","+h.max);
		}
		
		if(world.getMapper(Money.class).has(e)){		
			Money m = world.getMapper(Money.class).get(e);
			parser.addElement("money", ""+m.value);
		}
		
		if(world.getMapper(Inventory.class).has(e)){		
			Inventory i = world.getMapper(Inventory.class).get(e);
			String s = "";
			for(Item item: i.getItems()){
				s += item.getId()+",";
			}
			parser.addElement("inventory", i.max+","+s);
		}
		
//		if(world.getMapper(Locations.class).has(e)){	//locations
//			ComponentMapper<Locations> loc = world.getMapper(Locations.class);
//			
//			WorkArea w = loc.get(e).getWork();
//			if(w != null){
//				parser.addElement("work", ""+w.getId() + ","+w.getParentId());
//			}
//			
//			Building b = loc.get(e).getHome(); 
//			if(b != null){
//				parser.addElement("home", ""+b.getBuildingId());
//			}
//		}
		
		if(world.getMapper(Wall.class).has(e)){			//wall props
			Wall w = world.getMapper(Wall.class).get(e);
			String s = ""+w.firstSprite;
			if(w.secondSprite != null){
				s += ","+w.secondSprite;
			}
			parser.addElement("wall", s);
		}
		
		if(world.getMapper(Mask.class).has(e)){			//mask props
			MaskingSystem mS = gameRes.getRenderer().getRenderEntitySystem().getMasks();
			String id = mS.getAllMasks().getKey(world.getMapper(Mask.class).get(e).mask, false);
			parser.addElement("mask", id);
		}
		
		if(world.getMapper(Feature.class).has(e)){
			Feature f = world.getMapper(Feature.class).get(e);
			parser.addElement("feature", f.spriteId+","+f.sprite.isFlipX());
		}
		
		parser.newNode();
	}
	@Override
	protected void end(){
		Gdx.app.log(GameMain.LOG, "SAVING ENTITIES");
		parser.writeToFile();
	}
}
