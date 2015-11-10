package com.ado.trader.gui.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.ado.trader.gui.BasicWindow;
import com.ado.trader.gui.SaveLoadMenu;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.TimeUtils;

public class EntityLoader extends SaveLoadMenu {
	Json json;
	EntityEditor editor;
	RandomXS128 randGen;
	int modPrefix;
	
	OverwriteWindow overwrite;
	
	String fileExt = ".dat";

	public EntityLoader(EntityEditor editor) {
		super(editor.gameRes, "adoGame/editor/npcs/", "data/npcs/", 250, 300);
		this.editor = editor;
		json = new Json();
		randGen = new RandomXS128();
		modPrefix = 0;
		
		overwrite = new OverwriteWindow("Overwrite existing entity?", 320, 100, 
				editor.gameRes.getFont(), editor.gameRes.getSkin(), editor.gameRes.getStage());
		
		FileHandle file = Gdx.files.external(externalPath);
		if(!file.exists()){
			file.mkdirs();
		}
		
		load.addListener(new ClickListener() {
			public void clicked (InputEvent event, float x, float y) {
				if(folderList.getSelected() != null){
					FileHandle file;
					
					if(folderList.getSelected().startsWith("*")){
						file = Gdx.files.internal(internalPath + folderList.getSelected().substring(1));
					}else{
						file = Gdx.files.external(externalPath + folderList.getSelected());
					}
					
					Json json = new Json();
					JsonValue profile = json.fromJson(null, file);
					
					loadProfile(profile);
					
					hideWindow();
				}
			}
		});
		
	}
	private void loadProfile(JsonValue profile){
		editor.scroll.clearChildren();
		editor.dataObjects.clear();
		
		for(JsonValue e = profile.child; e != null; e = e.next()){
			
			Table t = editor.createEntry(editor.componentList.get(e.name));
			
			for(JsonValue d = e.child; d != null; d = d.next()){
				if(d.name.matches("class")) continue;
				
				Actor a = t.findActor(d.name);
				if(a instanceof TextField){
					((TextField)a).setText(d.asString());
					
					if(e.name.matches("baseid")){
						editor.setId(d.asString());
						((TextField)a).setDisabled(true);
					}
				}else if(a instanceof SelectBox<?>){
					if(d.name.matches("area")){
						Array<String> list = new Array<String>();
						for(String s: d.asStringArray()){
							list.add(s);
						}
						((SelectBox<String>)a).setItems(list);
					}else{
						((SelectBox<String>)a).setSelected(d.asString());	
					}
					
				}
			}
			editor.scroll.add(t).expand().fillX().row();
		}
		editor.scroll.layout();
	}
	
	public void saveProfile(){
		String id =  editor.getId();
		
		int prefix = 0;
		
		if(((CheckBox)editor.getCheckBoxes().findActor("ent")).isChecked()){
			prefix = 1;
		}else if(((CheckBox)editor.getCheckBoxes().findActor("item")).isChecked()){
			prefix = 2;
		}else if(((CheckBox)editor.getCheckBoxes().findActor("wall")).isChecked()){
			prefix = 3;
		}
		
		if(!id.contains(".") || prefix != 0){
			if(prefix == 0){
				if(modPrefix == 0){
					while(prefix <=3 || prefix > 99){
						prefix = randGen.nextInt(100);
					}
					modPrefix = prefix;
				}else{
					prefix = modPrefix;				
				}
			}else{
				id = id.split("\\.")[1];
			}
			
			id = String.valueOf(prefix) + "." + id;
			editor.setId(id);
		}
		
		FileHandle file;
		file = Gdx.files.external(externalPath + id + fileExt);

		//overwrite existing file. NO CONFIRMATION PROMPTED!
		if(file.exists()){
			overwrite.showWindow(Gdx.graphics.getWidth() / 2 , Gdx.graphics.getHeight() / 2, id);
			overwrite.toFront();
		}else{
			createFile(file.file());
			//splash confirmation on screen
			saveAlert("Profile Saved");
		}
	}
	
