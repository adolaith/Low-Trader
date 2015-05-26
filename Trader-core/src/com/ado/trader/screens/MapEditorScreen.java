package com.ado.trader.screens;

import com.ado.trader.GameMain;
import com.ado.trader.buildings.BuildingCollection;
import com.ado.trader.entities.EntityLoader;
import com.ado.trader.gui.CustomCursor;
import com.ado.trader.gui.ToolTip;
import com.ado.trader.gui.editor.MapEditorPanel;
import com.ado.trader.input.InputHandler;
import com.ado.trader.input.MapEditorInput;
import com.ado.trader.map.Chunk;
import com.ado.trader.map.MapRegion;
import com.ado.trader.map.TileLayer;
import com.ado.trader.placement.PlacementManager;
import com.ado.trader.systems.AnimationSystem;
import com.ado.trader.systems.SaveSystem;
import com.ado.trader.utils.FileLogger;
import com.ado.trader.utils.GameServices;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class MapEditorScreen implements Screen {
	static GameMain game;
	GameServices gameServices;
	BuildingCollection buildings;
	
	static Vector2 velocity = new Vector2(); //camera velocity
	Entity currentlySelected;

	public MapEditorScreen(GameMain game) {
		FileLogger.writeLog("MapEditor: [INIT]");
		MapEditorScreen.game = game;
		MapEditorInput input = new MapEditorInput();
		gameServices = new GameServices(1280, 720, input, null);
		
		FileLogger.writeLog("MapEditor: gameServices started");
		
		initWorld();
		
		FileLogger.writeLog("MapEditor: world systems started");
		
		input.addPlacementManager(new PlacementManager(gameServices));
		
		FileLogger.writeLog("MapEditor: placementManager added");
		
		new ToolTip(gameServices.getFont(), gameServices.getSkin(), gameServices.getStage());
		new CustomCursor(gameServices);
		new MapEditorPanel(gameServices);
		
		FileLogger.writeLog("MapEditor: gui elements added");
		
		runLogic = true;
		
	}
	
	public MapEditorScreen(GameMain game, String loadDir){
		MapEditorScreen.game = game;
		MapEditorInput input = new MapEditorInput();
		gameServices = new GameServices(1280, 720, input, loadDir);
		input.addPlacementManager(new PlacementManager(gameServices));
		
		initWorld();
		
		new ToolTip(gameServices.getFont(), gameServices.getSkin(), gameServices.getStage());
		new CustomCursor(gameServices);
		new MapEditorPanel(gameServices);
		
		input.addPlacementManager(new PlacementManager(gameServices));
		
		EntityLoader loader = gameServices.getEntities().getLoader();
		loader.loadSavedEntities(loadDir, gameServices);
		
		runLogic = true;
	}
	
	private void initWorld(){
		World world = gameServices.getWorld();
//		world.setSystem(new AiSystem(gameServices)); //TEST USE
		world.setSystem(new AnimationSystem());
		world.setSystem(new SaveSystem(gameServices), true);
		world.initialize();
	}
	@Override
	public void show() {
		
	}

	public static boolean runLogic;
	@Override
	public void render(float delta) {
		if(!runLogic){
			//dont move camera or gui :P
			InputHandler.getVelocity().setZero();
			
		}
		
		//LOGIC
		gameServices.getWorld().setDelta(delta);
		gameServices.getWorld().process();
		
		//RENDER
		gameServices.getCam().translate(InputHandler.getVelocity().x, InputHandler.getVelocity().y);
		
		gameServices.getStage().act(delta);
		
		gameServices.getRenderer().render(delta);
	}
	
	public static GameMain getGameMain(){
		return game;
	}

	@Override
	public void resize(int width, int height) {
		gameServices.getStage().getViewport().update(width, height, true);
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		gameServices.dispose();
	}
}
