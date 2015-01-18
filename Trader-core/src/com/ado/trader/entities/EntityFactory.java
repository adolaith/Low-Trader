package com.ado.trader.entities;

import com.ado.trader.entities.components.AiProfile;
import com.ado.trader.entities.components.Animation;
import com.ado.trader.entities.components.Area;
import com.ado.trader.entities.components.Health;
import com.ado.trader.entities.components.Hunger;
import com.ado.trader.entities.components.Inventory;
import com.ado.trader.entities.components.Locations;
import com.ado.trader.entities.components.Money;
import com.ado.trader.entities.components.Movement;
import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.SpriteComp;
import com.ado.trader.entities.components.Target;
import com.ado.trader.entities.components.Type;
import com.ado.trader.entities.components.Wall;
import com.ado.trader.gui.GameServices;
import com.ado.trader.map.IntMapLayer;
import com.ado.trader.map.Map;
import com.ado.trader.systems.AiSystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.managers.GroupManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;

//Contains entity templates and creates entities.
public class EntityFactory{
	EntityLoader loader;
	static Map map;

	static ArrayMap<Integer, ArrayMap<String, String>> staticEntities;
	static ArrayMap<Integer, ArrayMap<String, String>> npcs;
	static ArrayMap<String, SkeletonData> skeletons;
	static ArrayMap<String, AnimationStateData> animationPool;

	public EntityFactory(GameServices gameRes){
		EntityFactory.map = gameRes.getMap();
		staticEntities = new ArrayMap<Integer, ArrayMap<String,String>>();
		npcs = new ArrayMap<Integer, ArrayMap<String,String>>();
		animationPool = new ArrayMap<String, AnimationStateData>();

		loader = new EntityLoader(map.getWorld());
		loader.loadEntityProfiles("data/EntityProfiles", gameRes.getAtlas(), this, 
				gameRes.getRenderer().getRenderEntitySystem(), gameRes.getParser());
		
		skeletons = loader.loadSpineData(gameRes.getAtlas(), this);
	}

	//Takes entityName, gets entityProfile from master collection and creates entity accordingly
	public static Entity createEntity(int typeID){
		Entity entity = map.getWorld().createEntity();
		ArrayMap<String, String> profile;
		if(npcs.containsKey(typeID)){
			profile = npcs.get(typeID);
		}else{
			profile = staticEntities.get(typeID);
		}
		entity.edit().add(new Type(typeID));
		
		entity.edit().add(new Position(map.getTileWidth(), map.getTileHeight()));
		
		GroupManager gm = map.getWorld().getManager(GroupManager.class);
		for(String key: profile.keys()){
			switch(key){
			case "tags":
				String[] tags = profile.get(key).split(",");
				for(String s:tags){
					if(s.isEmpty())break;
					gm.add(entity, s);
				}
				break;
			case "animation":
				Skeleton skel = new Skeleton(skeletons.get(profile.get(key)));
				skel.setToSetupPose();
				Animation c = new Animation(skel, animationPool.get(profile.get(key)),
						map.getTileWidth(), map.getTileHeight());
				entity.edit().add(c);
				break;
			case "sprite":
				entity.edit().add(new SpriteComp());
				break;
			case "movement":
				entity.edit().add(new Movement(Float.parseFloat(profile.get(key))));
				entity.edit().add(new Target());
				break;
			case "ai":
				AiSystem aiSys = map.getWorld().getSystem(AiSystem.class);
				entity.edit().add(new AiProfile(aiSys.getAiProfile(profile.get(key))));
				entity.edit().add(new Locations());
				break;
			case "wall":
				entity.edit().add(new Wall());
				break;
			case "area":
				Area areaComp = new Area();
				String[] sList = profile.get(key).split("'");
				for(String s: sList){
					String[] vec = s.split(","); 
					areaComp.area.add(new Vector2(Float.valueOf(vec[0]), Float.valueOf(vec[1])));
				}
				entity.edit().add(areaComp);
				break;
			case "attributes":
				String[] aList = profile.get(key).split(",");
				for(String s: aList){
					switch(s){
					case "health":
						entity.edit().add(new Health());
						break;
					case "hunger":
						entity.edit().add(new Hunger());
						break;
					case "money":
						entity.edit().add(new Money());
						break;
					}
				}
				break;
			case "inventory":
				entity.edit().add(new Inventory(Integer.valueOf(profile.get(key))));
				break;
			}
		}
		map.getWorld().getEntityManager().added(entity);
//		game.getWorld().changedEntity(entity);
		return entity;
	}

	//create entity with specified sprite
	public static Entity createEntity(int typeID, int spriteId, Sprite sprite){
		Entity e = createEntity(typeID);
		e.getComponent(SpriteComp.class).mainId = spriteId;
		e.getComponent(SpriteComp.class).mainSprite = sprite;
		return e;
	}
	public static void deleteEntity(int x, int y, int h, IntMapLayer layer){
		if(layer.isOccupied(x,y,h)){
			Entity e = map.getWorld().getEntity(layer.map[x][y][h]);
//			game.getWorld().disable(e);
			Position p = map.getWorld().getMapper(Position.class).get(e);
			layer.deleteFromMap(p.getX(),p.getY(),h);
			ComponentMapper<Area> areaM = map.getWorld().getMapper(Area.class);
			if(areaM.has(e)){
				for(Vector2 vec: areaM.get(e).area){
					layer.deleteFromMap((int)(p.getX()+vec.x),(int)(p.getY()+vec.y),h);
				}
			}
			e.edit().deleteEntity();
		}
	}
	public ArrayMap<Integer, ArrayMap<String, String>> getEntities() {
		return staticEntities;
	}
	public ArrayMap<Integer, ArrayMap<String, String>> getNpcs(){
		return npcs;
	}
	public EntityLoader getLoader() {
		return loader;
	}
}
