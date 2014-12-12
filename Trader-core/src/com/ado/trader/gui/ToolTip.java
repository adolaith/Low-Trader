package com.ado.trader.gui;

import com.ado.trader.screens.GameScreen;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class ToolTip {
	Label l;
	String content;
	GameScreen game;
	final int TEXTHEIGHT;

	public ToolTip(Gui root) {
		game = root.game;
		TEXTHEIGHT = 18;
		
		LabelStyle lStyle = new LabelStyle();
		lStyle.background = root.skin.getDrawable("gui/guiBG");
		lStyle.font = root.font;
		
		l = new Label("", lStyle);
		l.setWidth(115);
		l.setVisible(false);
		root.stage.addActor(l);
	}
	
	public void updateToolTip(){
		if(content==null){return;}
		if(!l.isVisible()){
			l.setText(content);
			l.setPosition(game.getInput().getMousePos().x, game.getInput().getMousePos().y);
			l.setVisible(true);
		}
	}
	public void setContent(String content){
		this.content = content;
		int h = content.split("/n").length;
		l.setHeight(TEXTHEIGHT*h+1);
	}

	public Label getLabel() {
		return l;
	}
	public void exitParent(){
		content = null;
		l.setVisible(false);
	}
}

