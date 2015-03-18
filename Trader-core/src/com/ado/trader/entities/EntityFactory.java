package com.ado.trader.entities;

import com.ado.trader.entities.components.AiProfile;
import com.ado.trader.entities.components.Animation;
import com.ado.trader.entities.components.Area;
import com.ado.trader.entities.components.AttributeTable;
import com.ado.trader.entities.components.Inventory;
import com.ado.trader.entities.components.Movement;
import com.ado.trader.entities.components.Name;
import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.SpriteComp;
import com.ado.trader.map.IntMapLayer;
import com.ado.trader.map.Map;
import com.ado.trader.systems.AiSystem;
import com.ado.trader.utils.GameServices;
import com.artemis.Archetype;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.JsonValue;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;

//Contains entity templates and creates entities.
@Wire
public class EntityFactory{
	static ComponentMapper<Name> nameMap;
	static ComponentMapper<SpriteComp> spriteMap;
	static ComponentMapper<Animation> animMap;
	static ComponentMapper<AiProfile> aiMap;
	static ComponentMapper<Area> areaMap;
	static ComponentMapper<Inventory> inventoryMap;
	static ComponentMapper<Movement> movementMap;
	static ComponentMapper<AttributeTable> attributeMap;
	static ComponentMapper<Position> positionMap;
	static TagManager tagManager;
	static GroupManager groupManager;
	
	EntityLoader loader;
	static Map map;

	static ArrayMap<String, Archetype> entityArchetypes;
	static ArrayMap<String, JsonValue> entityData;
	static ArrayMap<String, SkeletonData> skeletons;
	static ArrayMap<String, AnimationStateData> animationPool;

	public EntityFactory(GameServices gameRes){
		EntityFactory.map = gameRes.getMap();
		entityArchetypes = new ArrayMap<String, Archetype>();
		entityData = new ArrayMap<String, JsonValue>();
		animationPool = new ArrayMap<String, AnimationStateData>();

		loader = new EntityLoader(map.getWorld());
		loader.loadEntityArchetypes(gameRes.getAtlas(), this, 
				gameRes.getRenderer().getRenderEntitySystem());
		
		skeletons = loader.loadSpineData(gameRes.getAtlas(), this);
	}

	//Creates entity from archetype and configures it
	public static Entity createEntity(String entityName){
		Entity entity = map.getWorld().createEntity(entityArchetypes.get(entityName));
		
		for(JsonValue d = entityData.get(entityName).child; d != null; d = d.next){
			switch(d.name){
			case "name":
				nameMap.get(entity).setName(d.asString());
				break;
			case "animation":
				Skeleton skel = new Skeleton(skeletons.get(d.asString()));
				skel.setToSetupPose();
				Animation a = animMap.get(entity);
				a.skeleton = skel;
				a.setAnimationData(animationPool.get(d.asString()));
				a.setTileSize(map.getTileWidth(), map.getTileHeight());
				break;
			case "ai":
				AiSystem aiSys = map.getWorld().getSystem(AiSystem.class);
				aiMap.get(entity).setAiProfile(aiSys.getAiProfile(d.asString()));
				break;
			case "area":
				Area area = areaMap.get(entity);
				area.area = new Array<Vector2>();
				for(JsonValue v = d.child; v != null; v = v.next){
					float[] xy = v.asFloatArray();
					area.area.add(new Vector2(xy[0], xy[1]));
				}
				break;
			case "attributes":
				
				break;
			case "inventory":
				inventoryMap.get(entity).init(d.asInt());
				break;
			case "movement":
				movementMap.get(entity).init(d.asFloat());
				break;
			case "group":
				groupManager.add(entity, d.asString());
				break;
			case "tags":
				tagManager.register(d.asString(), entity);
				break;
			}
		}
		
		return entity;
	}

	//create entity with specified sprite
	public static Entity createEntity(String entityName, int spriteIndex){
		Entity e = createEntity(entityName);
		spriteMap.get(e).mainSprite = spriteIndex;
		
		return e;
	}
	public static void deleteEntity(int x, int y, int h, IntMapLayer layer){
		if(layer.isOccupied(x,y,h)){
			Entity e = map.getWorld().getEntity(layer.map[x][y][h]);

			Position p = positionMap.get(e);
			layer.deleteFromMap(p.getX(),p.getY(),h);
			
			if(areaMap.has(e)){
				for(Vector2 vec: areaMap.get(e).area){
					layer.deleteFromMap((int)(p.getX()+vec.x),(int)(p.getY()+vec.y),h);
				}
			}
			e.edit().deleteEntity();
		}
	}
	public ArrayMap<String, JsonValue> getEntityData(){
		return entityData;
	}
	public ArrayMap<String, Archetype> getEntityProfiles() {
		return entityArchetypes;
	}
	public EntityLoader getLoader() {
		return loader;
	}
}
