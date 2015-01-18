package com.ado.trader.input;

import com.ado.trader.GameMain;
import com.ado.trader.gui.GameGui;
import com.ado.trader.gui.GameServices;
import com.ado.trader.map.Map;
import com.ado.trader.placement.PlacementManager;
import com.ado.trader.screens.GameScreen;
import com.ado.trader.systems.SaveSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class GameInput extends InputHandler {

	public GameInput() {
		super();
	}
	
	public boolean leftClick(int button){
		if(!super.leftClick(button)){
			return false;
		}
		
		return false;
	}
	public boolean rightClick(int button){
		if(!super.rightClick(button)){
//			if(gui.rightClickAction()) return true;
		}
		
		return false;
	}

	public boolean keyUp(int keycode) {
		switch(keycode){
		case Keys.ESCAPE:
			Gdx.app.exit();
		case Keys.W:
			if(GameScreen.getVelocity().y == velocity){
				GameScreen.getVelocity().y = 0;}
			break;
		case Keys.S:
			if(GameScreen.getVelocity().y == -velocity){
				GameScreen.getVelocity().y = 0;}
			break;
		case Keys.A:
			if(GameScreen.getVelocity().x == -velocity){
				GameScreen.getVelocity().x = 0;}
			break;
		case Keys.D:
			if(GameScreen.getVelocity().x == velocity){
				GameScreen.getVelocity().x = 0;}
			break;
		case Keys.R:
			break;
		case Keys.MINUS:
			String saveDir = "testSaveDERP";
			map.saveGameState(saveDir);
			
			Gdx.app.log(GameMain.LOG, "GAME SAVED");
			break;
		case Keys.F1:
			DEBUG = !DEBUG;
			break;
		case Keys.F3:
			break;
		default:
			break;
		}
		return true;
	}
	@Override
	public boolean keyDown(int keycode) {
		switch(keycode){
		case Keys.W:
			GameScreen.getVelocity().y = velocity;
			break;
		case Keys.S:
			GameScreen.getVelocity().y = -(velocity);
			break;
		case Keys.A:
			GameScreen.getVelocity().x = -(velocity);
			break;
		case Keys.D:
			GameScreen.getVelocity().x = velocity;
			break;

		default:
			break;
		}
		return true;
	}
}
