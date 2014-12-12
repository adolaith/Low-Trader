package com.ado.trader.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

public class NewsWindow extends BasicWindow {
	Array<Label> labels;

	public NewsWindow(Gui gui) {
		super("News window", 600, 150, gui);
		labels = new Array<Label>();
		
		Table t = new Table();
		t.setWidth(root.getWidth());
		t.setHeight(root.getHeight());
		t.left();
		LabelStyle ls = new LabelStyle(gui.font, Color.WHITE);
		for(int i=0;i<=15;i++){
			Label l = new Label("", ls);
			labels.add(l);
			t.add(l).height(16).row();
		}
		
		ScrollPaneStyle spS = new ScrollPaneStyle();
		spS.vScroll = gui.skin.getDrawable("gui/scrollBar");
		spS.vScrollKnob = gui.skin.getDrawable("gui/scrollBar");
		
		ScrollPane sP = new ScrollPane(t, spS);
		sP.setScrollingDisabled(true, false);
		sP.setScrollBarPositions(false, true);
		root.add(sP).width(root.getWidth()).height(root.getHeight());
	}
	
	public void newMessage(String text){
		String str1 = "";
		String str2 = text;
		
		for(int i=0;i<labels.size;i++){
			Label l = labels.get(i);
			if(i % 2 == 0){
				str1 = l.getText().toString();
				l.setText(str2);
			}else{
				str2 = l.getText().toString();
				l.setText(str1);
			}
		}
	}

}
