package com.ado.trader.entities;

import com.ado.trader.entities.components.Feature;
import com.ado.trader.entities.components.Mask;
import com.ado.trader.entities.components.Wall;
import com.ado.trader.rendering.EntityRenderSystem;
import com.ado.trader.rendering.EntityRenderSystem.Direction;
import com.ado.trader.rendering.MaskingSystem;
import com.ado.trader.utils.FileLogger;
import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class EntityFeatures {
	ArrayMap<String, JsonValue> featuresList;
	MaskingSystem maskSys;
	
	public EntityFeatures(TextureAtlas atlas, EntityRenderSystem entityRenderer){
		FileLogger.writeLog("EntityFeatures: [INIT]");
		featuresList = loadProfiles("data/Features", atlas, entityRenderer);
		FileLogger.writeLog("EntityFeatures: profiles loaded");
		maskSys = entityRenderer.getMasks();
	}
	public void applyFeature(Entity e, String featureName, int spriteIndex){
		JsonValue feature = featuresList.get(featureName);
		for(JsonValue d = feature.child(); d != null; d = d.next()){
			switch(d.name){
			case "sprite":
				e.edit().add(new Feature(featureName, spriteIndex));
				break;
			case "mask":
				String[] maskList = d.asStringArray();
				String maskName = maskList[0].substring(0, maskList[0].indexOf("_"));
				applyMask(e, maskName);
				break;
			}
		}
	}
	private void applyMask(Entity e, String maskName){
		Wall wC = e.getComponent(Wall.class);
		Mask m = new Mask();
		if(wC.firstSprite==Direction.NE||wC.firstSprite==Direction.SW){
			m.maskIndex = 1;
			m.maskName = maskName;
		}else if(wC.firstSprite==Direction.SE||wC.firstSprite==Direction.NW){
			m.maskIndex = 0;
			m.maskName = maskName;
		}
		e.edit().add(m);
	}
	private ArrayMap<String, JsonValue> loadProfiles(String fileName, TextureAtlas atlas, EntityRenderSystem entityRenderer) {
		ArrayMap<String, JsonValue> profiles = new ArrayMap<String, JsonValue>();
		Json json = new Json();
		JsonValue list = json.fromJson(null, Gdx.files.internal("data/Features"));
		list = list.child();
		ArrayMap<String, Sprite[]> sprites = new ArrayMap<String, Sprite[]>();

		//loop nodes
		for(JsonValue e = list.child(); e != null; e = e.next()){
			profiles.put(e.get("name").asString(), e);
			//loop node data
			for(JsonValue d = e.child; d != null; d = d.next){
				switch(d.name()){
				case "sprite":
					String[] s = d.asStringArray();
					Sprite[] featureSprites = new Sprite[4];
					for(int i = 0; i <= s.length; i += 2){
						Sprite sprite = atlas.createSprite(s[i]);
						sprite.scale(1f);
						featureSprites[i] = sprite;
						Sprite spriteFlip = atlas.createSprite(s[i]);
						spriteFlip.scale(1f);
						featureSprites[i + 1] = spriteFlip;
					}
					sprites.put(e.get("name").asString(), featureSprites);
					break;
				case "mask":
					//wall masks dont need flipped sprites. They are positioned according to the parent wall entity's direction and position
					String[] maskList = d.asStringArray();
					Sprite[] maskSprites = new Sprite[2];
					for(int i = 0; i <= maskList.length; i++){
						Sprite sprite = atlas.createSprite(maskList[i]);
						sprite.scale(1f);
						maskSprites[i] = sprite;
					}
					String maskName = maskList[0].substring(0, maskList[0].indexOf("_"));
					entityRenderer.getMasks().loadMaskSet(maskName, maskSprites);
					break;
				}
			}
		}
		
		entityRenderer.getSprites().putAll(sprites);
		return profiles;
	}
	public JsonValue getFeature(String name){
		return featuresList.get(name);
	}
	public ArrayMap<String, JsonValue> getFeaturesList() {
		return featuresList;
	}
}
