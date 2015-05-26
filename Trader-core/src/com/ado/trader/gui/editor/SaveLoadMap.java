package com.ado.trader.gui.editor;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;

import com.ado.trader.gui.SaveLoadMenu;
import com.ado.trader.gui.ToolTip;
import com.ado.trader.map.Chunk;
import com.ado.trader.map.Map;
import com.ado.trader.map.MapRegion;
import com.ado.trader.map.TileLayer;
import com.ado.trader.screens.MapEditorScreen;
import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class SaveLoadMap extends SaveLoadMenu {
	
	public SaveLoadMap(final GameServices gameRes) {
		super(gameRes, "adoGame/maps/", 400, 400);
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
				save(gameRes);
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
				load(gameRes.getMap());
				return true;
			}
		});
	}
	public void save(GameServices gameRes){
		FileHandle file = Gdx.files.external(externalPath + field.getText());
		if(file.exists()){
			file.delete();
			//over write existing save file?
//			OverwriteDialog dialog = (OverwriteDialog) gameRes.getStage().getRoot().findActor("overWrite");
//			dialog.showWindow(background.getX(), background.getY());
//			dialog.toFront();
		}
		Json j = new Json();
		try {
			j.setWriter(new FileWriter(file.file()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		gameRes.getMap().getMapLoader().saveRegion(1, 1, j);
		
		populateList();
		
//		OutputStream s = Gdx.files.external(externalPath+field.getText()).write(false);
//		ZipOutputStream z = new ZipOutputStream(s);
//		
//		
//		gameRes.getMap().saveGameState(externalPath+field.getText());
//		Gdx.app.log("SaveMap: ", "MAP SAVED!");
	}
	public void load(Map map){
		for(int x = 0; x < 3; x++){
			for(int y = 0; y < 3; y++){
				map.getRegionMap()[x][y] = null;
			}
		}
		testData(map);
	}
	private void testData(Map map){
		FileHandle f = Gdx.files.external(externalPath + field.getText());
		Json j = new Json();

		JsonValue r = j.fromJson(null, f);
		map.getRegionMap()[1][1] = map.getMapLoader().loadRegion(r, 1, 1);



		for(int x = 0; x < 3; x++){
			for(int y = 0; y < 3; y++){
				if(x == 1 && y == 1){
					continue;
				}

				MapRegion region = new MapRegion();

				for(int i = 0; i < 3; i++){
					for(int k = 0; k < 3; k++){
						Chunk chunk = new Chunk(map.getWorld());

						//set chunk to middle of region
						region.setChunk(i, k, chunk);

						TileLayer layer = chunk.getTiles();

						for(int m = 0; m < layer.getWidth(); m++){
							for(int n = 0; n < layer.getHeight(); n++){
								if(x % 2 == 0 && y % 2 == 0){
									layer.map[m][n] = map.getTilePool().createTile(6);
								}else{
									layer.map[m][n] = map.getTilePool().createTile(0);
								}
							}
						}
					}
				}

				//set region to middle of map
				map.getRegionMap()[x][y] = region;
			}
		}
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
