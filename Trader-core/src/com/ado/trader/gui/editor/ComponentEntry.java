package com.ado.trader.gui.editor;

import com.ado.trader.gui.GuiUtils;
import com.ado.trader.gui.ToolTip;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/*
 * Base class for component entries used in the EntityEditor class
 * extend and add input elements
 */
public abstract class ComponentEntry extends Table {
	Label label;
	ImageButton delete;
	
	public ComponentEntry(EntityEditor editor, JsonValue componentProfile){
		padBottom(3);
		
		if(!componentProfile.name.matches("name") && !componentProfile.name.matches("baseid")){
			delete = GuiUtils.createImageButton("gui/exitIcon", null, "gui/button", null, editor.gameRes.getSkin());
			delete.addListener(new ClickListener(){
				public void clicked (InputEvent event, float x, float y) {
					clear();
					editor.scroll.removeActor(ComponentEntry.this);
				}
			});
			add(delete).padRight(4).size(24);
		}
		
		label = new Label(componentProfile.name, editor.lStyle);
		
		label.setName(componentProfile.name);
		
		label.addListener(new ClickListener(){
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				ToolTip tt = getStage().getRoot().findActor("tooltip");
				tt.show(componentProfile.getString("desc"));
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
				ToolTip tt = getStage().getRoot().findActor("tooltip");
				tt.hide();
			}
		});
		add(label).expandX().fillX().padLeft(4);
	}
	
	public Label getLabel(){
		return label;
	}
	
	abstract public void save(Json writer);
	abstract public void load(JsonValue entityData);	

}
