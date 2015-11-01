package com.ado.trader.entities;

import com.ado.trader.entities.components.Area;
import com.ado.trader.entities.components.Name;
import com.ado.trader.map.Chunk;
import com.ado.trader.utils.FileLogger;
import com.ado.trader.utils.GameServices;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.Bag;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;

//Contains entity templates and creates entities.
public class EntityFactory{
	static ArrayMap<String, SkeletonData> skeletons;
	static ArrayMap<String, AnimationStateData> animationPool;

	public EntityFactory(TextureAtlas atlas){
		animationPool = new ArrayMap<String, AnimationStateData>();
		
		skeletons = loadSpineData(atlas, this);
	}
	
	public static Entity createEntity(Json json, JsonValue e){
		EntityBuilder builder = new EntityBuilder(GameServices.getWorld());
		for(JsonValue v = e.child; v != null; v = v.next){
			try {
				Component c = (Component) json.readValue(Class.forName(v.getString("class")), v);
				builder.with(c);
			} catch (ClassNotFoundException e1) {
				Gdx.app.log("EntityDataLoader", "error loading/deseriallizing entity");
				e1.printStackTrace();
			}
		}
		
		return builder.build();
	}

	public static void saveEntity(int id, Json json){
		Entity e = GameServices.getWorld().getEntity(id);
		json.writeObjectStart();
		Bag<Component> bag = new Bag<>();
		bag = e.getComponents(bag);
		for(Component c: bag){
			json.writeValue(c, c.getClass());
		}
		
		json.writeObjectEnd();
	}
		
	//loads all animations
	public ArrayMap<String, SkeletonData> loadSpineData(TextureAtlas atlas, EntityFactory entities){
		ArrayMap<String, SkeletonData> skeletons = new ArrayMap<String, SkeletonData>();
		String[] files = Gdx.files.internal("data/anim/files.txt").readString().split(",");
		
		for(String file: files){
			FileLogger.writeLog("EntityLoader: loadSpineData: "+ file);
			SkeletonJson json = new SkeletonJson(atlas);
			json.setScale(2f);

			FileHandle f = Gdx.files.internal("data/anim/" +file+ ".json");
			SkeletonData skelData = json.readSkeletonData(f);
			skeletons.put(skelData.getName(), skelData);

			EntityFactory.animationPool.put(skelData.getName(), new AnimationStateData(skelData));
		}
		return skeletons;
	}
	
	public static void deleteWall(Chunk chunk, int tileX, int tileY){
		Entity e = GameServices.getWorld().getEntity(chunk.getWalls().map[tileX][tileY]);

		chunk.getWalls().map[tileX][tileY] = null;
		
		e.deleteFromWorld();
	}
	
	public static void deleteEntity(Chunk chunk, int tileX, int tileY, String name){
		for(int c = 0; c < chunk.getEntities().map[tileX][tileY].length; c++){
			if(chunk.getEntities().map[tileX][tileY][c] == null) continue; 
			
			Entity e = GameServices.getWorld().getEntity(chunk.getEntities().map[tileX][tileY][c]);
			ComponentMapper<Name> nameMap = GameServices.getWorld().getMapper(Name.class);
			ComponentMapper<Area> areaMap = GameServices.getWorld().getMapper(Area.class);
			
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
	public static ArrayMap<String, AnimationStateData> getAnimationPool() {
		return animationPool;
	}
	public static ArrayMap<String, SkeletonData> getSkeletons() {
		return skeletons;
	}
}
