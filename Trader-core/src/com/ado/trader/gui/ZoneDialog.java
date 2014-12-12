package com.ado.trader.gui;

import com.ado.trader.map.Zone.ZoneType;
import com.ado.trader.utils.placement.PlacementManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class ZoneDialog extends BasicWindow{
	public Array<Vector2> area;
	int nextId;

	public ZoneDialog(final Gui gui) {
		super("New Zone", 190, 110, gui);
		nextId = gui.game.getMap().getNextZoneId();
		
		LabelStyle ls = new LabelStyle(gui.font, Color.BLUE);
		Label l = new Label("Select zone type:", ls);
		root.add(l).colspan(2).height(18).row();
		
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
		ls = new LabelStyle(gui.font, Color.WHITE);
		for(ZoneType t: ZoneType.values()){
			list.add(t.name());
		}
		box.setItems(list);
		root.add(box).colspan(2).height(24).center().padBottom(2).row();
		
		TextButtonStyle bStyle = new TextButtonStyle();
		bStyle.font = gui.font;
		bStyle.up = gui.skin.getDrawable("gui/button");
		TextButton b = new TextButton("Ok", bStyle);
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				PlacementManager pm = gui.game.getPlaceManager(); 
				if(area!=null){
					pm.getZonePl().createNewZone(nextId, area, ZoneType.valueOf(box.getSelected()), gui.game.getMap().getCurrentLayerGroup());
					area = null;
				}else{
					pm.getZonePl().createNewZone(nextId, gui.game.getInput().mapClicked, 
							ZoneType.valueOf(box.getSelected()), gui.game.getMap().getCurrentLayerGroup());
				}
				nextId++;
				hideWindow();
			}
		});
		root.add(b).width(38).height(24).right();
		b = new TextButton("Cancel", bStyle);
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				hideWindow();
			}
		});
		root.add(b).width(b.getText().length()*12).height(24).left();
	}
	public void showDialog(float x, float y){
		super.showWindow(x, y);
	}
	public void setNextId(int nextId){
		this.nextId = nextId;
	}
}
