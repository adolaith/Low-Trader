package com.ado.trader.gui.game;

import com.ado.trader.gui.SaveLoadMenu;
import com.ado.trader.gui.ToolTip;
import com.ado.trader.screens.GameScreen;
import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class SaveLoadGame extends SaveLoadMenu {

	public SaveLoadGame(final GameServices gameRes) {
		super(gameRes, "adoGame/saves/");
		setName("saveMenu");
		
		final ToolTip toolTip = (ToolTip)(gameRes.getStage().getRoot().findActor("tooltip"));

		save.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.show("Save game");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.hide();
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				FileHandle file = Gdx.files.external(externalPath + field.getText());
				if(file.exists()){
					//over write existing save?
					OverwriteDialog dialog = (OverwriteDialog) gameRes.getStage().getRoot().findActor("overWrite");
					dialog.showWindow(background.getX(), background.getY());
					dialog.toFront();
				}else{
					gameRes.getMap().saveGameState(externalPath+field.getText());
					Gdx.app.log("SaveGame: ", "Game SAVED!");
				}
				populateList();
				return true;
			}
		});
		
		delete.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.show("Delete save game");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.hide();
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if(folderList.getSelected() != null){
					FileHandle file = Gdx.files.external(externalPath + folderList.getSelected());
					file.deleteDirectory();
					populateList();
				}
				return true;
			}
		});

		load.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.show("Load map");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.hide();
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				FileHandle file = Gdx.files.external(externalPath + folderList.getSelected());
				if(file.exists()){
					GameScreen.getGame().setScreen(new GameScreen(GameScreen.getGame(), externalPath + folderList.getSelected()));
				}
				return true;
			}
		});
	}
	
	@Override
	public void show(boolean loading){
		super.show(loading);
		populateList();
	}
	
	private void populateList(){
		FileHandle file = Gdx.files.external(externalPath);
		if(file.exists() && file.isDirectory()){
			Array<String> arr = new Array<String>();
			for(FileHandle f: file.list()){
				arr.add(f.name());
			}
			folderList.setItems(arr);
		}
	}
}
