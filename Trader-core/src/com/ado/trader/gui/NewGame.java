package com.ado.trader.gui;

import java.net.URL;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.ado.trader.GameMain;
import com.ado.trader.screens.GameScreen;
import com.ado.trader.utils.FileLogger;
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
	Array<String> internalMaps;
	String internalPath = "data/maps/";
	String externalPath = "adoGame/maps/";
	
	static int width = (int) (Gdx.graphics.getWidth() * 0.5);
	static int height = (int) (Gdx.graphics.getHeight() * 0.4);

	public NewGame(final GameMain game, BitmapFont font, Skin skin, Stage stage) {
		super("Select map", width, height, font, skin, stage);
		setName("newGame");
		body.top();
		
		getTitle().clearListeners();
		
		//load maps stored inside JAR
		getCampaignMaps();
		
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
		
		body.add(sP).width((float) (width * 0.95)).height((float)(height * 0.7)).row();
		
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
		
		body.add(start).padTop(2).width(width / 2).height((float)(height * 0.08)).row();;
		
		//back to main menu
		Button back = GuiUtils.createButton("gui/button", null, skin);
		back.add(new Label("Back",labelStyle));
		back.addListener(new ClickListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				hideWindow();
				return true;
			}
		});
		
		body.add(back).padTop(2).width(width / 2).height((float)(height * 0.08)).row();
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
		
		arr.addAll(internalMaps);
		
		FileHandle file = Gdx.files.external(externalPath);
		if(file.exists() && file.isDirectory()){
			for(FileHandle f: file.list()){
				arr.add(f.name());
			}
		}
		mapList.setItems(arr);
	}
	
	private void getCampaignMaps(){
		try{
			FileLogger.writeLog("READING INTERNAL MAPS...");
			CodeSource src = this.getClass().getProtectionDomain().getCodeSource();
			internalMaps = new Array<String>();

			if( src != null ) {
				URL jar = src.getLocation();
				
				ZipInputStream zip = new ZipInputStream( jar.openStream());
				ZipEntry ze = null;

				while( ( ze = zip.getNextEntry() ) != null ) {
					String entryName = ze.getName();
					if(entryName.startsWith("data/maps/") ){
						entryName = entryName.replaceAll("data/maps/", "");
						if(entryName.endsWith("/")){
							FileLogger.writeLog("Map name: " + entryName);
							internalMaps.add("*" + entryName);
						}
					}
				}
			}
			FileLogger.writeLog("DONE READING MAPS!");
		}catch(Exception ex){
			System.out.println("Error reading maps inside JAR. Error: "+ ex);
		}
	}

}
