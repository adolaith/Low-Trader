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
import com.ado.trader.map.Chunk;
import com.ado.trader.map.Map;
import com.ado.trader.systems.AiSystem;
import com.ado.trader.utils.GameServices;
import com.artemis.Archetype;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.JsonValue;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;

//Contains entity templates and creates entities.
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
		
		nameMap = gameRes.getWorld().getMapper(Name.class);
		spriteMap = gameRes.getWorld().getMapper(SpriteComp.class);
		animMap = gameRes.getWorld().getMapper(Animation.class);
		aiMap = gameRes.getWorld().getMapper(AiProfile.class);
		areaMap = gameRes.getWorld().getMapper(Area.class);
		inventoryMap = gameRes.getWorld().getMapper(Inventory.class);
		movementMap = gameRes.getWorld().getMapper(Movement.class);
		attributeMap = gameRes.getWorld().getMapper(AttributeTable.class);
		positionMap = gameRes.getWorld().getMapper(Position.class);
		
		tagManager = gameRes.getWorld().getManager(TagManager.class);
		groupManager = gameRes.getWorld().getManager(GroupManager.class);
		

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
	public static void deleteWall(Chunk chunk, int tileX, int tileY){
		Entity e = map.getWorld().getEntity(chunk.getWalls().map[tileX][tileY]);

		chunk.getWalls().map[tileX][tileY] = null;
		
		e.deleteFromWorld();
	}
	public static void deleteEntity(Chunk chunk, int tileX, int tileY, String name){
		for(int c = 0; c < chunk.getEntities().map[tileX][tileY].length; c++){
			if(chunk.getEntities().map[tileX][tileY][c] == null) continue; 
			Entity e = map.getWorld().getEntity(chunk.getEntities().map[tileX][tileY][c]);
			if(nameMap.get(e).getName().matches(name)){

				chunk.getEntities().map[tileX][tileY][c] = null;

				if(areaMap.has(e)){
					for(Vector2 vec: areaMap.get(e).area){
						chunk.getEntities().map[(int)(tileX + vec.x)][(int)(tileY + vec.y)][c] = null;
					}
				}
				e.deleteFromWorld();
			}
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
