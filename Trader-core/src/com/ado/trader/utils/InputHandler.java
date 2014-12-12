package com.ado.trader.utils;

import com.ado.trader.map.EntityLayer;
import com.ado.trader.screens.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class InputHandler implements InputProcessor{
	
	public static boolean DEBUG = false;
	GameScreen game;
	Vector3 vec3Clicked,mousePosVec3;
	public Vector2 mapClicked;
	Vector2 isoClicked;
	Vector2 mousePosVec2;
	InputMultiplexer inputSystem;
	int velocity = 25;

	public InputHandler(GameScreen game){
		this.game = game;
		vec3Clicked = new Vector3();
		isoClicked= new Vector2();
		mapClicked= new Vector2();
		mousePosVec3 = new Vector3();
		mousePosVec2 = new Vector2(0,0);
		setInputSystems(game.getGui().getStage(),this);
	}

	public boolean leftClick(int button){
		game.getGui().getStage().setKeyboardFocus(null);
		if(button == Buttons.LEFT){
			EntityLayer layer = game.getMap().getCurrentLayerGroup().entityLayer;
			
			if(layer.isOccupied((int)mapClicked.x, (int)mapClicked.y)){		//entity selection
				game.setCurrentlySelected(game.getWorld().getEntity(layer.map[(int)mapClicked.x][(int)mapClicked.y])); 
				return true;
			}
			
			game.setCurrentlySelected(null);
		}
		return false;
	}
	public boolean rightClick(int button){
		if(button != Buttons.RIGHT) return false;
		
			if(game.getPlaceManager().getPlacementSelection()!=null){
				game.getPlaceManager().resetSelection();
				return true;
			}
			
			if(game.getGui().rightClickAction()){return true;}
			
			return true;
	}
	public boolean middleClick(int button){
		return false;
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

	@Override
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
			game.getPlaceManager().rotateSelection();
			break;
		case Keys.MINUS:
			game.saveGame("testSaveDERP");
			break;
		case Keys.F1:
			DEBUG = !DEBUG;
			break;
		case Keys.F3:
			game.getPlaceManager().setEditMode(!game.getPlaceManager().isEditMode());
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		//converts and stores mouse coords to 2Dworld coords
		vec3Clicked.set(screenX, screenY, 0);
		game.getRenderer().getCamera().unproject(vec3Clicked);
		isoClicked.set(vec3Clicked.x, vec3Clicked.y);
		
		mapClicked = IsoUtils.getColRow((int)isoClicked.x, (int)isoClicked.y, game.getMap().getTileWidth(), game.getMap().getTileHeight());
		if(mapClicked.x<0||mapClicked.y<0||mapClicked.x>game.getMap().getWidthInTiles()||mapClicked.y>game.getMap().getHeightInTiles()){return true;}
		
		if(leftClick(button)){return true;}
		if(rightClick(button)){return true;}
		if(middleClick(button)){return true;}
		
		return false;
	}
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Vector3 tmp =new Vector3();
		tmp.set(screenX, screenY, 0);
		game.getRenderer().getCamera().unproject(tmp);
		Vector2 mapUp = IsoUtils.getColRow((int)tmp.x, (int)tmp.y, game.getMap().getTileWidth(), game.getMap().getTileHeight());
		
		if(game.getPlaceManager().handleClick(mapUp, this)) return true;
		
		return false;
	}
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		mousePosVec3.set(screenX, screenY, 0);
		game.getRenderer().getCamera().unproject(mousePosVec3);
		mousePosVec2.set(mousePosVec3.x, mousePosVec3.y);
		return false;
	}
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		mousePosVec3.set(screenX, screenY, 0);
		game.getRenderer().getCamera().unproject(mousePosVec3);
		mousePosVec2.set(mousePosVec3.x, mousePosVec3.y);
		return false;
	}
	public boolean scrolled(int amount) {
		return false;
	}
	public void setInputSystems(InputProcessor... processors) {
		inputSystem = new InputMultiplexer(processors);
		Gdx.input.setInputProcessor(inputSystem);
	}
	public Vector2 getIsoClicked() {
		return isoClicked;
	}
	public Vector2 getMapClicked() {
		return mapClicked;
	}
	public Vector3 getVec3Clicked() {
		return vec3Clicked;
	}
	public Vector2 getMousePos() {
		return mousePosVec2;
	}
}
