package com.ado.trader.screens;

import com.ado.trader.GameMain;
import com.ado.trader.buildings.BuildingCollection;
import com.ado.trader.entities.EntityLoader;
import com.ado.trader.gui.CustomCursor;
import com.ado.trader.gui.ToolTip;
import com.ado.trader.gui.editor.MapEditorPanel;
import com.ado.trader.input.InputHandler;
import com.ado.trader.input.MapEditorInput;
import com.ado.trader.placement.PlacementManager;
import com.ado.trader.systems.AnimationSystem;
import com.ado.trader.systems.SaveSystem;
import com.ado.trader.utils.GameServices;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;

public class MapEditorScreen implements Screen {
	static GameMain game;
	GameServices gameServices;
	BuildingCollection buildings;
	
	static Vector2 velocity = new Vector2(); //camera velocity
	Entity currentlySelected;

	public MapEditorScreen(GameMain game) {
		MapEditorScreen.game = game;
		MapEditorInput input = new MapEditorInput();
		gameServices = new GameServices(1280, 720, input, null);
		input.addPlacementManager(new PlacementManager(gameServices));
		
		initWorld();
		
		new ToolTip(gameServices.getFont(), gameServices.getSkin(), gameServices.getStage());
		new CustomCursor(gameServices);
		new MapEditorPanel(gameServices);
		
		input.addPlacementManager(new PlacementManager(gameServices));
		
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
		world.setManager(new GroupManager());
		world.setSystem(new AnimationSystem());
//		world.setSystem(new AiSystem(gameServices));
//		world.setSystem(new MovementSystem(gameServices));
		world.setSystem(new SaveSystem(gameServices), true);
//		world.setSystem(new FarmSystem(this), true);
//		world.setSystem(new StatusIconSystem(0.7f, gameServices.getAtlas()));
		world.initialize();
	}
	@Override
	public void show() {
		
	}

	public static boolean runLogic;
	@Override
	public void render(float delta) {
		//LOGIC
		if(runLogic){
			gameServices.getWorld().setDelta(delta);
			gameServices.getWorld().process();
			
		}else{
			//dont move camera or gui :P
			InputHandler.getVelocity().setZero();
		}
		
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
		gameServices.getStage().getViewport().update(width, height);
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
