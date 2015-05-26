package com.ado.trader.gui;

import com.ado.trader.entities.components.Name;
import com.ado.trader.items.Description;
import com.ado.trader.items.Farmable;
import com.ado.trader.items.Food;
import com.ado.trader.items.Tool;
import com.ado.trader.items.Value;
import com.ado.trader.utils.GameServices;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

@Wire
public class ItemWindow extends BasicWindow {
	ComponentMapper<Name> nameMap;
	ComponentMapper<Food> foodMap;
	ComponentMapper<Tool> toolMap;
	ComponentMapper<Farmable> farmMap;
	ComponentMapper<Description> descMap;
	ComponentMapper<Value> valueMap;
	
	BitmapFont font;
	
	public ItemWindow(GameServices guiRes){
		super("Item Info", 180, 100, guiRes.getFont(), guiRes.getSkin(), guiRes.getStage());
		font = guiRes.getFont();
	}
	public void showWindow(float x, float y, Entity e){
		addLabelPair("Name: ", nameMap.get(e).getName(), font);
		if(foodMap.has(e)){
			addLabelPair("Food: ", ""+foodMap.get(e).value, font);
		}
		if(toolMap.has(e)){
			addLabelPair("Tool: ", toolMap.get(e).current +"/"+ toolMap.get(e).max, font);
		}
		if(farmMap.has(e)){
			addLabelPair("Farmable: ", "true", font);
		}
		if(valueMap.has(e)){
			addLabelPair("Value: ", ""+valueMap.get(e).value, font);
		}
		if(descMap.has(e)){
			addLabelPair("Description: ", descMap.get(e).description, font);
		}
		
		super.showWindow(x, y);
	}
	public void hideWindow(){
		super.hideWindow();
		root.clearChildren();
	}
}
