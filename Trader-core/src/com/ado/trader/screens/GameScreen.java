package com.ado.trader.screens;

import com.ado.trader.GameMain;
import com.ado.trader.buildings.BuildingCollection;
import com.ado.trader.entities.EntityFeatures;
import com.ado.trader.entities.EntityDataLoader;
import com.ado.trader.gui.CustomCursor;
import com.ado.trader.gui.RightClickMenu;
import com.ado.trader.gui.ToolTip;
import com.ado.trader.gui.game.ControlArea;
import com.ado.trader.input.GameInput;
import com.ado.trader.input.InputHandler;
import com.ado.trader.systems.AiSystem;
import com.ado.trader.systems.AnimationSystem;
import com.ado.trader.systems.MovementSystem;
import com.ado.trader.systems.SaveSystem;
import com.ado.trader.systems.StatusIconSystem;
import com.ado.trader.utils.GameServices;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.TimeUtils;

//Main game class
public class GameScreen implements Screen{
	static GameMain game;
	
	GameServices gameServices;
	
	BuildingCollection buildings;
	public static boolean logicRunning;
	
	//initialize
	public GameScreen(GameMain game) {
		GameScreen.game = game;
		init(null);
	}
	
	//loads saved states from save dir files
	public GameScreen(GameMain game, String dirName){
		GameScreen.game = game;
		init(dirName);
	}
	private void init(String loadDir){
		logicRunning = true;
		gameServices = new GameServices(1280, 720, new GameInput(), loadDir);
		
		//loads feature sprites
		new EntityFeatures(gameServices.getAtlas(), gameServices.getRenderer().getRenderEntitySystem());
		
		//start world systems and managers
		initWorld();
		
		//GUI elements
		new ToolTip(gameServices.getFont(), gameServices.getSkin(), gameServices.getStage());
		new CustomCursor(gameServices);
		new ControlArea(gameServices);
		new RightClickMenu(gameServices);
		
		//load saved entities
		if(loadDir != null){
			EntityDataLoader loader = gameServices.getEntities().getLoader();
			loader.loadSavedEntities(loadDir, gameServices);
		}
		
	}
	private void initWorld(){
		World world = GameServices.getWorld();
		world.setSystem(new AnimationSystem());
		world.setSystem(new AiSystem(gameServices));
		world.setSystem(new MovementSystem(gameServices));
		world.setSystem(new SaveSystem(gameServices), true);
//		world.setSystem(new FarmSystem(this), true);
		world.setSystem(new StatusIconSystem(0.7f, gameServices.getAtlas()));
		world.initialize();
	}
	
	public static void setSpeed(float modifier){
		if(modifier == 0){
			speed = 1;
			return;
		}
		speed += modifier;
		if(speed > 10){
			speed = 10;
		}else if(speed < -3){
			speed = -3;
		}
	}
	
	//updates game logic
	public static long updateTime;
	public static int speed = 0;
	
	public void updateLogic(float delta){
		if(speed > 1){
			delta *= speed;
		}else if(speed == -1){
			delta *= 0.8f;
		}else if(speed == -2){
			delta *= 0.7f;
		}else if(speed == -3){
			delta *= 0.6f;
		}
		long start = TimeUtils.nanoTime();
		
		GameServices.getWorld().setDelta(delta);
		GameServices.getWorld().process();
		
		updateTime = TimeUtils.nanosToMillis(TimeUtils.timeSinceNanos(start));
//		Gdx.app.log(GameMain.LOG, "===========END-LOOP==========");
	}
	
	//updates logic and renderer
	@Override
	public void render(float delta) {
		if(logicRunning){
			updateLogic(delta);
			
			gameServices.getCam().translate(InputHandler.getVelocity().x, InputHandler.getVelocity().y);
		}
		
		gameServices.getStage().act(delta);
		
		gameServices.getRenderer().render(delta);
	}
	
	public BuildingCollection getBuildingCollection() {
		return buildings;
	}
	@Override
	public void resize(int width, int height) {
		gameServices.getStage().getViewport().update(width, height);
	}
	@Override
	public void show() {
	}
	@Override
	public void hide() {
		dispose();
	}
	@Override
	public void pause() {
	}
	@Override
	public void resume() {
	}
	public static GameMain getGame(){
		return game;
	}

	@Override
	public void dispose() {
		gameServices.dispose();
	}
}