package com.ado.trader.screens;

import com.ado.trader.GameMain;
import com.ado.trader.buildings.BuildingCollection;
import com.ado.trader.gui.CustomCursor;
import com.ado.trader.gui.ToolTip;
import com.ado.trader.gui.editor.MapEditorPanel;
import com.ado.trader.gui.editor.MiniMap;
import com.ado.trader.input.InputHandler;
import com.ado.trader.input.MapEditorInput;
import com.ado.trader.map.EditorStreamer;
import com.ado.trader.placement.PlacementManager;
import com.ado.trader.systems.AiSystem;
import com.ado.trader.systems.AnimationSystem;
import com.ado.trader.systems.SaveSystem;
import com.ado.trader.utils.GameServices;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class MapEditorScreen implements Screen {
	static GameMain game;
	GameServices gameServices;
	BuildingCollection buildings;
	
	static Vector2 velocity = new Vector2(); //camera velocity
	Entity currentlySelected;
	Label fps;

	public MapEditorScreen(GameMain game, String loadDir) {
		MapEditorScreen.game = game;
		MapEditorInput input = new MapEditorInput();
		gameServices = new GameServices(1280, 720, input, loadDir);
		
		initWorld();
		
		gameServices.setStreamer(new EditorStreamer(gameServices.getMap()));
		
		input.addPlacementManager(new PlacementManager(gameServices));
		
		LabelStyle style = new LabelStyle(gameServices.getFont(), Color.WHITE);
		fps = new Label("", style);
		fps.setPosition(Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() - 20);
		gameServices.getStage().addActor(fps);
		
		style = new LabelStyle(style);
		style.fontColor = Color.BLACK;
		
		new ToolTip(style, gameServices.getSkin(), gameServices.getStage());
		new CustomCursor(gameServices);
		new MapEditorPanel(gameServices);
		new MiniMap(gameServices);
		
		runLogic = true;
	}
	
	private void initWorld(){
		World world = GameServices.getWorld();
		world.setSystem(new AiSystem(gameServices)); //TEST USE
		world.setSystem(new AnimationSystem());
		world.setSystem(new SaveSystem(gameServices), true);
		world.initialize();
		
//		gameServices.getStage().setDebugAll(true);
	}
	@Override
	public void show() {
		
	}

	public static boolean runLogic;
	@Override
	public void render(float delta) {
		if(!runLogic){
			//dont move camera or gui
			InputHandler.getVelocity().setZero();
		}
		//LOGIC
		((EditorStreamer)gameServices.getStreamer()).streamMap(gameServices.getCam());
		GameServices.getWorld().setDelta(delta);
		GameServices.getWorld().process();
		
		//RENDER
		gameServices.getCam().translate(InputHandler.getVelocity().x, InputHandler.getVelocity().y);
		gameServices.getCam().update();
		
		gameServices.getRenderer().render(delta);
		
		gameServices.getStage().act(delta);
		gameServices.getStage().draw();
		
		fps.setText("FPS: "+ Gdx.graphics.getFramesPerSecond());
	}
	
	public static GameMain getGameMain(){
		return game;
	}

	@Override
	public void resize(int width, int height) {
		gameServices.getStage().getViewport().update(width, height, false);
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
