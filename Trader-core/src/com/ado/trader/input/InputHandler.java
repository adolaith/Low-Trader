package com.ado.trader.input;

import com.ado.trader.map.Map;
import com.ado.trader.map.Tile;
import com.ado.trader.utils.IsoUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class InputHandler implements InputProcessor{
	int velocity = 25;
	static Vector2 currentVelocity = new Vector2(); //camera velocity
	public static boolean DEBUG = false;
	
	Vector3 vec3Clicked,mousePosVec3;
	int mouseButton;
	public static Vector2 mapClicked;
	public static Vector2 mapUp;
	static Vector2 isoClicked;
	static Vector2 isoUp;
	static Vector2 mousePosVec2;
	static Vector2 dragDir;
	Sprite highlight;
	
	InputMultiplexer inputSystem;
	
	Stage stage;
	Map map;
	OrthographicCamera camera;

	//Map, stage, camera and tileHighlight must be added using builder methods provided before the first render call
	public InputHandler(){
		vec3Clicked = new Vector3();
		isoClicked= new Vector2();
		mapClicked= new Vector2();
		isoUp = new Vector2();
		mapUp = new Vector2();
		dragDir = new Vector2();
		mousePosVec3 = new Vector3();
		mousePosVec2 = new Vector2(0,0);
	}

	public boolean leftClick(int button){
		return false;
	}
	public boolean rightClick(int button){
		return false;
	}
	@Override
	public boolean keyDown(int keycode) {
		switch(keycode){
		case Keys.W:
			currentVelocity.y = velocity;
			break;
		case Keys.S:
			currentVelocity.y = -(velocity);
			break;
		case Keys.A:
			currentVelocity.x = -(velocity);
			break;
		case Keys.D:
			currentVelocity.x = velocity;
			break;

		default:
			break;
		}
		return true;
	}
	@Override
	public boolean keyUp(int keycode) {
		switch(keycode){
		case Keys.W:
			if(currentVelocity.y == velocity){
				currentVelocity.y = 0;}
			break;
		case Keys.S:
			if(currentVelocity.y == -velocity){
				currentVelocity.y = 0;}
			break;
		case Keys.A:
			if(currentVelocity.x == -velocity){
				currentVelocity.x = 0;}
			break;
		case Keys.D:
			if(currentVelocity.x == velocity){
				currentVelocity.x = 0;}
			break;
		}
		
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		mouseButton = button;
		//converts and stores mouse coords to 2Dworld coords
		vec3Clicked.set(screenX, screenY, 0);
		camera.unproject(vec3Clicked);
		isoClicked.set(vec3Clicked.x, vec3Clicked.y);
		
		stage.setKeyboardFocus(null);
		stage.setScrollFocus(null);
		
		mapClicked = IsoUtils.getColRow((int)isoClicked.x, (int)isoClicked.y, map.getTileWidth(), map.getTileHeight());
		
		if(mapClicked.x < 0 || mapClicked.y < 0 || mapClicked.x > map.getWidthInTiles() || mapClicked.y > map.getHeightInTiles()){return true;}
		
		return false;
	}
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		vec3Clicked.set(screenX, screenY, 0);
		camera.unproject(vec3Clicked);
		isoUp.set(vec3Clicked.x, vec3Clicked.y);
		
		mapUp = IsoUtils.getColRow((int)isoUp.x, (int)isoUp.y, map.getTileWidth(), map.getTileHeight());
		
		if(mapUp.x < 0 || mapUp.y < 0 || mapUp.x > map.getWidthInTiles() || mapUp.y > map.getHeightInTiles()){return true;}
		
		if(button == Buttons.LEFT){
			leftClick(button);
		}else if(button == Buttons.RIGHT){
			rightClick(button);	
		}
		
		//clear vectors
//		if(!dragDir.isZero()){
//			dragDir.setZero();
//			currentVelocity.setZero();
//			Gdx.app.log("InputHandler(drag): ", "GOT HERE");
//		}
		mousePosVec2.setZero();
		mapClicked.setZero();
		return true;
	}
	
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		mousePosVec3.set(screenX, screenY, 0);
		camera.unproject(mousePosVec3);
		mousePosVec2.set(mousePosVec3.x, mousePosVec3.y);
		
		
//		if(mouseButton == Buttons.RIGHT && dragDir.x != screenX && dragDir.y != screenY){
//			dragDir.x = mousePosVec3.x - vec3Clicked.x;
//			dragDir.y = mousePosVec3.y - vec3Clicked.y;
//			Gdx.app.log("InputHandler(drag): ", "drag direction: "+ dragDir);
//			
//			camera.translate( camera.position.x - dragDir.x, camera.position.y - dragDir.y);
//			dragDir.set(screenX, screenY);
//		}
		
		return true;
	}
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		mousePosVec3.set(screenX, screenY, 0);
		camera.unproject(mousePosVec3);
		mousePosVec2.set(mousePosVec3.x, mousePosVec3.y);
		return true;
	}
	public boolean scrolled(int amount) {
		if(amount < 0 && camera.zoom > 0.9f){
			camera.zoom -= 0.01;
		}else if(camera.zoom < 1.34f){
			camera.zoom += 0.01;
		}
		
		return true;
	}
	public void render(SpriteBatch batch){
		try{
			renderTileHighlight(batch);
		}catch(Exception ex){
			Gdx.app.log("Game - InputHandler: ", "RENDER FAIL!");
		}
	}
	public void renderTileHighlight(SpriteBatch batch){
		Vector2 mapPos = IsoUtils.getColRow((int)mousePosVec2.x, (int)mousePosVec2.y, map.getTileWidth(), map.getTileHeight());
		if(mapPos.x<0||mapPos.y<0||mapPos.x>=map.getWidthInTiles()||mapPos.y>=map.getHeightInTiles()){return;}
		
		Tile t = map.getTileLayer().map[(int)mapPos.x][(int)mapPos.y][map.currentLayer];
		mapPos = IsoUtils.getIsoXY((int)t.getX(), (int)t.getY(), map.getTileWidth(), map.getTileHeight());
		batch.begin();
		batch.draw(highlight, (int)mapPos.x, (int)mapPos.y,highlight.getWidth()*highlight.getScaleX(),highlight.getHeight()*highlight.getScaleY());	
		batch.end();
	}
	public void setInputSystems(InputProcessor... processors) {
		inputSystem = new InputMultiplexer(processors);
		Gdx.input.setInputProcessor(inputSystem);
	}
	public InputHandler addMap(Map map){
		this.map = map;
		return this;
	}
	public InputHandler addStage(Stage stage){
		this.stage = stage;
		setInputSystems(stage, this);
		return this;
	}
	public InputHandler addCamera(OrthographicCamera cam){
		this.camera = cam;
		return this;
	}
	public InputHandler addTileHighlight(Sprite tileHighlight){
		highlight = tileHighlight;
		highlight.setScale(2);
		return this;
	}
	public static Vector2 getVelocity(){
		return currentVelocity;
	}
	public static Vector2 getIsoClicked() {
		return isoClicked;
	}
	public static Vector2 getMapClicked() {
		return mapClicked;
	}
	public Vector3 getVec3Clicked() {
		return vec3Clicked;
	}
	public static Vector2 getMousePos() {
		return mousePosVec2;
	}
}
