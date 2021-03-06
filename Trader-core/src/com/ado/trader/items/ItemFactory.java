package com.ado.trader.items;

import com.ado.trader.entities.components.Name;
import com.ado.trader.entities.components.SpriteComp;
import com.ado.trader.map.Chunk;
import com.ado.trader.utils.GameServices;
import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class ItemFactory {
	static ComponentMapper<Name> nameMap;
	static ComponentMapper<SpriteComp> spriteMap;
	static ComponentMapper<Value> valueMap;
	static ComponentMapper<Food> foodMap;
	static ComponentMapper<Tool> toolMap;
	static ComponentMapper<Description> descMap;
	
	static GroupManager groupManager;
	
	static ArrayMap<String, JsonValue> itemData;
	static ArrayMap<String, Archetype> itemProfiles;
	static ArrayMap<String, Sprite> itemSprites;
	
	static World world;
	
	public ItemFactory(String filePath, GameServices gameRes){
		itemData = new ArrayMap<String, JsonValue>();
		itemProfiles = new ArrayMap<String, Archetype>();
		itemSprites = new ArrayMap<String, Sprite>();
		
		world  = gameRes.getWorld();
		
		nameMap = world.getMapper(Name.class);
		spriteMap = world.getMapper(SpriteComp.class);
		valueMap = world.getMapper(Value.class);
		foodMap = world.getMapper(Food.class);
		toolMap = world.getMapper(Tool.class);
		descMap = world.getMapper(Description.class);
		groupManager = world.getManager(GroupManager.class);
		
		init(filePath, gameRes.getAtlas());
	}
	
	private void init(String filePath, TextureAtlas atlas){
		Json json = new Json();
		JsonValue file = json.fromJson(null, Gdx.files.internal(filePath));
		file = file.child;
		
		for(JsonValue i = file.child; i != null; i = i.next){
			
			ArchetypeBuilder item = new ArchetypeBuilder();

			for(JsonValue d = i.child; d != null; d = d.next){
				switch(d.name){
				case "sprite":
					createSprite(i.getString("name"), d.asString(), atlas);
					break;
				
				case "name":
					item.add(Name.class);
					break;
				
				case "value":
					item.add(Value.class);
					break;
				
				case "food":
					item.add(Food.class);
					break;
					
				case "tool":
					item.add(Tool.class);
					break;
					
				case "farmable":
//					addFarmProfile(farmProfiles, template.get("id"), template.get(key));
					item.add(Farmable.class);
					break;
				case "desc":
					item.add(Description.class);
					break;	
				}
			}
			itemProfiles.put(i.getString("name"), item.build(world));
			itemData.put(i.getString("name"), i);
		}
	}
	
	public static Entity createItem(String name){
		Entity i = world.createEntity(itemProfiles.get(name));
 
		JsonValue data = itemData.get(name);
		
		for(JsonValue d = data.child; d != null; d = d.next){
			switch(d.name){
			case "name":
				nameMap.get(i).setName(d.asString());
				break;
			case "value":
				valueMap.get(i).value = d.asInt();
				break;
			case "food":
				foodMap.get(i).value = d.asInt();
				break;
			case "tool":
				toolMap.get(i).init(d.asInt());
				break;
			case "desc":
				descMap.get(i).description = d.asString();
				break;
			}
		}
		return i;
	}
	
	public static void deleteItem(Chunk chunk, int tileX, int tileY, String name){
		for(int c = 0; c < chunk.getItems().map[tileX][tileY].length; c++){
			if(chunk.getItems().map[tileX][tileY][c] == null) continue;
			Entity e = world.getEntity(chunk.getItems().map[tileX][tileY][c]);
			if(nameMap.get(e).getName().matches(name)){
				chunk.getItems().map[tileX][tileY][c] = null;
			}
			e.deleteFromWorld();
		}
		
	}
	
	private void addFarmProfile(ArrayMap<String, ArrayMap<String, Integer>> farmProfiles, String id, String values){
		ArrayMap<String, Integer> data = new ArrayMap<String, Integer>();
		String[] list = values.split(",");
		data.put("stage1", Integer.valueOf(list[0]));
		data.put("stage2", Integer.valueOf(list[1]));
		data.put("growTime", Integer.valueOf(list[2]));
		data.put("maxHarvest", Integer.valueOf(list[3]));
		farmProfiles.put(id, data);
	}
	
	private void createSprite(String itemName, String spriteName, TextureAtlas atlas){
		Sprite sprite = atlas.createSprite(spriteName);
		sprite.scale(1);
		itemSprites.put(itemName, sprite);
	}
	public static ArrayMap<String, JsonValue> getItemData() {
		return itemData;
	}
	public static ArrayMap<String, Archetype> getItemProfiles() {
		return itemProfiles;
	}
	public static ArrayMap<String, Sprite> getItemSprites() {
		return itemSprites;
	}
	
}
