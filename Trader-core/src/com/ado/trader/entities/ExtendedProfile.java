package com.ado.trader.entities;

import com.badlogic.gdx.utils.JsonValue;

public class ExtendedProfile extends EntityProfile {
	JsonValue extProfile;

	public ExtendedProfile(JsonValue profile, JsonValue extProfile) {
		super(profile);
		this.extProfile = extProfile;
	}
	
	@Override
	public String getName(){
		return extProfile.getString("name");
	}
	
	@Override
	public String getId(){
		return extProfile.getString("baseid");
	}
	@Override
	public String getSprite(){
		if(extProfile.has("sprite")){
			return extProfile.getString("sprite");
		}else if(profile.has("sprite")){
			return profile.getString("sprite");
		}
		return null;
	}
	@Override
	public String getAnim(){
		if(extProfile.has("anim")){
			return extProfile.getString("anim");
		}else if(profile.has("anim")){
			return profile.getString("anim");
		}
		return null;
	}
	public JsonValue getExtendedJson(){
		return extProfile;
	}
}
