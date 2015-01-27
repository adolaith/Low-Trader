package com.ado.trader.input;

import com.ado.trader.GameMain;
import com.ado.trader.placement.PlacementManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MapEditorInput extends InputHandler{
	PlacementManager plManager;

	public MapEditorInput() {
		super();
	}
	
	public boolean leftClick(int button){
		if(!super.leftClick(button)){
			
		}
		
		return false;
	}
	public boolean rightClick(int button){
		if(!super.rightClick(button)){
			if(plManager.getPlacementSelection()!=null){
				plManager.resetSelection();
				return true;
			}
			if(plManager.handleClick(mapClicked, this)){
				return true;
			}
		}
		
		return false;
	}

	public boolean keyUp(int keycode) {
		super.keyUp(keycode);
		
		switch(keycode){
		case Keys.ESCAPE:
			Gdx.app.exit();
			break;
		case Keys.R:
			plManager.rotateSelection();
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
			plManager.setEditMode(!plManager.isEditMode());
			break;
		default:
			break;
		}
		return true;
	}
	@Override
	public boolean keyDown(int keycode) {
		super.keyDown(keycode);
		
		switch(keycode){
		}
		return true;
	}
	public void render(SpriteBatch batch){
		super.render(batch);
		plManager.render(batch);
	}
	public void addPlacementManager(PlacementManager placement){
		this.plManager = placement;
	}
	public PlacementManager getPlacementManager(){
		return plManager;
	}
}
