package com.ado.trader.input;

import com.ado.trader.GameMain;
import com.ado.trader.gui.Gui;
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
	PlacementManager plManager;
	SaveSystem saveSys;
	Gui gui;

	public GameInput(SaveSystem save, Gui gui, Stage stage, Map map, PlacementManager plManager, OrthographicCamera camera) {
		super(stage, map, camera);
		this.saveSys = save;
		this.plManager = plManager;
		this.gui = gui;
	}

	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		super.touchUp(screenX, screenY, pointer, button);
		
		if(button == Buttons.LEFT){

			return true;
		}else if(button == Buttons.RIGHT){
			if(plManager.getPlacementSelection()!=null){
				plManager.resetSelection();
				return true;
			}
			
			if(gui.rightClickAction()) return true;
			
		}
		
		if(plManager.handleClick(mapClicked, this)) return true;
		
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
			plManager.rotateSelection();
			break;
		case Keys.MINUS:
			String saveDir = "testSaveDERP";
			map.getMapLoader().saveMap(saveDir);
			saveSys.saveEntities(saveDir);
			
			Gdx.app.log(GameMain.LOG, "GAME SAVED");
			break;
		case Keys.F1:
			DEBUG = !DEBUG;
			break;
		case Keys.F3:
			plManager.setEditMode(!plManager.isEditMode());
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
