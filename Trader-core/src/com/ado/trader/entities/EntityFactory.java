package com.ado.trader.entities;

import com.ado.trader.entities.components.Area;
import com.ado.trader.entities.components.Name;
import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.SerializableComponent;
import com.ado.trader.entities.components.WallSprite;
import com.ado.trader.map.Chunk;
import com.ado.trader.utils.FileLogger;
import com.ado.trader.utils.GameServices;
import com.ado.trader.utils.IdGenerator;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.TimeUtils;

//Contains entity templates and creates entities.
public class EntityFactory{
	private static ArrayMap<String, ArrayMap<String, JsonValue>> entityData;
	
	private static Json j;
	
	public EntityFactory(TextureAtlas atlas){
		entityData = new ArrayMap<String, ArrayMap<String, JsonValue>>();
		j = new Json();

		JsonValue cfg = j.fromJson(null, Gdx.files.internal("data/entities/classTags.cfg"));
		
		//load entity component class tags
		for(JsonValue v = cfg.child; v != null; v = v.next){
			if(v.name.matches("note")) continue;
			
			try {
				j.addClassTag(v.name, Class.forName(v.asString()));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		/*loads entity profiles. 
		 *need to load external profiles
		 */
		loadInternalProfiles();
		loadCustomProfiles();
		
	}
	
	//create first entity from profile takes ~4ms and 0ms for every entity of the same type after that. 
	public static Entity createEntity(String id){
		long start = TimeUtils.nanoTime(); 
		
		World w = GameServices.getWorld();
		TagManager tagMan = w.getSystem(TagManager.class);
		GroupManager groupMan = w.getSystem(GroupManager.class);
		String[] split = id.split("\\.");
		
		JsonValue jsonData = entityData.get(split[0]).get(split[1]);
		
		Entity e = w.createEntity();
						
		for(JsonValue c = jsonData.child; c != null; c = c.next){

			if(c.name.matches("tag")){
				tagMan.register(c.asString(), e);
			}else if(c.name.matches("group")){
				String[] groups = c.asStringArray(); 
				
				for(String g: groups){
					groupMan.add(e, g);
				}
			}else{
				SerializableComponent component;
				if(split[0].matches(IdGenerator.WALL) && c.name.matches("sprite")){
					component = new WallSprite();
				}else{
					Class<? extends Component> className = j.getClass(c.name);
					
					component = (SerializableComponent) e.edit().create(className);
				}		
				
				component.load(c);
				
				e.edit().add(component);
			}
		}
		
		//all entities get a Position component
		e.edit().add(new Position());
		
		start = TimeUtils.timeSinceNanos(start);
		
		return e;
	}

	private void loadInternalProfiles(){
		FileHandle baseFile = Gdx.files.internal("data/entities/BaseProfiles.dat");
		
		if(baseFile.exists()){
			Array<JsonValue> data = j.fromJson(null, baseFile);
			
			for(JsonValue e: data){
				loadProfile(e);
			}
		}
	}
	
	private void loadCustomProfiles(){
		FileHandle entityDir = Gdx.files.external("adoGame/editor/entities/");
		
		if(entityDir.exists()){
			FileHandle[] list = entityDir.list("dat");
		
			for(FileHandle f: list){
				JsonValue profile = j.fromJson(null, f);
				
				loadProfile(profile);
			}
		}
	}
	
	public void loadProfile(JsonValue entryData){
		String[] idSplit = entryData.getString("baseid").split("\\.");
		
		if(!entityData.containsKey(idSplit[0])){
			entityData.put(idSplit[0], new ArrayMap<String, JsonValue>());
		}
		
		entityData.get(idSplit[0]).put(idSplit[1], entryData);
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
	
	public static ArrayMap<String, ArrayMap<String, JsonValue>> getEntityData() {
		return entityData;
	}
}
