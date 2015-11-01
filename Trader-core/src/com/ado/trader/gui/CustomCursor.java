package com.ado.trader.gui;

import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class CustomCursor extends Image{
	Skin skin;

	public CustomCursor(GameServices gameRes) {
		skin = gameRes.getSkin();
		setName("customCursor");
		setSize(28, 28);
		
		Group layer = gameRes.getStage().getRoot().findActor("guiLayer");
		layer.addActor(this);
		
		hide();
	}
	
	@Override
	public void act(float delta){
		if(isVisible()){
			if(Gdx.input.isButtonPressed(Buttons.RIGHT)){
				hide();
			}
			super.act(delta);
			Vector2 tmp = getStage().screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
			
			if(getX() != tmp.x || getY() != tmp.y){
				setPosition(tmp.x, tmp.y);
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
