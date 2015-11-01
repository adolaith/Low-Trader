package com.ado.trader.input;

import com.ado.trader.GameMain;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class GameInput extends InputHandler {

	public GameInput() {
		super();
	}
	
	public boolean leftClick(int button){
		return false;
	}
	public boolean rightClick(int button){
//			if(gui.rightClickAction()) return true;
		
		return false;
	}

	public boolean keyUp(int keycode) {
		super.keyUp(keycode);

		switch(keycode){
		case Keys.ESCAPE:
			Gdx.app.exit();
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
}
