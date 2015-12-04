package com.ado.trader.gui.editor;

import com.ado.trader.gui.GuiUtils;
import com.ado.trader.systems.AiSystem;
import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/*
 * Ai component entry in entity editor. Not to be confused with AiEntry, which is part of the behaviour tree/AI editor.
 */
public class AiComponentEntry extends ComponentEntry {
	SelectBox<String> aiBox;

	public AiComponentEntry(EntityEditor editor, JsonValue componentProfile) {
		super(editor, componentProfile);
		
		JsonValue inputType = componentProfile.get("input");
		
		aiBox = GuiUtils.createSelectBox(editor.gameRes.getSkin(), editor.gameRes.getFont());
		
		Array<String> aiList = new Array<String>();
		AiSystem aiSys = GameServices.getWorld().getSystem(AiSystem.class);
		for(String s: aiSys.getAllAiProfiles().keys()){
			aiList.add(s);
		}
		aiBox.setItems(aiList);
		aiBox.setName(inputType.getString("name"));
		add(aiBox);
	}

	@Override
	public void save(Json writer) {
		writer.writeValue(label.getName(), aiBox.getSelected());
	}

	@Override
	public void load(JsonValue entityData) {
		aiBox.setSelected(entityData.asString());
	}
}
