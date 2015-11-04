package com.ado.trader.gui.editor;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;

import com.ado.trader.gui.SaveLoadMenu;
import com.ado.trader.gui.ToolTip;
import com.ado.trader.map.Map;
import com.ado.trader.map.MapRegion;
import com.ado.trader.systems.EntityDeletionManager;
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
import com.badlogic.gdx.utils.TimeUtils;

public class SaveLoadMap extends SaveLoadMenu {
	GameServices gameRes;
	int  lastSave;
	
	public SaveLoadMap(final GameServices gameRes) {
		super(gameRes, "adoGame/maps/", 400, 400);
		setName("saveMenu");
		this.gameRes = gameRes;
		lastSave = LocalDateTime.now().getMinute();
		
		final ToolTip toolTip = (ToolTip)(gameRes.getStage().getRoot().findActor("tooltip"));

		save.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.show("Save game");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.hide();
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				save(field.getText());
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
				load();
				return true;
			}
		});
	}
	
	@Override
	public void act(float delta){
		super.act(delta);
		
		int now = LocalDateTime.now().getMinute();
		//autosave
		if(now - lastSave >= 5){
			lastSave = LocalDateTime.now().getMinute();
			
			//if more than 3 autosaves, deletes oldest autosave
			FileHandle saveDir = Gdx.files.external(externalPath);
			FileHandle[] saves = saveDir.list();
			int count = 0;
			for(FileHandle f: saves){
				if(f.name().contains("autosave")){
					count++;
				}
			}
			if(count >= 3){
				FileHandle old = null;
				for(FileHandle f: saves){
					if(!f.name().contains("autosave")) continue;
					if(old == null){
						old = f;
						continue;
					}
					
					LocalDateTime o = LocalDateTime.parse(old.name().substring(old.name().indexOf("_")));
					LocalDateTime n = LocalDateTime.parse(f.name().substring(f.name().indexOf("_")));
					if(o.compareTo(n) > 0){
						old = f;
					}
				}
				old.deleteDirectory();
			}
			
			//autosave
			save(""+ LocalDateTime.now());
		}
	}
	
	public void save(String saveName){
		int regionCount = 0;
		String regionData = "[";
		String filePath;
		Json j = new Json();
		
		//create map folder
		FileHandle file = Gdx.files.external(externalPath + saveName + "/");
		if(file.exists()){
			file.emptyDirectory();
		}else{
			file.mkdirs();
		}
		
		//copy tmp files to map folder
		FileHandle tmp = Gdx.files.external("adoGame/editor/maps/tmp");
		if(tmp.list().length > 0){
			for(FileHandle f: tmp.list()){
				
				filePath = file.file() +"/"+ f.name();
				FileHandle d = Gdx.files.external(filePath);
				f.moveTo(d);
				
				extractRegionData(filePath, regionData);
								
				regionCount++;
			}
			tmp.emptyDirectory();
		}
		
		//write active regions to map folder
		
		for(int x = 0; x < 3; x++){
			for(int y = 0; y < 3; y++){
				if(gameRes.getMap().getRegionMap()[x][y] == null) continue;
				
				filePath = file.file().getPath() + 
						"/" + gameRes.getMap().getRegionMap()[x][y].getId();
				
				try {
					j.setWriter(new FileWriter(filePath));
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				gameRes.getStreamer().saveRegion(gameRes.getMap().getRegionMap()[x][y], j);
				
				extractRegionData(filePath, regionData);
				
				regionCount++;
			}
		}
		
		//write meta file 
		try {
			j.setWriter(new FileWriter(file.file().getPath() + "/" + "meta"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		j.writeObjectStart();
		
		//this will need changing - RE - id generation
		String idGen = String.valueOf(TimeUtils.nanoTime());
		j.writeValue("id", idGen.substring(idGen.length() - 6));
		
		j.writeValue("count", regionCount);
		
		j.writeValue("regions", regionData);
		
		Vector2 camVec = IsoUtils.getColRow((int)gameRes.getCam().position.x, (int)gameRes.getCam().position.y,
				Map.tileWidth, Map.tileHeight);
		MapRegion lastRegion = gameRes.getMap().getRegion((int) camVec.x, (int) camVec.y);
		
		j.writeValue("lastRegion", lastRegion.getId());
		
		j.writeValue("camPos", gameRes.getCam().position.x +":"+ gameRes.getCam().position.y);
		
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
	
	private void extractRegionData(String regionFile, String mapHeader){
		Scanner scan = new Scanner(regionFile);
		mapHeader += "{" + scan.findInLine("id.+open.+]") + "},";
		scan.close();
	}
	
	public void load(){
		
		//clear existing data
		FileHandle tmp = Gdx.files.external("adoGame/editor/maps/tmp");
		tmp.emptyDirectory();
		GameServices.getWorld().getManager(EntityDeletionManager.class).deleteAllEntities();
		
		for(int x = 0; x < 3; x++){
			for(int y = 0; y < 3; y++){
				gameRes.getMap().getRegionMap()[x][y] = null;
			}
		}
		
		//read map
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
					if(m.has("e") && gameRes.getMap().getRegionMap()[2][2] == null){
						JsonValue e = j.fromJson(null, dir.child(m.getString("e")));
						gameRes.getStreamer().loadRegion(e, 2, 2);
					}
					if(m.has("w") && gameRes.getMap().getRegionMap()[0][2] == null){
						JsonValue w = j.fromJson(null, dir.child(m.getString("w")));
						gameRes.getStreamer().loadRegion(w, 0, 2);
					}

					break;
				case "s":
					m = j.fromJson(null, dir.child(c.asString()));
					gameRes.getStreamer().loadRegion(m, 1, 0);
					
					m = m.get("conn");
					if(m.has("e") && gameRes.getMap().getRegionMap()[2][0] == null){
						JsonValue e = j.fromJson(null, dir.child(m.getString("e")));
						gameRes.getStreamer().loadRegion(e, 2, 0);
					}
					if(m.has("w") && gameRes.getMap().getRegionMap()[0][0] == null){
						JsonValue w = j.fromJson(null, dir.child(m.getString("w")));
						gameRes.getStreamer().loadRegion(w, 0, 0);
					}
					
					break;
				case "w":
					m = j.fromJson(null, dir.child(c.asString()));
					gameRes.getStreamer().loadRegion(m, 0, 1);
					
					m = m.get("conn");
					if(m.has("n") && gameRes.getMap().getRegionMap()[0][2] == null){
						JsonValue n = j.fromJson(null, dir.child(m.getString("n")));
						gameRes.getStreamer().loadRegion(n, 0, 2);
					}
					if(m.has("s") && gameRes.getMap().getRegionMap()[0][0] == null){
						JsonValue s = j.fromJson(null, dir.child(m.getString("s")));
						gameRes.getStreamer().loadRegion(s, 0, 0);
					}
					break;
				case "e":
					m = j.fromJson(null, dir.child(c.asString()));
					gameRes.getStreamer().loadRegion(m, 2, 1);
					
					m = m.get("conn");
					if(m.has("n") && gameRes.getMap().getRegionMap()[2][2] == null){
						JsonValue n = j.fromJson(null, dir.child(m.getString("n")));
						gameRes.getStreamer().loadRegion(n, 2, 2);
					}
					if(m.has("s") && gameRes.getMap().getRegionMap()[2][0] == null){
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
	
	@Override
	public void showWindow(float x, float y, boolean loading){
		super.showWindow(x, y, loading);
		
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
