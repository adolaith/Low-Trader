package com.ado.trader.entities.components;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

//Contains the entity's spriteid's 
public class SpriteComp extends SerializableComponent {
	public String spriteName;
	public int spriteIndex;
	
	public SpriteComp() {
	}

	@Override
	public void save(Json writer) {
		writer.writeArrayStart("sprite");
		writer.writeValue(spriteName);
		writer.writeValue(spriteIndex);
		writer.writeArrayEnd();
	}

	@Override
	public void load(JsonValue data) {
		String[] list = data.asStringArray();
		
		this.spriteName = list[0];
		
		if(list.length > 1){
			this.spriteIndex = Integer.valueOf(list[1]);
		}else{
			this.spriteIndex = 0;
		}
	}
}
