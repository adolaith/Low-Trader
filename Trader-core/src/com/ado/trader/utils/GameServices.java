package com.ado.trader.utils;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.input.InputHandler;
import com.ado.trader.items.ItemFactory;
import com.ado.trader.map.Map;
import com.ado.trader.map.MapStreamer;
import com.ado.trader.pathfinding.AStarPathFinder;
import com.ado.trader.rendering.Renderer;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameServices {
	Stage stage;
	OrthographicCamera cam;
	Skin skin;
	BitmapFont font;
	TextureAtlas atlas;
	
	World world;
	Map map;
	MapStreamer streamer;
	InputHandler input;
	Renderer renderer;
	
	EntityFactory entities;
	ItemFactory items;
	
	AStarPathFinder pathfinder;
	//God class containing objects needed globally
	public GameServices(int camWidth, int camHeight, InputHandler input, String loadDir){
		atlas = new TextureAtlas("img/master.pack");
		
		Texture texture = new Texture(Gdx.files.internal("font/white.png"), true);
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		this.font = new BitmapFont(Gdx.files.internal("font/white.fnt"), new TextureRegion(texture), false);
		
		font.setScale(0.6f);
		
		cam = new OrthographicCamera(camWidth, camHeight);
		this.skin = new Skin(atlas);
		
		this.stage = new Stage(new ExtendViewport(camWidth, camHeight));
		
		Group layer = new Group();
		layer.setName("guiLayer");
		Viewport view = stage.getViewport();
		layer.setPosition(view.getScreenX(), view.getScreenY());
		layer.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		stage.addActor(layer);
		
		world = new World();
		world.setManager(new TagManager());
		world.setManager(new GroupManager());
		
		FileLogger.writeLog("GameServices: basics started");
		
		items = new ItemFactory("data/ItemProfiles", this);
		
		if(loadDir == null){
			map = new Map(this);
		}else{
			map = new Map(loadDir, this);
		}
		
		FileLogger.writeLog("GameServices: map loaded");
		
		this.input = input;
		//configure input
		this.input.addCamera(cam).addMap(map).addStage(stage).addTileHighlight(atlas.createSprite("gui/highlightTile"));
		
		renderer = new Renderer(this);
		
		entities = new EntityFactory(this);
		
		FileLogger.writeLog("GameServices: entities loaded");
		
		//centre camera on map
		if(map.getRegionMap()[1][1] != null){
			Vector2 tmp = IsoUtils.getIsoXY(map.getWidthInTiles()/2, map.getHeightInTiles()/2, map.getTileWidth(), map.getTileHeight());
			renderer.getCamera().position.x = tmp.x;
			renderer.getCamera().position.y = tmp.y;
		}

		pathfinder = new AStarPathFinder(map, 500, false);
	}
	public MapStreamer getStreamer(){
		return streamer;
	}
	public void setStreamer(MapStreamer streamer){
		this.streamer = streamer;
	}
	public TextureAtlas getAtlas() {
		return atlas;
	}
	public World getWorld() {
		return world;
	}
	public Map getMap() {
		return map;
	}
	public InputHandler getInput() {
		return input;
	}
	public Renderer getRenderer() {
		return renderer;
	}
	public EntityFactory getEntities() {
		return entities;
	}
	public ItemFactory getItems() {
		return items;
	}
	public AStarPathFinder getPathfinder() {
		return pathfinder;
	}
	public Stage getStage() {
		return stage;
	}
	public Skin getSkin() {
		return skin;
	}
	public BitmapFont getFont() {
		return font;
	}
	public OrthographicCamera getCam() {
		return cam;
	}
	public void dispose(){
		atlas.dispose();
		stage.dispose();
		renderer.dispose();
		font.dispose();
	}
}