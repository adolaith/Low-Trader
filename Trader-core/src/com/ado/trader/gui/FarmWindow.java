package com.ado.trader.gui;

import com.ado.trader.map.FarmZone;
import com.ado.trader.screens.GameScreen;
import com.ado.trader.systems.FarmSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

public class FarmWindow extends BasicWindow {
	
	public FarmWindow(Gui gui) {
		super("Farm", 250, 30*3, gui);
	}
	private void setupFarmInfo(final FarmZone z, final GameScreen game){
		FarmSystem fSys = game.getWorld().getSystem(FarmSystem.class);
		
		ArrayMap<String, Integer> profile = fSys.getProfile(z.itemName);
		
		BitmapFont font = game.getGui().font;
		Skin skin = game.getGui().skin;
		
		if(!z.itemName.isEmpty()){
			updateSize(250, 30*4);
		}else{
			updateSize(250, 30*3);
		}
		
		addLabelPair("Growing:", z.itemName, font);
		
		//farm is not empty
		if(!z.itemName.isEmpty()){
			addLabelPair("Progress:", z.daysGrowing+"/"+profile.get("growTime"), font);
			int growth = profile.get("maxHarvest")*z.getTileList().size;
			addLabelPair("Harvest:", ""+growth*Math.round(z.growScore/profile.get("growTime")), font);
			cropSelection(font, skin, z, game);
			return;
		}
		//select and set farm's crop
		cropSelection(font, skin, z, game);
	}
	private void cropSelection(BitmapFont font, Skin skin, final FarmZone z, final GameScreen game){
		Table cropSelect = new Table();
		cropSelect.defaults().left();
		
		LabelStyle ls = new LabelStyle(font, Color.WHITE);
		Label l = new Label("New crop ", ls);
		cropSelect.add(l).left();
		
		//select box for zone type goes here
		ScrollPaneStyle styleScroll = new ScrollPaneStyle();
		styleScroll.vScroll = skin.getDrawable("gui/scrollBar");
		styleScroll.vScrollKnob = skin.getDrawable("gui/scrollBar");
		
		ListStyle styleList = new ListStyle(font, Color.YELLOW, Color.GRAY, 
				skin.getDrawable("gui/guiBG"));
		styleList.background = skin.getDrawable("gui/guiBG");
		
		SelectBoxStyle styleSelect = new SelectBoxStyle(font, Color.BLACK,
				skin.getDrawable("gui/guiBG"), styleScroll, styleList);
		
		final SelectBox<String> box = new SelectBox<String>(styleSelect);
		Array<String> list = new Array<String>();
		
		for(String s: game.getWorld().getSystem(FarmSystem.class).getProfiles().keys()){
			list.add(s);
		}
		box.setItems(list);
		
		TextButtonStyle bStyle = new TextButtonStyle();
		bStyle.font = font;
		bStyle.up = skin.getDrawable("gui/button");
		TextButton b = new TextButton("Set", bStyle);
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				z.itemName = box.getSelected();
				root.clearChildren();
				setupFarmInfo(z, game);
			}
		});
		
		cropSelect.add(box).height(24);
		cropSelect.add(b);
		
		root.add(cropSelect).padBottom(2).colspan(2).left().row();
	}
	public void showWindow(float x, float y, FarmZone z, GameScreen game){
		setupFarmInfo(z, game);	
		super.showWindow(x, y);
	}
	public void hideWindow(){
		super.hideWindow();
		root.clearChildren();
	}
}
