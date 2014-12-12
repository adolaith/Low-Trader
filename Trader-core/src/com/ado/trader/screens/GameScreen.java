package com.ado.trader.screens;

import com.ado.trader.GameMain;
import com.ado.trader.entities.EntityCollection;
import com.ado.trader.gui.Gui;
import com.ado.trader.items.ItemCollection;
import com.ado.trader.map.Map;
import com.ado.trader.systems.AiSystem;
import com.ado.trader.systems.AnimationSystem;
import com.ado.trader.systems.FarmSystem;
import com.ado.trader.systems.GameTime;
import com.ado.trader.systems.MovementSystem;
import com.ado.trader.systems.SaveSystem;
import com.ado.trader.systems.StatusIconSystem;
import com.ado.trader.utils.FileParser;
import com.ado.trader.utils.InputHandler;
import com.ado.trader.utils.IsoUtils;
import com.ado.trader.utils.WorldRenderer;
import com.ado.trader.utils.pathfinding.AStarPathFinder;
import com.ado.trader.utils.placement.PlacementManager;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

//Main game class
public class GameScreen implements Screen{
	
	GameMain game;
	WorldRenderer renderer;
	InputHandler input;
	FileParser parser;
	
	TextureAtlas atlas;
	Map map;
	Gui gui;
	World world;
	
	EntityCollection entities;
	ItemCollection items;
	AStarPathFinder pathfinder;
	PlacementManager placement;
	
	static Vector2 velocity = new Vector2(); //camera velocity
	public static String saveDir;
	
	Entity currentlySelected;
	
	//initialize
	public GameScreen(GameMain game) {
		this.game = game;
		world = new World();
		parser = new FileParser();
		atlas = new TextureAtlas("img/master.pack");
		renderer = new WorldRenderer(this);
		initWorld();
		map = new Map(this);
		placement = new PlacementManager(this);
		
		items = new ItemCollection("data/ItemProfiles", this);
		
		gui = new Gui(this);
		input = new InputHandler(this);
		
		//centre camera on map
		Vector2 tmp = IsoUtils.getIsoXY(map.getWidthInTiles()/2, map.getHeightInTiles()/2, map.getTileWidth(), map.getTileHeight());
		renderer.getCamera().position.x = tmp.x;
		renderer.getCamera().position.y = tmp.y;
		
		pathfinder = new AStarPathFinder(map, 500, false);
	}
	
	//loads saved states from save dir files
	public GameScreen(GameMain game, String dirName){
		if(Gdx.files.external("saves/"+dirName).exists() && Gdx.files.external("saves/"+dirName).isDirectory()){
			this.game = game;
			world = new World();
			parser = new FileParser();
			atlas = new TextureAtlas("img/master.pack");
			renderer = new WorldRenderer(this);
			initWorld();
			items = new ItemCollection("data/ItemProfiles", this);
			placement = new PlacementManager(this);
			map = new Map(this,"testSaveDERP");
			
			entities.getLoader().loadSavedEntities("testSaveDERP", this, entities);
			
			gui = new Gui(this);
			input = new InputHandler(this);
			
			//centre camera on map
			Vector2 tmp = IsoUtils.getIsoXY(map.getWidthInTiles()/2, map.getHeightInTiles()/2, map.getTileWidth(), map.getTileHeight());
			renderer.getCamera().position.x = tmp.x;
			renderer.getCamera().position.y = tmp.y;
			
			pathfinder = new AStarPathFinder(map, 500, false);
		}else{
			Gdx.app.log("CRITICAL ERROR: ", "Loading game failed. File not found");
			Gdx.app.exit();
		}
	}
	private void initWorld(){
		world.setManager(new GroupManager());
		world.setSystem(new AnimationSystem(this));
		world.setSystem(new AiSystem(this));
		world.setSystem(new MovementSystem(this));
		world.setSystem(new SaveSystem(this), true);
		world.setSystem(new FarmSystem(this), true);
		world.setSystem(new GameTime(1.0f));
		world.setSystem(new StatusIconSystem(0.7f, atlas));
		world.initialize();
		entities = new EntityCollection(this);
	}
	
	public void saveGame(String fileName){
		saveDir = fileName;
		map.saveMap(fileName);
		world.getSystem(SaveSystem.class).process();
		Gdx.app.log(GameMain.LOG, "GAME SAVED");
	}
	public void setSpeed(float modifier){
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
	public long updateTime;
	public int speed = 0;
	
	public void update(float delta){
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
		world.setDelta(delta);
		world.process();
		updateTime = TimeUtils.nanosToMillis(TimeUtils.timeSinceNanos(start));//debug code
//		Gdx.app.log(GameMain.LOG, "===========END-LOOP==========");
	}
	
	//updates logic and renderer
	@Override
	public void render(float delta) {
		update(delta);
		renderer.getCamera().translate(velocity.x, velocity.y);
		gui.getStage().act(delta);
		gui.update(velocity.x, velocity.y);
		renderer.render(delta);
	}
	
	public Entity getCurrentlySelected() {
		return currentlySelected;
	}
	public void setCurrentlySelected(Entity currentlySelected) {
		this.currentlySelected = currentlySelected;
	}
	public FileParser getParser() {
		return parser;
	}
	public TextureAtlas getAtlas() {
		return atlas;
	}
	public static Vector2 getVelocity() {
		return velocity;
	}
	public WorldRenderer getRenderer() {
		return renderer;
	}
	public InputHandler getInput() {
		return input;
	}
	public Map getMap() {
		return map;
	}
	public Gui getGui() {
		return gui;
	}
	public EntityCollection getEntities() {
		return entities;
	}
	public AStarPathFinder getPathfinder() {
		return pathfinder;
	}	
	public World getWorld() {
		return world;
	}
	public PlacementManager getPlaceManager(){
		return placement;
	}
	public ItemCollection getItems(){
		return items;
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
		renderer.dispose();
		gui.dispose();
		atlas.dispose();
	}
}