	private void createFile(File file){
		//set file writer
		try {
			json.setWriter(new FileWriter(file));
		} catch (IOException e) {
			Gdx.app.log("entityLoader: ", "Error writing file!");
			e.printStackTrace();
		}
		
		//write editor data
		json.writeObjectStart();
		
		
		for(Label l: editor.dataObjects.keys()){
			json.writeObjectStart(l.getText().toString());
			json.writeValue("class", l.getName());
			
			Actor a = editor.dataObjects.get(l);
			
			if(a instanceof TextField){
				TextField tf = (TextField) a;
				if(l.getText().toString().matches("baseid")){
					tf.setText(editor.getId());
				}
				json.writeValue(tf.getName(), tf.getText());
				
			}else if(a instanceof SelectBox<?>){
				SelectBox<String> box = (SelectBox<String>) a;
				if(l.getText().toString().matches("area")){
					json.writeArrayStart(box.getName());
					for(String s: box.getItems()){
						json.writeValue(s);
					}
					json.writeArrayEnd();
				}else{
					json.writeValue(box.getName(), box.getSelected());	
				}
			}
			json.writeObjectEnd();
		}
		
		json.writeObjectEnd();
		
		//close writer
		try {
			json.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//file overwrite confirmation window
	private class  OverwriteWindow extends BasicWindow{
		
		public OverwriteWindow(String title, float width, float height, BitmapFont font, Skin skin, Stage stage){
			super(title, width, height, font, skin, stage);
			
			TextButtonStyle style = new TextButtonStyle();
			style.up = skin.getDrawable("gui/button");
			style.font = font;
			style.fontColor = Color.BLACK;
			
			TextButton b = new TextButton("Yes", style);
			b.setName("yes");
			
			this.body.add(b).center();
			
			b = new TextButton("No", style);
			b.setName("no");
			
			this.body.add(b).center();
		}
		
		public void showWindow(float x, float y, String id){
			this.showWindow(x, y);
			
			this.body.findActor("yes").addListener(new ClickListener(){
				public void clicked (InputEvent event, float x, float y) {
					FileHandle file = Gdx.files.external(externalPath + id + fileExt);
					createFile(file.file());
					
					hideWindow();
					
					//splash confirmation on screen
					saveAlert("Profile Saved");
				}
			});
			
			this.body.findActor("no").addListener(new ClickListener(){
				public void clicked (InputEvent event, float x, float y) {
					String newId =  String.valueOf(TimeUtils.nanoTime());
					newId = newId.substring(newId.length() - 6);
					
					String[] split = id.split("\\.");
					
					newId = split[0] + "." + newId;
					editor.setId(newId);
					
					FileHandle file = Gdx.files.external(externalPath + newId + fileExt);
					createFile(file.file());
					
					hideWindow();
					
					//splash confirmation on screen
					saveAlert("Profile Saved");
				}
			});
		}
		
		@Override
		public void hideWindow(){
			setVisible(false);
			this.body.findActor("yes").clearListeners();
			this.body.findActor("no").clearListeners();
		}
	}
	
	@Override
	public void showWindow(float x, float y, boolean loading){
		super.showWindow(x, y, loading);
		
		populateList(loading);
		
	}
	@Override
	protected void populateList(boolean loading){
		Array<String> arr = new Array<String>();
		FileHandle file;
		
		//reads internal profiles. writing to internal dir unsupported
		if(loading){
			file = Gdx.files.internal(internalPath + "files.txt");
			if(file.exists()){
				BufferedReader reader = file.reader(500);
				try{
					for(String p = reader.readLine(); p != null; p = reader.readLine()){
						arr.add("*" + p);
					}
				}catch(Exception ex){
					Gdx.app.log("EntProfileLoader: ", "ERROR READING INTERNAL  PROFILES: "+ ex);
				}
			}
		}
		
		//read external profiles. user made profiles saved here
		file = Gdx.files.external(externalPath);
		if(file.exists() && file.isDirectory()){
			for(FileHandle f: file.list()){
				arr.add(f.name());
			}
		}
		
		folderList.setItems(arr);
	}
}
