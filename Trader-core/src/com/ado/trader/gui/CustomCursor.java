package com.ado.trader.gui;

import com.ado.trader.input.InputHandler;
import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class CustomCursor extends Image{
	Skin skin;

	public CustomCursor(GameServices gameRes) {
		skin = gameRes.getSkin();
		setName("customCursor");
		setSize(28, 28);
		gameRes.getStage().addActor(this);
		hide();
	}
	
	@Override
	public void act(float delta){
		if(isVisible()){
			if(Gdx.input.isButtonPressed(Buttons.RIGHT)){
				hide();
			}
			super.act(delta);
			if(getX() != InputHandler.getMousePos().x || getY() != InputHandler.getMousePos().y){
				setPosition(InputHandler.getMousePos().x, InputHandler.getMousePos().y);
			}
		}
	}
	public void show(String iconName){
		setDrawable(skin.getDrawable("gui/"+iconName));
		setVisible(true);
	}
	public void hide(){
		setVisible(false);
	}
}
