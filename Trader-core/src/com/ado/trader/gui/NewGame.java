package com.ado.trader.gui;

import com.ado.trader.GameMain;
import com.ado.trader.screens.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class NewGame extends BasicWindow {
	List<String> mapList;
	String internalPath = "./bin/data/maps/";
	String externalPath = "adoGame/maps/";
	
	static int width = (int) (Gdx.graphics.getWidth() * 0.5);
	static int height = (int) (Gdx.graphics.getHeight() * 0.6);

	public NewGame(final GameMain game, BitmapFont font, Skin skin, Stage stage) {
		super("Select map", width, height, font, skin, stage);
		setName("newGame");
		root.top();
		
		getTitle().clearListeners();
		
		//Map list
		ListStyle listStyle = new ListStyle();
		listStyle.font = font;
		listStyle.selection = skin.getDrawable("gui/tooltip");
		mapList = new List<String>(listStyle);
		
		//Scrollpane
		ScrollPaneStyle spS = new ScrollPaneStyle();
		spS.vScroll = skin.getDrawable("gui/scrollBar");
		spS.vScrollKnob = skin.getDrawable("gui/scrollBar");
		
		ScrollPane sP = new ScrollPane(mapList, spS);
		sP.setScrollBarPositions(true, true);
		
		root.add(sP).width((float) (width * 0.95)).height((float)(height * 0.7)).row();
		
		//start game button
		LabelStyle labelStyle = new LabelStyle(font, Color.WHITE);
		Button start = GuiUtils.createButton("gui/button", null, skin);
		start.add(new Label("Start game",labelStyle));
		start.addListener(new ClickListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if(mapList.getSelected().startsWith("*")){
					game.setScreen(new GameScreen(game, internalPath + mapList.getSelected().substring(1)));	
				}else{
					game.setScreen(new GameScreen(game, externalPath + mapList.getSelected()));
				}
				
				return true;
			}
		});
		
		root.add(start).padTop(2).width(width / 2).height((float)(height * 0.08)).row();;
		
		//back to main menu
		Button back = GuiUtils.createButton("gui/button", null, skin);
		back.add(new Label("Back",labelStyle));
		back.addListener(new ClickListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				hideWindow();
				return true;
			}
		});
		
		root.add(back).padTop(2).width(width / 2).height((float)(height * 0.08)).row();
	}

	@Override
	public void showWindow(float x, float y){
		super.showWindow(x, y);
		populate();
		toFront();
		getStage().getRoot().findActor("mainMenu").setTouchable(Touchable.disabled);
	}
	
	@Override
	public void hideWindow(){
		super.hideWindow();
		getStage().getRoot().findActor("mainMenu").setTouchable(Touchable.enabled);
	}
	
	private void populate(){
		Array<String> arr = new Array<String>();
		FileHandle file = Gdx.files.internal(internalPath);
		
		if(file.exists() && file.isDirectory()){
			for(FileHandle f: file.list()){
				arr.add("*" + f.name());
			}
		}
		file = Gdx.files.external(externalPath);
		if(file.exists() && file.isDirectory()){
			for(FileHandle f: file.list()){
				arr.add(f.name());
			}
		}
		mapList.setItems(arr);
	}
	
}
