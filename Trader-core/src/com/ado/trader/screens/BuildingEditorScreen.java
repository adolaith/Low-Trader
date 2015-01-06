package com.ado.trader.screens;

import com.ado.trader.GameMain;
import com.ado.trader.buildings.BuildingCollection;
import com.ado.trader.entities.EntityCollection;
import com.ado.trader.gui.Gui;
import com.ado.trader.input.InputHandler;
import com.ado.trader.items.ItemCollection;
import com.ado.trader.map.Map;
import com.ado.trader.placement.PlacementManager;
import com.ado.trader.rendering.WorldRenderer;
import com.ado.trader.systems.AiSystem;
import com.ado.trader.systems.AnimationSystem;
import com.ado.trader.systems.MovementSystem;
import com.ado.trader.systems.SaveSystem;
import com.ado.trader.utils.FileParser;
import com.ado.trader.utils.IsoUtils;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

public class BuildingEditorScreen implements Screen {
	GameMain game;
	WorldRenderer renderer;
	InputHandler input;
	FileParser parser;
	
	TextureAtlas atlas;
	Map map;
	World world;
	
	EntityCollection entities;
	ItemCollection items;
	BuildingCollection buildings;
	PlacementManager placement;
	
	static Vector2 velocity = new Vector2(); //camera velocity
	Entity currentlySelected;

	public BuildingEditorScreen(GameMain game) {
		this.game = game;
		world = new World();
		parser = new FileParser();
		atlas = new TextureAtlas("img/master.pack");
		renderer = new WorldRenderer(this);
		
		world.setManager(new GroupManager());
		world.setSystem(new SaveSystem(this), true);
		world.initialize();
		entities = new EntityCollection(this);
		
		map = new Map(this);
		placement = new PlacementManager(this);
		
		buildings = new BuildingCollection(this);
		items = new ItemCollection("data/ItemProfiles", this);
		
		input = new InputHandler(this);
		
		//centre camera on map
		Vector2 tmp = IsoUtils.getIsoXY(map.getWidthInTiles()/2, map.getHeightInTiles()/2, map.getTileWidth(), map.getTileHeight());
		renderer.getCamera().position.x = tmp.x;
		renderer.getCamera().position.y = tmp.y;
	}

	@Override
	public void show() {
		
	}

	@Override
	public void render(float delta) {
		
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {
		
	}
}
