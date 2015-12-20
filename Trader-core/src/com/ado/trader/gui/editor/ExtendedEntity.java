package com.ado.trader.gui.editor;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.gui.GuiUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class ExtendedEntity extends ComponentEntry {
	SelectBox<String> box;
	ArrayMap<String, String> nameToIdMap;

	public ExtendedEntity(EntityEditor editor, JsonValue componentProfile) {
		super(editor, componentProfile);

		JsonValue inputType = componentProfile.get("input");		
		
		box = GuiUtils.createSelectBox(editor.gameRes.getSkin(), editor.gameRes.getFont());
		nameToIdMap = new ArrayMap<String, String>();
		
		Array<String> entities = new Array<String>();
		
		ArrayMap<String, ArrayMap<String, JsonValue>> list = EntityFactory.getEntityData();
		
		for(String typeId: list.keys()){
			for(JsonValue data: list.get(typeId).values()){
				String name = data.getString("name");
				entities.add(name);
				nameToIdMap.put(name, data.getString("baseid"));
			}
		}
		
		box.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				//needs to set checkboxes
				editor.markCheckBoxesSelected(nameToIdMap.get(box.getSelected()));
				
				editor.disableCheckboxes(true);
			}
		});
		
		delete.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				editor.disableCheckboxes(false);
			}
		});
		
		box.setItems(entities);
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
