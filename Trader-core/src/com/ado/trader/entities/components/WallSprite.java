package com.ado.trader.entities.components;

import com.ado.trader.entities.WallDirection;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class WallSprite extends SerializableComponent {
	public String spriteName;
	public WallDirection firstSprite, secondSprite;
	
	@Override
	public void save(Json writer) {
		writer.writeObjectStart("sprite");
		
		writer.writeValue("name", spriteName);
		
		writer.writeArrayStart("dir");
		writer.writeValue(firstSprite);
		if(secondSprite != null){
			writer.writeValue(secondSprite);
		}
		
		writer.writeArrayEnd();
		
		writer.writeObjectEnd();			
	}
	
	@Override
	public void load(JsonValue data) {
		if(data.isArray()){
			spriteName = data.asStringArray()[0];
		}else{
			spriteName = data.getString("name");
			
			String[] dirs = data.get("dir").asStringArray();
			
			firstSprite = WallDirection.valueOf(dirs[0]);
			
			if(dirs.length > 1){
				secondSprite = WallDirection.valueOf(dirs[1]);
			}
		}
	}
}
