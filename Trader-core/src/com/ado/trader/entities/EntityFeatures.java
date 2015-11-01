package com.ado.trader.entities;

import com.ado.trader.entities.components.FeatureSprite;
import com.ado.trader.entities.components.Mask;
import com.ado.trader.entities.components.WallSprite;
import com.ado.trader.rendering.EntityRenderSystem;
import com.ado.trader.rendering.MaskingSystem;
import com.ado.trader.utils.FileLogger;
import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class EntityFeatures {
	ArrayMap<String, JsonValue> featuresList;
	MaskingSystem maskSys;
	
	public EntityFeatures(TextureAtlas atlas, EntityRenderSystem entityRenderer){
		FileLogger.writeLog("EntityFeatures: [INIT]");
		featuresList = loadProfiles();
		
		FileLogger.writeLog("EntityFeatures: profiles loaded");
		maskSys = entityRenderer.getMasks();
	}
	public void applyFeature(Entity e, String featureName, int spriteIndex){
		JsonValue feature = featuresList.get(featureName);
		for(JsonValue d = feature.child(); d != null; d = d.next()){
			switch(d.name){
			case "sprite":
				e.edit().add(new FeatureSprite(featureName, spriteIndex));
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
		WallSprite wC = e.getComponent(WallSprite.class);
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
	private ArrayMap<String, JsonValue> loadProfiles() {
		ArrayMap<String, JsonValue> profiles = new ArrayMap<String, JsonValue>();
		Json json = new Json();
		JsonValue list = json.fromJson(null, Gdx.files.internal("data/entities/Features"));
		list = list.child();

		//loop nodes
		for(JsonValue e = list.child(); e != null; e = e.next()){
			profiles.put(e.get("name").asString(), e);
		}
		
		return profiles;
	}
	public JsonValue getFeature(String name){
		return featuresList.get(name);
	}
	public ArrayMap<String, JsonValue> getFeaturesList() {
		return featuresList;
	}
}
