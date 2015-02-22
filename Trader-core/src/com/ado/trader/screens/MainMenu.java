package com.ado.trader.screens;

import com.ado.trader.GameMain;
import com.ado.trader.gui.GameOptions;
import com.ado.trader.gui.LoadGame;
import com.ado.trader.gui.NewGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

//old code from a tutorial. Used purely for utility. TO be replaced.
public class MainMenu implements Screen {
	
	GameMain game;
	Stage stage;
	BitmapFont white;
	TextureAtlas atlas;
	Skin skin;
	SpriteBatch batch;
	TextButtonStyle style;
	GameOptions options;
	
	public MainMenu(GameMain game){
		this.game = game;
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
		
		stage.act(delta);
		
		batch.begin();
		stage.draw();
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		//creates stage if there is none
		if(stage == null){
			stage = new Stage();
		}
		
	}

	@Override
	public void show() {

		//init call
		batch = new SpriteBatch();
		atlas = new TextureAtlas(Gdx.files.internal("img/master.pack")); 
		skin = new Skin();
		skin.addRegions(atlas);
		white = new BitmapFont(Gdx.files.internal("font/white.fnt"), false);
		white.scale(1.6f);
		stage = new Stage();
		options = new GameOptions(white, skin, stage);
		
		Gdx.input.setInputProcessor(stage);
		
		new LoadGame(game, white, skin, stage);
		
		Table root = new Table();
		root.setName("mainMenu");
		root.defaults().width(400).height(60);
		
		setStyle("gui/panelButton", "gui/panelButton2", white);
		new NewGame(game, white, skin, stage);
		
		//Prompts map selection then starts new game
		TextButton newGame = new TextButton("New Game", style);
		newGame.addListener(new InputListener(){
			public boolean touchDown(InputEvent e, float x, float y, int pointer, int button){
				return true;
			}
			public void touchUp(InputEvent e, float x, float y, int pointer, int button){
				System.out.println("New Game");
				NewGame window = (NewGame) stage.getRoot().findActor("newGame");
				window.showWindow(Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 7);
			}
		});
		
		//load a saved game
		TextButton load = new TextButton("Load Game", style);
		load.addListener(new InputListener(){
			public boolean touchDown(InputEvent e, float x, float y, int pointer, int button){
				return true;
			}
			public void touchUp(InputEvent e, float x, float y, int pointer, int button){
				LoadGame load = (LoadGame) stage.getRoot().findActor("loadGame");
				load.showWindow(Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 7);
			}
		});
		
		//start up the map editor
		TextButton mapEditor = new TextButton("Map Editor", style);
		mapEditor.addListener(new InputListener(){
			public boolean touchDown(InputEvent e, float x, float y, int pointer, int button){
				return true;
			}
			public void touchUp(InputEvent e, float x, float y, int pointer, int button){
				System.out.println("Starting Map Editor...");
				game.setScreen(new MapEditorScreen(game));
			}
		});
		
		//change game options
		TextButton optionsButton = new TextButton("Options", style);
		optionsButton.addListener(new InputListener(){
			public boolean touchDown(InputEvent e, float x, float y, int pointer, int button){
				return true;
			}
			public void touchUp(InputEvent e, float x, float y, int pointer, int button){
				System.out.println("Game options coming soon...");
				options.showWindow(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
			}
		});
		
		//Quit game to desktop
		TextButton quit = new TextButton("Quit", style);
		quit.addListener(new InputListener(){
			public boolean touchDown(InputEvent e, float x, float y, int pointer, int button){
				return true;
			}
			public void touchUp(InputEvent e, float x, float y, int pointer, int button){
				Gdx.app.exit();
			}
		});
		
		LabelStyle ls = new LabelStyle(white, Color.BLUE);
		Label label = new Label("RTS Prototype", ls);
		label.setWidth(400);
		label.setAlignment(Align.center);
		
		root.add(label).row();
		root.add(newGame).row();
		root.add(load).row();
		root.add(mapEditor).row();
		root.add(optionsButton).row();
		root.add(quit);
		root.setX(Gdx.graphics.getWidth() / 2);
		root.setY(Gdx.graphics.getHeight() / 2);
		
		stage.addActor(root);
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
		batch.dispose();
		skin.dispose();
		atlas.dispose();
		stage.dispose();
		white.dispose();
	}
	
	public void setStyle(String styleUp, String styleDown, BitmapFont font){
		style = new TextButtonStyle();
		style.up = skin.getDrawable(styleUp);
		style.down = skin.getDrawable(styleDown);
		style.font=font;
		style.fontColor = Color.BLACK;
	}
}
