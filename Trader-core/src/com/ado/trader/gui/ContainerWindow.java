package com.ado.trader.gui;

import com.ado.trader.entities.components.Inventory;
import com.ado.trader.entities.components.Name;
import com.ado.trader.items.ItemFactory;
import com.ado.trader.utils.GameServices;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

@Wire
public class ContainerWindow extends BasicWindow {
	ComponentMapper<Name> nameMap;
	BitmapFont font;
	Skin skin;
	World world;

	public ContainerWindow(GameServices guiRes) {
		super("Container", 192, 3 * 26, guiRes.getFont(), guiRes.getSkin(), guiRes.getStage());
		font = guiRes.getFont();
		skin = guiRes.getSkin();
		world = guiRes.getWorld();
		root.left();
	}
	public void showWindow(float x, float y, Inventory i){
		super.showWindow(x, y);
		updateSize(192, (i.getItems().size / 4 + 3) * 26);
		for(int j = 0; j < i.getItems().size; j++){
			if(i.getItems().get(j) == null) continue;
			Entity item = world.getEntity(i.getItems().get(j));
			
			Sprite s = ItemFactory.getItemSprites().get(nameMap.get(item).getName());
			Image img = new Image(s);
			
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
