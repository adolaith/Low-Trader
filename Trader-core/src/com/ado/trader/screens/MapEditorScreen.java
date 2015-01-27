package com.ado.trader.screens;

import com.ado.trader.GameMain;
import com.ado.trader.buildings.BuildingCollection;
import com.ado.trader.gui.GameServices;
import com.ado.trader.gui.MapEditorPanel;
import com.ado.trader.gui.ToolTip;
import com.ado.trader.input.InputHandler;
import com.ado.trader.input.MapEditorInput;
import com.ado.trader.placement.PlacementManager;
import com.ado.trader.systems.AnimationSystem;
import com.ado.trader.systems.GameTime;
import com.ado.trader.systems.SaveSystem;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;

public class MapEditorScreen implements Screen {
	GameMain game;
	GameServices gameServices;
	BuildingCollection buildings;
	
	static Vector2 velocity = new Vector2(); //camera velocity
	Entity currentlySelected;

	public MapEditorScreen(GameMain game) {
		this.game = game;
		MapEditorInput input = new MapEditorInput();
		gameServices = new GameServices(1024, 768, input, null);
		input.addPlacementManager(new PlacementManager(gameServices));
		
		initWorld();
		
		new ToolTip(gameServices);
		new MapEditorPanel(gameServices);
		
		input.addPlacementManager(new PlacementManager(gameServices));
	}
	private void initWorld(){
		World world = gameServices.getWorld();
		world.setManager(new GroupManager());
		world.setSystem(new AnimationSystem());
//		world.setSystem(new AiSystem(gameServices));
//		world.setSystem(new MovementSystem(gameServices));
		world.setSystem(new SaveSystem(gameServices), true);
//		world.setSystem(new FarmSystem(this), true);
		world.setSystem(new GameTime(1.0f));
//		world.setSystem(new StatusIconSystem(0.7f, gameServices.getAtlas()));
		world.initialize();
	}
	@Override
	public void show() {
		
	}

	@Override
	public void render(float delta) {
		//LOGIC
		gameServices.getWorld().setDelta(delta);
		gameServices.getWorld().process();
		
		//RENDER
		gameServices.getCam().translate(InputHandler.getVelocity().x, InputHandler.getVelocity().y);
		
		gameServices.getStage().act(delta);
		
		gameServices.getRenderer().render(delta);
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
