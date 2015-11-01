package com.ado.trader.gui.editor;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;

import com.ado.trader.gui.SaveLoadMenu;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class EntityLoader extends SaveLoadMenu {
	Json json;
	EntityEditor editor;

	public EntityLoader(EntityEditor editor) {
		super(editor.gameRes, "adoGame/editor/npcs/", "data/npcs/", 250, 300);
		this.editor = editor;
		json = new Json();
		
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
		String name = ((TextField)editor.dataObjects.getValueAt(0)).getText();
		
		if(name.isEmpty()){
			Gdx.app.log("EntityEditor", "Profile needs a name before it can be saved!");
			return;
		}
		
		FileHandle file;
		file = Gdx.files.external(externalPath + name);

		//overwrite existing file. NO CONFIRMATION PROMPTED!
		if(file.exists()){
			file = Gdx.files.external(externalPath + name + "_new");
		}
		
		//set file writer
		try {
			json.setWriter(new FileWriter(file.file()));
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
		
		//splash confirmation on screen
		saveAlert("Profile Saved");
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
