package com.ado.trader.gui.editor;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;

import com.ado.trader.gui.SaveLoadMenu;
import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

public class NpcLoader extends SaveLoadMenu {
	Json json;
	NpcEditor editor;

	public NpcLoader(GameServices gameRes, NpcEditor editor) {
		super(gameRes, "adoGame/editor/npcs/", "data/npcs/", 250, 300);
		this.editor = editor;
		json = new Json();
		
		save.addListener(new ClickListener() {
			public void clicked (InputEvent event, float x, float y) {
				FileHandle file;
				if(field.getText().startsWith("*")){
					file = Gdx.files.external(externalPath + field.getText().substring(1));
				}else{
					file = Gdx.files.external(externalPath + field.getText());
				}
				//overwrite existing file. NO CONFIRMATION PROMPTED!
				if(file.exists()){
					file.delete();
				}
				
				//set file writer
				try {
					json.setWriter(new FileWriter(file.file()));
				} catch (IOException e) {
					Gdx.app.log("NpcLoader: ", "Error writing file!");
					e.printStackTrace();
				}
				
				saveProfile();
				
				//close writer
				try {
					json.getWriter().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				populateList(false);
				
				//splash confirmation on screen
				saveAlert("Profile Saved");
			}
		});
		
		load.addListener(new ClickListener() {
			public void clicked (InputEvent event, float x, float y) {
				
			}
		});
		
	}
	private void saveProfile(){
		//write editor data
		json.writeObjectStart();
		
		for(Actor a: editor.dataObjects){
			if(a instanceof TextField){
				TextField tf = (TextField) a;
				if(!tf.isDisabled()){
					json.writeValue(tf.getName(), tf.getText());
				}
			}else if(a instanceof SelectBox<?>){
				SelectBox<String> box = (SelectBox<String>) a;
//				if(box.)
				json.writeValue(box.getName(), box.getSelected());
			}
		}
		
		
		json.writeObjectEnd();

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
