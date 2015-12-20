package com.ado.trader.gui.editor;

import com.ado.trader.gui.GuiUtils;
import com.ado.trader.systems.AnimationSystem;
import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.brashmonkey.spriter.Entity;

public class AnimationEntry extends ComponentEntry {
	SelectBox<String> box;

	public AnimationEntry(EntityEditor editor, JsonValue componentProfile) {
		super(editor, componentProfile);

		JsonValue inputType = componentProfile.get("input");
		
		box = GuiUtils.createSelectBox(editor.gameRes.getSkin(), editor.gameRes.getFont());
		
		Array<String> anims = new Array<String>();
		AnimationSystem animSys = GameServices.getWorld().getSystem(AnimationSystem.class);
		
		for(Entity animEnt: animSys.getAnimationData().getAllEntities()){
			anims.add(animEnt.name);
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
