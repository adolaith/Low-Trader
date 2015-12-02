package com.ado.trader.gui.editor;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.gui.GuiUtils;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class AnimationEntry extends ComponentEntry {
	SelectBox<String> box;

	public AnimationEntry(EntityEditor editor, JsonValue componentProfile) {
		super(editor, componentProfile);

		JsonValue inputType = componentProfile.get("input");
		
		box = GuiUtils.createSelectBox(editor.gameRes.getSkin(), editor.gameRes.getFont());
		
		Array<String> anims = new Array<String>();
		for(String s: EntityFactory.getAnimationPool().keys()){
			anims.add(s);
		}
		box.setItems(anims);
		box.setName(inputType.getString("name"));
		add(box);
	}

	@Override
	public void save(Json writer) {
		writer.writeValue(box.getName(), box.getSelected());
	}

	@Override
	public void load(JsonValue entityData) {
		box.setSelected(entityData.asString());
	}
}
