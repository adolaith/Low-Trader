package com.ado.trader.gui;

import com.ado.trader.items.Item;
import com.ado.trader.items.ItemData;
import com.ado.trader.items.ItemPosition;
import com.ado.trader.items.ItemSprite;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class ItemWindow extends BasicWindow {
	BitmapFont font;
	
	public ItemWindow(GameServices guiRes){
		super("Item Info", 180, 100, guiRes.font, guiRes.skin, guiRes.stage);
		font = guiRes.font;
	}
	public void showWindow(float x, float y, Item i){
		super.showWindow(x, y);
		updateSize(180, 26*i.getAllData().size);
		addLabelPair("Name: ", i.getId(), font);
		for(ItemData d: i.getAllData()){
			if(d instanceof ItemSprite || d instanceof ItemPosition)continue;
			addLabelPair(d.getComponentName()+": ", String.valueOf(d.value), font);
		}
	}
	public void hideWindow(){
		super.hideWindow();
		root.clearChildren();
	}
}
