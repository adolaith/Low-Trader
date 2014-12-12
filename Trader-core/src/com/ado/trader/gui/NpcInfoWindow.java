package com.ado.trader.gui;

import com.ado.trader.entities.components.Health;
import com.ado.trader.entities.components.Hunger;
import com.ado.trader.entities.components.Inventory;
import com.ado.trader.entities.components.Money;
import com.ado.trader.items.Item;
import com.ado.trader.items.ItemSprite;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;


public class NpcInfoWindow extends BasicWindow {
	BitmapFont font;
	Skin skin;

	public NpcInfoWindow(Gui gui) {
		super("Npc Info", 265, 165, gui);
		font = gui.font;
		skin = gui.skin;
	}
	
	public void showWindow(float x, float y, Entity e){
		super.showWindow(x, y);
		setupWindow(e);
	}
	public void hideWindow(){
		super.hideWindow();
		root.clearChildren();
	}
	private void setupWindow(Entity e){
		addLabelPair("Name(ID): ", ""+e.getId(), font);
		Health hp = e.getComponent(Health.class);
		addLabelPair("Health: ", hp.value+"/"+hp.max, font);
		Hunger h = e.getComponent(Hunger.class);
		addLabelPair("Hunger: ", h.value+"/"+h.max, font);
		Money m = e.getComponent(Money.class);
		addLabelPair("Money: ", ""+m.value, font);
		Inventory i = e.getComponent(Inventory.class);
		Table t = new Table();
		t.setBackground(skin.getDrawable("gui/bGround"));
		for(int x = 0; x < i.getItems().size; x++){
			Item item = i.getItems().get(x);
			if(item == null) continue;
			
			ItemSprite sprite = item.getData(ItemSprite.class);
			Image img = new Image(sprite.sprite);
			if((x + 1) % 4 == 0){
				t.add(img).width(32).height(32).row();
			}else{
				t.add(img).width(32).height(32);
			}
		}
		root.add(t).padTop(4).colspan(2).width(width-width/5).height(height-height/2-36);
	}
}
