package com.ado.trader.screens;

import com.ado.trader.GameMain;
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
	TextButton play,load, exit;
	Label label;
	TextButtonStyle style;
	
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
		stage.clear();
		
		Gdx.input.setInputProcessor(stage);
		
		setStyle("gui/panelButton", "gui/panelButton2", white);
		
		play = new TextButton("Play", style);
		play.setWidth(400);
		play.setHeight(100);
		play.setX(Gdx.graphics.getWidth()/2 - play.getWidth() /2);
		play.setY(Gdx.graphics.getHeight()/2+100 - play.getHeight() /2);
		
		//handles button action on pressed down/up
		play.addListener(new InputListener(){
			public boolean touchDown(InputEvent e, float x, float y, int pointer, int button){
				return true;
			}
			public void touchUp(InputEvent e, float x, float y, int pointer, int button){
				System.out.println("New Game");
				GameScreen gs = new GameScreen(game);
				game.setScreen(gs);
			}
		});
		
		load = new TextButton("Load", style);
		load.setWidth(400);
		load.setHeight(100);
		load.setX(Gdx.graphics.getWidth()/2 - load.getWidth() /2);
		load.setY(Gdx.graphics.getHeight()/2 - load.getHeight() /2);
		
		//handles button action on pressed down/up
		load.addListener(new InputListener(){
			public boolean touchDown(InputEvent e, float x, float y, int pointer, int button){
				return true;
			}
			public void touchUp(InputEvent e, float x, float y, int pointer, int button){
				System.out.println("Loading Game...");
				game.setScreen(new GameScreen(game, "testSaveDERP"));
			}
		});
		
		exit = new TextButton("Exit", style);
		exit.setWidth(400);
		exit.setHeight(100);
		exit.setX(Gdx.graphics.getWidth()/2 - exit.getWidth() /2);
		exit.setY(Gdx.graphics.getHeight()/2-100 - exit.getHeight() /2);
		
		//handles button action on pressed down/up
		exit.addListener(new InputListener(){
			public boolean touchDown(InputEvent e, float x, float y, int pointer, int button){
				return true;
			}
			public void touchUp(InputEvent e, float x, float y, int pointer, int button){
				Gdx.app.exit();
			}
		});
		
		LabelStyle ls = new LabelStyle(white, Color.BLUE);
		label = new Label("RTS Prototype", ls);
		label.setX(0);
		label.setY(Gdx.graphics.getHeight()/2+200);
		label.setWidth(width);
		label.setAlignment(Align.center);
		
		stage.addActor(play);
		stage.addActor(load);
		stage.addActor(exit);
		stage.addActor(label);
	}

	@Override
	public void show() {

		//init call
		batch = new SpriteBatch();
		atlas = new TextureAtlas(Gdx.files.internal("img/master.pack")); 
		skin = new Skin();
		skin.addRegions(atlas);
		white = new BitmapFont(Gdx.files.internal("font/baseFontWhite.fnt"), false);
	}

	@Override
	public void hide() {
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
