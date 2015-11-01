package com.ado.trader.gui.editor;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;

import com.ado.trader.gui.SaveLoadMenu;
import com.ado.trader.systems.AiSystem;
import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class AiProfileLoader extends SaveLoadMenu {
	AiEditorWindow editor;
	Json json;

	public AiProfileLoader(final GameServices gameRes, final AiEditorWindow editor) {
		super(gameRes, "adoGame/editor/ai/", "data/ai/", 400, 400);
		this.editor = editor;
		json = new Json();
		
		save.addListener(new ClickListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
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
					Gdx.app.log("AiLoader: ", "Error writing file!");
					e.printStackTrace();
				}
				
				//write editor data
				json.writeObjectStart();
				writeEntry(AiEditorWindow.rootNode, json);
				json.writeObjectEnd();
				
				//close writer
				try {
					json.getWriter().close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				AiSystem aiSys = gameRes.getWorld().getSystem(AiSystem.class);
				aiSys.getAllAiProfiles().put(file.name(), json.fromJson(null, file));
				
				populateList(false);
				
				//splash confirmation on screen
				saveAlert("Profile Saved");
				
				return true;
			}
		});
		
		load.addListener(new ClickListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				
				if(folderList.getSelected() != null){
					FileHandle file;
					
					if(folderList.getSelected().startsWith("*")){
						file = Gdx.files.internal(internalPath + folderList.getSelected().substring(1));
					}else{
						file = Gdx.files.external(externalPath + folderList.getSelected());
					}
					
					Json json = new Json();
					JsonValue profile = json.fromJson(null, file);
					profile = profile.child;
					
					loadProfile(profile, gameRes);
					
					AiEditorWindow.refreshLayout();
					
					hideWindow();
				}
				
				return true;
			}
		});
		
	}
	
	private void loadProfile(JsonValue profile, final GameServices gameRes){
		AiEditorWindow.rootNode = new ParentEntry(profile, true, null, gameRes);
		
		if(profile.has("deco")){
			for(JsonValue d = profile.get("deco").child; d != null; d = d.next){
				AiEditorWindow.rootNode.addDecoration(new DecorationEntry(d, true, AiEditorWindow.rootNode, gameRes));
			}
		}
		
		if(profile.has("children")){
			for(JsonValue c = profile.get("children").child; c != null; c = c.next){
				loadEntry(c, AiEditorWindow.rootNode, gameRes);
			}
		}
	}
	
	private void loadEntry(JsonValue entry, ParentEntry parent, final GameServices gameRes){
		AiEntry e;
		if(entry.has("children")){
			e = new ParentEntry(entry, true, parent, gameRes);
		}else{
			e = new LeafEntry(entry, true, parent, gameRes);
		}
		parent.addChildTask(e);
		
		if(entry.has("deco")){
			for(JsonValue d = entry.get("deco").child; d != null; d = d.next){
				e.addDecoration(new DecorationEntry(d, true, AiEditorWindow.rootNode, gameRes));
			}
		}
		
		if(entry.has("children")){
			for(JsonValue c = entry.get("children").child; c != null; c = c.next){
				loadEntry(c, (ParentEntry)e, gameRes);
			}
		}
		
	}
	
	//save profile
	private void writeEntry(AiEntry entry, Json file){
		//node itself
		file.writeObjectStart(entry.getName());
		if(entry.paramField != null){
			if(!entry.paramField.getText().isEmpty()){
				file.writeValue("param", entry.paramField.getText());
			}
		}
		
		//node decorations
		if(!(entry instanceof DecorationEntry)){
			if(entry.decorations.size > 0){
				file.writeObjectStart("deco");
				for(DecorationEntry d: entry.decorations){
					file.writeObjectStart(d.getName());
					if(d.paramField != null){
						file.writeValue("param", d.paramField.getText());
					}
					file.writeObjectEnd();
				}
				file.writeObjectEnd();
			}
		}
		
		if(entry instanceof ParentEntry){
			file.writeObjectStart("children");
			ParentEntry p = (ParentEntry) entry;
			for(AiEntry c: p.childTasks){
				writeEntry(c, file);
			}
			file.writeObjectEnd();
		}
		file.writeObjectEnd();
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
			file = Gdx.files.internal(internalPath + "Profiles.txt");
			if(file.exists()){
				BufferedReader reader = file.reader(500);
				try{
					for(String p = reader.readLine(); p != null; p = reader.readLine()){
						arr.add("*" + p);
					}
				}catch(Exception ex){
					Gdx.app.log("AILoader: ", "ERROR READING INTERNAL AI PROFILES: "+ ex);
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
