package com.ado.trader.gui;

import com.ado.trader.entities.components.Inventory;
import com.ado.trader.items.Item;
import com.ado.trader.items.ItemSprite;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ContainerWindow extends BasicWindow {
	BitmapFont font;
	Skin skin;

	public ContainerWindow(Gui gui) {
		super("Container", 192, 3 * 26, gui);
		font = gui.font;
		skin = gui.skin;
		root.left();
	}
	public void showWindow(float x, float y, Inventory i){
		super.showWindow(x, y);
		updateSize(192, (i.getItems().size / 4 + 3) * 26);
		for(int j = 0; j < i.getItems().size; j++){
			Item item = i.getItems().get(j);
			
			if(item == null) continue;
			
			ItemSprite sprite = item.getData(ItemSprite.class);
			Image img = new Image(sprite.sprite);
			
			if((j + 1) % 4 == 0){
				root.add(img).width(32).height(32).row();
			}else{
				root.add(img).width(32).height(32);
			}
		}
	}
	public void hideWindow(){
		super.hideWindow();
		root.clearChildren();
	}
}
