package com.ado.trader.entities;

import com.badlogic.gdx.utils.JsonValue;

public class EntityProfile {
	JsonValue profile;

	public EntityProfile(JsonValue profile) {
		this.profile = profile;	
	}

	public String getId(){
		return profile.getString("baseid");
	}
	public String getName(){
		return profile.getString("name");
	}
	public String getSprite(){
		if(profile.has("sprite")){
			return profile.getString("sprite");
		}
		return null;
	}
	public String getAnim(){
		if(profile.has("anim")){
			return profile.getString("anim");
		}
		return null;
	}
	public JsonValue getProfileJson(){
		return profile;
	}
}
