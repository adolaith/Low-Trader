package com.ado.trader.gui;

import com.ado.trader.map.WorkZone;
import com.ado.trader.systems.AiSystem;
import com.ado.trader.utils.IsoUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class WorkZoneWindow extends BasicWindow {
	WorkZone z;
	float x, y;

	public WorkZoneWindow(Gui gui) {
		super("New work tile", 292, 84, gui);
		setupWindow(gui);
	}
	
	public void setupWindow(Gui gui){
		LabelStyle ls = new LabelStyle(gui.font, Color.WHITE);
		Label l = new Label("Select ai profile:", ls);
		root.add(l).left().row();
		
		//select box for zone type goes here
		ScrollPaneStyle styleScroll = new ScrollPaneStyle();
		styleScroll.vScroll = gui.skin.getDrawable("gui/scrollBar");
		styleScroll.vScrollKnob = gui.skin.getDrawable("gui/scrollBar");

		ListStyle styleList = new ListStyle(gui.font, Color.YELLOW, Color.GRAY, 
				gui.skin.getDrawable("gui/guiBG"));
		styleList.background = gui.skin.getDrawable("gui/guiBG");

		SelectBoxStyle styleSelect = new SelectBoxStyle(gui.font, Color.BLACK,
				gui.skin.getDrawable("gui/guiBG"), styleScroll, styleList);

		final SelectBox<String> box = new SelectBox<String>(styleSelect);
		Array<String> list = new Array<String>();
		
		AiSystem aiSys = gui.game.getWorld().getSystem(AiSystem.class);
		for(String s: aiSys.getAllAiProfiles().keys()){
			if(s.startsWith("work")){
				list.add(s.substring(s.indexOf('k') + 1));
			}
		}
		box.setItems(list);
		root.add(box).height(24).left();
		
		TextButtonStyle bStyle = new TextButtonStyle();
		bStyle.font = gui.font;
		bStyle.up = gui.skin.getDrawable("gui/button");
		TextButton b = new TextButton("Set work", bStyle);
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				z.addWorkTile(new Vector2(x, y), "work" + box.getSelected());
				hideWindow();
			}
		});
		root.add(b).left();
	}
	
	public void showWindow(int x, int y, int width, int height, WorkZone z){
		Vector2 tmp = IsoUtils.getIsoXY(x, y, width, height);
		super.showWindow(tmp.x, tmp.y);
		this.z = z;
		this.x = x;
		this.y = y;
	}
	public void hideWindow(){
		super.hideWindow();
		z = null;
	}

}
