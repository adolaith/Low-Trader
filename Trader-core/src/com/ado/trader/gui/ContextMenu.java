package com.ado.trader.gui;

import com.ado.trader.input.InputHandler;
import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class ContextMenu extends Table {
	protected int width = 130;
	protected int height = 26;
	
	public ContextMenu(GameServices gameRes){
		defaults().width(width).height(height).fill().expand().pad(2);
		setBackground(gameRes.getSkin().getDrawable("gui/bGround"));
		setSize(width + 8, height);
		setVisible(false);
	}
	
	@Override
	public void act(float delta){
		super.act(delta);
		if(isVisible()){
			Vector2 tmp = getStage().screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
			
			if(tmp.x < getX() - 15 || tmp.x > getRight() + 15 ||
					tmp.y < getY() - 15 || tmp.y > getTop() + 15){
				hide();
			}
		}
	}
	
	public void show(){
		Vector2 tmp = new Vector2(InputHandler.getVec3Clicked().x, InputHandler.getVec3Clicked().y);
		tmp = getStage().screenToStageCoordinates(tmp);
		setPosition(tmp.x, tmp.y );
		setVisible(true);
	}
	
	public void hide(){
		setVisible(false);
		clearChildren();
	}
}
