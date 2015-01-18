package com.ado.trader.screens;

import com.ado.trader.GameMain;
import com.ado.trader.buildings.BuildingCollection;
import com.ado.trader.entities.EntityLoader;
import com.ado.trader.gui.GameGui;
import com.ado.trader.gui.GameServices;
import com.ado.trader.input.GameInput;
import com.ado.trader.systems.AiSystem;
import com.ado.trader.systems.AnimationSystem;
import com.ado.trader.systems.GameTime;
import com.ado.trader.systems.MovementSystem;
import com.ado.trader.systems.SaveSystem;
import com.ado.trader.systems.StatusIconSystem;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

//Main game class
public class GameScreen implements Screen{
	GameMain game;
	
	GameServices gameServices;
	GameGui gui;
	
	BuildingCollection buildings;
	
	static Vector2 velocity = new Vector2(); //camera velocity
	
	//initialize
	public GameScreen(GameMain game) {
		this.game = game;
		init(null);
	}
	
	//loads saved states from save dir files
	public GameScreen(GameMain game, String dirName){
		try{
			this.game = game;
			init(dirName);
		}catch(Exception ex){
			Gdx.app.log("CRITICAL ERROR: ", "Loading game failed. Exception: "+ ex);
			Gdx.app.exit();
		}
	}
	private void init(String loadDir){
		
		gameServices = new GameServices(1280, 720, new GameInput(), loadDir);
				
		initWorld();
		
		gui = new GameGui(gameServices);
		
		if(loadDir != null){
			EntityLoader loader = gameServices.getEntities().getLoader();
			loader.loadSavedEntities("testSaveDERP", gameServices);
		}
		
	}
	private void initWorld(){
		World world = gameServices.getWorld();
		world.setManager(new GroupManager());
		world.setSystem(new AnimationSystem());
		world.setSystem(new AiSystem(gameServices));
		world.setSystem(new MovementSystem(gameServices));
		world.setSystem(new SaveSystem(gameServices), true);
//		world.setSystem(new FarmSystem(this), true);
		world.setSystem(new GameTime(1.0f));
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
		
		gameServices.getWorld().setDelta(delta);
		gameServices.getWorld().process();
		
		updateTime = TimeUtils.nanosToMillis(TimeUtils.timeSinceNanos(start));
//		Gdx.app.log(GameMain.LOG, "===========END-LOOP==========");
	}
	
	//updates logic and renderer
	@Override
	public void render(float delta) {
		updateLogic(delta);
		
		gameServices.getCam().translate(velocity.x, velocity.y);
		
		gameServices.getStage().act(delta);
		gui.update();
		
		gameServices.getRenderer().render(delta);
	}
	
	public BuildingCollection getBuildingCollection() {
		return buildings;
	}
	public static Vector2 getVelocity() {
		return velocity;
	}
	public GameGui getGui() {
		return gui;
	}
	@Override
	public void resize(int width, int height) {
		gui.resize(width, height);
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

	@Override
	public void dispose() {
		gameServices.dispose();
	}
}