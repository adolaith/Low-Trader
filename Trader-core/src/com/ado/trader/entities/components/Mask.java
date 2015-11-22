package com.ado.trader.entities.components;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

//sprite mask for windows in walls, wall mask, doorway mask
public class Mask extends SerializableComponent {
	public int maskIndex;
	public String maskName;
	
	@Override
	public void save(Json writer) {
		writer.writeArrayStart("mask");
		writer.writeValue(maskName);
		writer.writeValue(maskIndex);
		writer.writeArrayEnd();
	}
	
	@Override
	public void load(JsonValue data) {
		String[] list = data.asStringArray();
		this.maskName = list[0];
		this.maskIndex = Integer.valueOf(list[1]);
	}
}
