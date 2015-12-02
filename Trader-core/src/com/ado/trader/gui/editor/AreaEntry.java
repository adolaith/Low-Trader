package com.ado.trader.gui.editor;

import com.ado.trader.gui.GuiUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter.DigitsOnlyFilter;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class AreaEntry extends ComponentEntry {
	SelectBox<String> areaBox;

	public AreaEntry(EntityEditor editor, JsonValue componentProfile) {
		super(editor, componentProfile);
		
		JsonValue inputType = componentProfile.get("input");

		areaBox = GuiUtils.createSelectBox(editor.gameRes.getSkin(), editor.gameRes.getFont());
		
		Array<String> areaList = new Array<String>();
		areaBox.setItems(areaList);
		areaBox.setName(inputType.getString("name"));
		
		add(areaBox).expandX().fillX().padRight(2);
		
		final TextField xField = new TextField("", editor.tfStyle);
		xField.setTextFieldFilter(new DigitsOnlyFilter());
		xField.setMessageText("x");
		add(xField).width(30).padRight(2);
		
		final TextField yField = new TextField("", editor.tfStyle);
		yField.setTextFieldFilter(new DigitsOnlyFilter());
		yField.setMessageText("y");
		add(yField).width(30).padRight(2);
		
		Button sub = new Button(editor.bStyle);
		Label l = new Label("Add", editor.lStyle);
		sub.add(l);
		sub.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				if(xField.getText().isEmpty() || yField.getText().isEmpty()) return;
				
				Array<String> list = new Array<String>(areaBox.getItems());
				list.add("[" + xField.getText() + "," + yField.getText() + "]");
				areaBox.setItems(list);
				xField.setText("");
				yField.setText("");
			}
		});
		add(sub);
	
	}

	@Override
	public void save(Json writer) {
		writer.writeArrayStart(areaBox.getName());
		for(String s: areaBox.getItems()){
			writer.writeValue(s);
		}
		writer.writeArrayEnd();
	}

	@Override
	public void load(JsonValue entityData) {
		String[] vecs = entityData.asStringArray();
		
		areaBox.setItems(vecs);		
	}
}
