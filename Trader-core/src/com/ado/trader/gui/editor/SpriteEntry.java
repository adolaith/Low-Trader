package com.ado.trader.gui.editor;

import com.ado.trader.gui.GuiUtils;
import com.ado.trader.rendering.SpriteManager;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class SpriteEntry extends ComponentEntry {
	SelectBox<String> spriteBox;

	public SpriteEntry(EntityEditor editor, JsonValue componentProfile) {
		super(editor, componentProfile);
		
		JsonValue inputType = componentProfile.get("input");
		
		spriteBox = GuiUtils.createSelectBox(editor.gameRes.getSkin(), editor.gameRes.getFont());
		
		Array<String> sprites = new Array<String>();
		SpriteManager spriteMan = editor.gameRes.getRenderer().getRenderEntitySystem().getSpriteManager();
		
		//load sprite name lists
		loadSprites(spriteMan.getEntitySprites().keys(), sprites);
		loadSprites(spriteMan.getItemSprites().keys(), sprites);
		loadSprites(spriteMan.getWallSprites().keys(), sprites);
		loadSprites(spriteMan.getFeatureSprites().keys(), sprites);
		
		spriteBox.setItems(sprites);
		spriteBox.setName(inputType.getString("name"));
		add(spriteBox).padRight(4);
	
	}
	
	private void loadSprites(ArrayMap.Keys<String> list, Array<String> editorList){
		for(String s: list){
			editorList.add(s);
		}
	}

	@Override
	public void save(Json writer) {
		writer.writeArrayStart(label.getName());
		writer.writeValue(spriteBox.getSelected());
		writer.writeArrayEnd();
	}

	@Override
	public void load(JsonValue entityData) {
		String[] spriteData = entityData.asStringArray();
		spriteBox.setSelected(spriteData[0]);
	}
	
	public SelectBox<String> getSelectBox(){
		return spriteBox;
	}

}
