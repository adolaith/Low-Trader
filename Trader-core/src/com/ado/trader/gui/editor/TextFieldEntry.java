package com.ado.trader.gui.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter.DigitsOnlyFilter;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class TextFieldEntry extends ComponentEntry {
	TextField field;

	public TextFieldEntry(EntityEditor editor, JsonValue componentProfile) {
		super(editor, componentProfile);
		
		JsonValue inputType = componentProfile.get("input");
		
		field = new TextField("", editor.tfStyle);
		
		if(inputType.has("eg")){
			field.setMessageText(inputType.getString("eg"));
		}
		if(inputType.getString("type").matches("intfield")){
			field.setTextFieldFilter(new DigitsOnlyFilter());
		}
		
		field.setName(inputType.getString("name"));
		
		add(field).width(editor.getWidth() * 0.56f).padRight(4);
		
		if(componentProfile.name.matches("baseid")){
			field.setDisabled(true);
			
			TextFieldStyle style = new TextFieldStyle(field.getStyle());
			style.fontColor = Color.WHITE;
			field.setStyle(style);
			
			removeActor(delete);
		}else if(componentProfile.name.matches("name")){
			removeActor(delete);
		}
	}

	@Override
	public void save(Json writer) {
		if(field.getText().contains(",")){
			String[] split = field.getText().split("\\,");
			
			writer.writeArrayStart(label.getName());
			
			for(String s: split){
				writer.writeValue(s);
			}
			
			writer.writeArrayEnd();
		}else{
			writer.writeValue(label.getName(), field.getText());	
		}
		
	}

	@Override
	public void load(JsonValue entityData) {
		if(entityData.isArray()){
			String groups = "";
			for(String s: entityData.asStringArray()){
				groups += s + ",";
			}
			field.setText(groups);
		}else{
			field.setText(entityData.asString());
		}
		
	}
	public TextField getTextField(){
		return field;
	}
}
