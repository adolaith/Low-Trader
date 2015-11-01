package com.ado.trader.entities;

import com.ado.trader.utils.FileLogger;
import com.ado.trader.utils.GameServices;
import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.utils.Bag;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;

public class EntityDataLoader {

	public EntityDataLoader(TextureAtlas atlas){
		createSprites(atlas);
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
	public static Entity loadEntity(Json json, JsonValue e){
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

	//loads entity profile templates
	public void loadEntityProfiles(FileHandle profilesFile, EntityFactory collection) {
		Json json = new Json();
//		JsonValue profiles = json.fromJson(null, Gdx.files.internal("data/EntityProfiles"));
		JsonValue profiles = json.fromJson(null, profilesFile);
		profiles = profiles.child();
		
		//loops each entity
		for(JsonValue e = profiles.child(); e != null; e = e.next()){
			//store entity component data/settings
			collection.getEntityData().put(e.get("name").asString(), e);
		}
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
	private void createSprites(TextureAtlas atlas){
		ArrayMap<String, Sprite[]> sprites = new ArrayMap<String, Sprite[]>();
		Array<AtlasRegion> regions = atlas.getRegions();
		
		for(AtlasRegion a: regions){
			if(!a.name.contains("/")){
				//load entity sprites
				if(sprites.containsKey(a.name) || a.index > 0){
					continue;
				}
				Sprite[] list = new Sprite[4];
				
				if(a.index == -1){
					list[0] = new Sprite(a);
					list[0].scale(1);
					
					sprites.put(a.name, list);
					continue;
				}
				
				Sprite sprite = new Sprite(a);
				sprite.scale(1);
				list[0] = sprite;
				
				sprite = new Sprite(sprite);
				sprite.flip(true, false);
				list[1] = sprite;
				
				sprite = atlas.createSprite(a.name, a.index + 1);
				if(sprite != null){
					sprite.scale(1);
					list[2] = sprite;

					sprite = new Sprite(sprite);
					sprite.flip(true, false);
					list[3] = sprite;
				}
				
				sprites.put(a.name, list);
				
			}else if(a.name.startsWith("walls")){
				//load wall sprites
				String name = a.name.substring(a.name.indexOf('/') + 1);
				name = name.split("_")[0];
				
				if(sprites.containsKey(name)){
					Sprite s = new Sprite(a);
					s.scale(1);
					sprites.get(name)[1] = s;
					continue;
				}
				
				Sprite[] list = new Sprite[2];
				Sprite s = new Sprite(a);
				s.scale(1);
				list[0] = s;
				
				sprites.put(name, list);
			}
		}
	}
}
