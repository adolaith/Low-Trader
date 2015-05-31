package com.ado.trader.gui.editor;

import java.io.FileWriter;
import java.io.IOException;

import com.ado.trader.gui.SaveLoadMenu;
import com.ado.trader.gui.ToolTip;
import com.ado.trader.map.Chunk;
import com.ado.trader.map.Map;
import com.ado.trader.map.MapRegion;
import com.ado.trader.map.TileLayer;
import com.ado.trader.utils.GameServices;
import com.ado.trader.utils.IsoUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class SaveLoadMap extends SaveLoadMenu {
	Map map;
	
	public SaveLoadMap(final GameServices gameRes) {
		super(gameRes, "adoGame/maps/", 400, 400);
		setName("saveMenu");
		this.map = gameRes.getMap();
		
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
				load(gameRes);
				return true;
			}
		});
	}
	public void save(GameServices gameRes){
		FileHandle file = Gdx.files.external(externalPath + field.getText() + "/");
		if(file.exists()){
			file.emptyDirectory();
		}else{
			file.mkdirs();
		}
		
		Json j = new Json();
		
		for(int x = 0; x < 3; x++){
			for(int y = 0; y < 3; y++){
				if(gameRes.getMap().getRegionMap()[x][y] == null) continue;
				try {
					j.setWriter(new FileWriter(file.file().getPath() + 
							"/" + gameRes.getMap().getRegionMap()[x][y].getId()));
				} catch (IOException e) {
					e.printStackTrace();
				}
				gameRes.getStreamer().saveRegion(x, y, j);
			}
		}
		
		try {
			j.setWriter(new FileWriter(file.file().getPath() + "/" + "meta"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		j.writeObjectStart();
		j.writeValue("camPos", gameRes.getCam().position.x +":"+ gameRes.getCam().position.y);
		
		Vector2 camVec = IsoUtils.getColRow((int)gameRes.getCam().position.x, (int)gameRes.getCam().position.y,
				Map.tileWidth, Map.tileHeight);
		MapRegion lastRegion = gameRes.getMap().getRegion((int) camVec.x, (int) camVec.y);
		
		j.writeValue("lastRegion", lastRegion.getId());
		
		j.writeObjectEnd();
		
		try {
			j.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		saveAlert(gameRes.getFont());
		populateList();
		
//		OutputStream s = Gdx.files.external(externalPath+field.getText()).write(false);
//		ZipOutputStream z = new ZipOutputStream(s);
//		
//		
//		gameRes.getMap().saveGameState(externalPath+field.getText());
//		Gdx.app.log("SaveMap: ", "MAP SAVED!");
	}
	public void load(GameServices gameRes){
		for(int x = 0; x < 3; x++){
			for(int y = 0; y < 3; y++){
				map.getRegionMap()[x][y] = null;
			}
		}
		FileHandle dir = Gdx.files.external(externalPath + field.getText());
		Json j = new Json();

		JsonValue m = j.fromJson(null, dir.child("meta"));
		
		//position cam to last
		String[] pos = m.getString("camPos").split(":");
		gameRes.getCam().position.x = Float.valueOf(pos[0]);
		gameRes.getCam().position.y = Float.valueOf(pos[1]);
		
		//load last active region(where cam was at time of save)
		String loc = m.get("lastRegion").asString();
		JsonValue r = j.fromJson(null, dir.child(loc));
		
		gameRes.getStreamer().loadRegion(r, 1, 1);
		
		loadConnectedRegion(r, j, dir, gameRes);
	}
	private void loadConnectedRegion(JsonValue r, Json j, FileHandle dir, GameServices gameRes){
		JsonValue m;
		if(r.has("conn")){
			for(JsonValue c = r.get("conn").child; c != null; c = c.next){
				switch(c.name){
				case "n":
					m = j.fromJson(null, dir.child(c.asString()));
					gameRes.getStreamer().loadRegion(m, 1, 2);
					
					m = m.get("conn");
					if(m.has("e") && map.getRegionMap()[2][2] == null){
						JsonValue e = j.fromJson(null, dir.child(m.getString("e")));
						gameRes.getStreamer().loadRegion(e, 2, 2);
					}
					if(m.has("w") && map.getRegionMap()[0][2] == null){
						JsonValue w = j.fromJson(null, dir.child(m.getString("w")));
						gameRes.getStreamer().loadRegion(w, 0, 2);
					}

					break;
				case "s":
					m = j.fromJson(null, dir.child(c.asString()));
					gameRes.getStreamer().loadRegion(m, 1, 0);
					
					m = m.get("conn");
					if(m.has("e") && map.getRegionMap()[2][0] == null){
						JsonValue e = j.fromJson(null, dir.child(m.getString("e")));
						gameRes.getStreamer().loadRegion(e, 2, 0);
					}
					if(m.has("w") && map.getRegionMap()[0][0] == null){
						JsonValue w = j.fromJson(null, dir.child(m.getString("w")));
						gameRes.getStreamer().loadRegion(w, 0, 0);
					}
					
					break;
				case "w":
					m = j.fromJson(null, dir.child(c.asString()));
					gameRes.getStreamer().loadRegion(m, 0, 1);
					
					m = m.get("conn");
					if(m.has("n") && map.getRegionMap()[0][2] == null){
						JsonValue n = j.fromJson(null, dir.child(m.getString("n")));
						gameRes.getStreamer().loadRegion(n, 0, 2);
					}
					if(m.has("s") && map.getRegionMap()[0][0] == null){
						JsonValue s = j.fromJson(null, dir.child(m.getString("s")));
						gameRes.getStreamer().loadRegion(s, 0, 0);
					}
					break;
				case "e":
					m = j.fromJson(null, dir.child(c.asString()));
					gameRes.getStreamer().loadRegion(m, 2, 1);
					
					m = m.get("conn");
					if(m.has("n") && map.getRegionMap()[2][2] == null){
						JsonValue n = j.fromJson(null, dir.child(m.getString("n")));
						gameRes.getStreamer().loadRegion(n, 2, 2);
					}
					if(m.has("s") && map.getRegionMap()[2][0] == null){
						JsonValue s = j.fromJson(null, dir.child(m.getString("s")));
						gameRes.getStreamer().loadRegion(s, 2, 0);
					}
					break;
				}
			}
		}
	}
	
	//splashes 'game saved' label across screen before fading
	private void saveAlert(BitmapFont font){
		LabelStyle lStyle = new LabelStyle(font, Color.WHITE);
		Label l = new Label("Map Saved", lStyle);
		l.setFontScale(3);
		l.setWidth(200);
		l.setHeight(40);
		getStage().addActor(l);
		l.toFront();
		l.setPosition((Gdx.graphics.getWidth() / 2) - (l.getWidth()), Gdx.graphics.getHeight() / 2);
		l.addAction(Actions.sequence(Actions.alpha(0, 2), Actions.removeActor()));
	}
	
	private void testData(Map map){
		
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
