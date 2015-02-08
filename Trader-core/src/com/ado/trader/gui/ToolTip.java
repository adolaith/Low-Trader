package com.ado.trader.gui;

import com.ado.trader.input.InputHandler;
import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class ToolTip extends Actor{
	final int delay = 1; //sec
	float count; 
	boolean started;
	Label label;

	public ToolTip(GameServices gameRes) {
		setName("tooltip");
		label = new Label("", new LabelStyle(gameRes.getFont(), Color.BLACK));
		label.setAlignment(Align.topLeft);
		label.getStyle().background = gameRes.getSkin().getDrawable("gui/tooltip");
		
		label.setWidth(185);
		label.setWrap(true);
		label.setVisible(false);
		
		count = -1;
		
		gameRes.getStage().addActor(label);
		
		gameRes.getStage().addActor(this);
	}
	public void show(String content){
		//if quickly moving between actors using TT, hide TT
		if(label.isVisible() && label.getColor().a < 1){
			label.setVisible(false);
			label.clearActions();
		}
		if(started == true) return;
		started = true;
		count = 0;
		
		label.setText(content);
	}
	@Override
	public void act(float delta){
		super.act(delta);
		
		if(label.isVisible() && (InputHandler.getVelocity().x != 0 || InputHandler.getVelocity().y != 0)){
			label.moveBy(InputHandler.getVelocity().x, InputHandler.getVelocity().y);
		}
		if(started == false) return;
		
		count += Gdx.graphics.getRawDeltaTime();
		if(count >= delay){
			autoSize();
			autoPosition();
			label.toFront();
			label.setVisible(true);
			
			started = false;
			count = -1;
		}
	}
	public void autoSize(){
		int maxLength = 12;
		int textHeight = 24;
		if(label.getText().length <= maxLength){
			label.setHeight(textHeight);
		}else if(label.getText().length <= maxLength * 2){
			label.setHeight(textHeight * 2);
		}else if(label.getText().length <= maxLength * 3){
			label.setHeight(textHeight * 3);
		}
	}
	//default tooltip position = top right of parent actor
	private void autoPosition(){
		float x = InputHandler.getMousePos().x + 4;
		float y = InputHandler.getMousePos().y;
		//tooltip trying to display offscreen width ways
		if(x + label.getWidth() > label.getStage().getWidth() / 2){
			x = InputHandler.getMousePos().x - label.getWidth() - 4;	
		}
		label.setPosition(x, y);
	}
	public void hide(){
		//timer started but not finished
		if(started == true && count < delay){
			started = false;
		//tooltip visible
		}else if(started == false && label.isVisible()){
			label.addAction(Actions.sequence(Actions.alpha(0.75f), Actions.delay(2), Actions.alpha(0, 1), Actions.visible(false), Actions.alpha(1)));
		}
	}
}

