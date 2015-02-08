package com.ado.trader.gui;

import com.ado.trader.input.InputHandler;
import com.ado.trader.utils.GameServices;
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
			if(InputHandler.getMousePos().x < getX() - 15 || InputHandler.getMousePos().x > getRight() + 15 ||
					InputHandler.getMousePos().y < getY() - 15 || InputHandler.getMousePos().y > getTop() + 15){
				hide();
			}
		}
	}
	
	public void show(){
		setPosition(InputHandler.getIsoClicked().x, InputHandler.getIsoClicked().y - getHeight() / 2);
		setVisible(true);
	}
	
	public void hide(){
		setVisible(false);
		clearChildren();
	}
}
