package com.ado.trader.gui;

import com.ado.trader.input.InputHandler;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class ToolTip {
	Label l;
	String content;
	final int TEXTHEIGHT;

	public ToolTip(GameServices gameRes) {
		TEXTHEIGHT = 18;
		
		LabelStyle lStyle = new LabelStyle();
		lStyle.background = gameRes.skin.getDrawable("gui/guiBG");
		lStyle.font = gameRes.font;
		
		l = new Label("", lStyle);
		l.setWidth(115);
		l.setVisible(false);
		gameRes.stage.addActor(l);
	}
	
	public void updateToolTip(){
		if(content==null){return;}
		if(!l.isVisible()){
			l.setText(content);
			l.setPosition(InputHandler.getMousePos().x, InputHandler.getMousePos().y);
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

