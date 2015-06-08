package com.ado.trader.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

public class GameOptions extends BasicWindow {
	TextButtonStyle style;
	Table body, main, video, audio, gameplay;
	
	public GameOptions(BitmapFont font, Skin skin, Stage stage) {
		super("Game Options", 500, 360, font, skin, stage);
		
		style = new TextButtonStyle();
		style.up = skin.getDrawable("gui/panelButton");
		style.down = skin.getDrawable("gui/panelButton2");
		style.font = font;
		style.fontColor = Color.BLACK;
		
		Table buttons = new Table();
		buttons.defaults().width(150).height(50);

		TextButton apply = new TextButton("Apply", style);
		
		//handles button action on pressed down/up
		apply.addListener(new InputListener(){
			public boolean touchDown(InputEvent e, float x, float y, int pointer, int button){
				return true;
			}
			public void touchUp(InputEvent e, float x, float y, int pointer, int button){
				System.out.println("Apply changed options");
			}
		});
		
		TextButton back = new TextButton("Back", style);
		
		//handles button action on pressed down/up
		back.addListener(new InputListener(){
			public boolean touchDown(InputEvent e, float x, float y, int pointer, int button){
				return true;
			}
			public void touchUp(InputEvent e, float x, float y, int pointer, int button){
				System.out.println("Go Back!");
				if(body.getChildren().first().getName().matches("main")){
					hideWindow();
				}else{
					body.getChildren().first().setVisible(false);
					body.clear();
					body.add(main);
				}
			}
		});
		
		buttons.add(apply, back);
		
		initTables();
		
		body = new Table();
		body.add(main);
		
		body.add(body).expand().row();
		body.add(buttons);
	}
	private void initTables(){
		main = new Table();
		main.setName("main");
		main.defaults().width(300).height(40);
		
		TextButton game = new TextButton("Gameplay options", style);
		
		//handles button action on pressed down/up
		game.addListener(new InputListener(){
			public boolean touchDown(InputEvent e, float x, float y, int pointer, int button){
				return true;
			}
			public void touchUp(InputEvent e, float x, float y, int pointer, int button){
				System.out.println("clicked gameplay options");
			}
		});
		
		TextButton videoButton = new TextButton("Video options", style);
		
		//handles button action on pressed down/up
		videoButton.addListener(new InputListener(){
			public boolean touchDown(InputEvent e, float x, float y, int pointer, int button){
				return true;
			}
			public void touchUp(InputEvent e, float x, float y, int pointer, int button){
				System.out.println("clicked video options");
			}
		});
		
		TextButton audioButton = new TextButton("Audio options", style);
		
		//handles button action on pressed down/up
		audioButton.addListener(new InputListener(){
			public boolean touchDown(InputEvent e, float x, float y, int pointer, int button){
				return true;
			}
			public void touchUp(InputEvent e, float x, float y, int pointer, int button){
				System.out.println("clicked audio options");
			}
		});
		
		main.add(game).row();
		main.add(videoButton).row();
		main.add(audioButton).row();
	}

	public void showWindow(float x, float y){
		super.showWindow(x, y);
	}
}
