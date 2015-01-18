package com.ado.trader.items;

import com.ado.trader.GameMain;
import com.ado.trader.gui.GameServices;
import com.ado.trader.utils.FileParser;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

public class ItemFactory {
	static ArrayMap<String, ArrayMap<String, String>> itemProfiles;
	static ArrayMap<Integer, Sprite> itemSprites;
	
	public ItemFactory(String profileFileName, GameServices gameRes){
		itemProfiles = loadItemProfiles(profileFileName, gameRes.getAtlas(), gameRes.getParser());
	}
	public static Item createItem(String name){
		Item i = new Item(name);
		ArrayMap<String, String> profile = itemProfiles.get(name);
		for(String key: profile.keys()){
			switch(key){
			case "sprite":
				int spriteId = Integer.valueOf(profile.get(key));
				ItemSprite s = new ItemSprite(spriteId, new Sprite(itemSprites.get(spriteId)));
				i.addData(s);
				break;
			case "value":
				int value = Integer.valueOf(profile.get(key));
				ItemValue v = new ItemValue(value);
				i.addData(v);
				break;
			case "coins":
				int coins = Integer.valueOf(profile.get(key));
				Coins c = new Coins(coins);
				i.addData(c);
				break;
			case "food":
				int fValue = Integer.valueOf(profile.get(key));
				FoodItem f = new FoodItem(fValue);
				i.addData(f);
				break;
			case "tool":
				int tValue = Integer.valueOf(profile.get(key));
				ToolItem t = new ToolItem(tValue);
				i.addData(t);
				break;
			}
		}
		i.addData(new ItemPosition());
		return i;
	}
	private ArrayMap<String, ArrayMap<String,String>> loadItemProfiles(String fileName, TextureAtlas atlas, FileParser p){
		ArrayMap<String, ArrayMap<String,String>> items = new ArrayMap<String, ArrayMap<String,String>>();
		p.initParser(fileName, false, false);
		ArrayMap<Integer, Sprite> sprites = new ArrayMap<Integer, Sprite>();
		ArrayMap<String, ArrayMap<String, Integer>> farmProfiles = new ArrayMap<String, ArrayMap<String,Integer>>();
		Array<ArrayMap<String, String>> data = p.readFile();
		for(ArrayMap<String, String> template: data){
			ArrayMap<String,String> item = new ArrayMap<String, String>();
			for(String key: template.keys()){
				switch(key){
				case "sprite":
					createSprite(sprites, template.get(key), atlas);
					item.put(key, template.get(key).split("'")[0]);
					break;
				case "farmable":
					addFarmProfile(farmProfiles, template.get("id"), template.get(key));
					break;
				default:
					item.put(key, template.get(key));
				}
			}
			items.put(template.get("id"), item);
		}
		itemSprites = sprites;
//		game.getWorld().getSystem(FarmSystem.class).loadProfiles(farmProfiles);
		return items;
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
	private void createSprite(ArrayMap<Integer, Sprite> sprites, String list, TextureAtlas atlas){
		String[] tmp = list.split("'");
		try{
			Sprite sprite = atlas.createSprite(tmp[1]);
			sprite.scale(1f);
			sprites.put(Integer.valueOf(tmp[0]), sprite);
		}catch(Exception e){Gdx.app.log(GameMain.LOG, "Error loading entity sprites...:"+e);}
	}
	public ArrayMap<String, ArrayMap<String, String>> getItemProfiles() {
		return itemProfiles;
	}
	public ArrayMap<Integer, Sprite> getItemSprites() {
		return itemSprites;
	}
}
