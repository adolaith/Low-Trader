package com.ado.trader.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

public class ToolTip extends Label{
	final int delay = 1; //sec
	boolean started;

	public ToolTip(LabelStyle style, Skin skin, Stage stage) {
		super("", style);
		setName("tooltip");
		setAlignment(Align.topLeft);
		getStyle().background = skin.getDrawable("gui/tooltip");
		
		setWidth(185);
		setWrap(true);
		setVisible(false);
		
		stage.addActor(this);
	}
	public void show(String content){
		//if quickly moving between actors using TT, hide TT
		if(isVisible() && getColor().a < 1){
			setVisible(false);
			setColor(getColor().r, getColor().g, getColor().b, 1);
			clearActions();
		}else if(isVisible()){
			setText(content);
			
			autoSize();
			autoPosition();
			return;
		}
		
		setText(content);
		
		autoSize();
		autoPosition();
		
		addAction(Actions.sequence(Actions.delay(1), Actions.visible(true)));
	}
	private void autoSize(){
		int maxLength = 12;
		int textHeight = 24;
		if(getText().length <= maxLength){
			setHeight(textHeight);
		}else if(getText().length <= maxLength * 2){
			setHeight(textHeight * 2);
		}else if(getText().length <= maxLength * 3){
			setHeight(textHeight * 3);
		}
	}
	//default tooltip position = top right of parent actor
	private void autoPosition(){
		float x = Gdx.input.getX() + 6;
		float y = Gdx.input.getY();
		y = Gdx.graphics.getHeight() - y;
		//tooltip trying to display offscreen width ways
		if(x + getWidth() > getStage().getWidth() / 2){
			x = Gdx.input.getX() - getWidth() - 6;	
		}
		Vector2 tmp = getStage().screenToStageCoordinates(new Vector2(x, y));
		setPosition(tmp.x, tmp.y);
		
		setPosition(x, y);
	}
	public void hide(){
		//timer started but not finished
		if(isVisible()){
			addAction(Actions.sequence(Actions.alpha(0.75f), Actions.fadeOut(1.2f), Actions.visible(false), Actions.alpha(1)));
		}
	}
}

