package com.ado.trader.gui.editor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.gui.BasicWindow;
import com.ado.trader.gui.SaveLoadMenu;
import com.ado.trader.utils.IdGenerator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class EntityProfileLoader extends SaveLoadMenu {
	Json json;
	EntityEditor editor;
	
	OverwriteWindow overwrite;
	
	String fileExt = ".dat";

	public EntityProfileLoader(EntityEditor editor) {
		super(editor.gameRes, "adoGame/editor/entities/", "data/entities/", 250, 300);
		this.editor = editor;
		json = new Json();
		
		overwrite = new OverwriteWindow("Overwrite existing entity?", 320, 100, 
				editor.gameRes.getFont(), editor.gameRes.getSkin(), editor.gameRes.getStage());
		
		FileHandle file = Gdx.files.external(externalPath);
		if(!file.exists()){
			file.mkdirs();
		}
		
		load.addListener(new ClickListener() {
			public void clicked (InputEvent event, float x, float y) {
				if(folderList.getSelected() != null){
					String[] selected = folderList.getSelected().split("\\.");
					
					JsonValue profile = EntityFactory.getEntityData().get(
							selected[0]).get(selected[1]);
					
					loadProfile(profile);
					
					hideWindow();
				}
			}
		});
	}
	
	private void loadProfile(JsonValue profile){
		//clear editor
		editor.scroll.clearChildren();
		editor.componentEntries.clear();
		editor.scroll.layout();
		
		for(Actor a: editor.checkBoxes.getChildren()){
			((CheckBox) a).setChecked(false);
		}
		
		//load baseid and name entries first. personal preference 
		JsonValue baseId = profile.get("baseid");
		JsonValue componentProfile = editor.componentList.get(baseId.name);
		
		ComponentEntry entry = editor.createEntry(componentProfile);
		entry.load(baseId);
		editor.scroll.add(entry).row();
		
		JsonValue name = profile.get("name");
		componentProfile = editor.componentList.get(name.name);
		
		entry = editor.createEntry(componentProfile);
		entry.load(name);
		editor.scroll.add(entry).row();
		
		//mark the relevant type checkbox in the editor
		editor.markCheckBoxesSelected(profile.getString("baseid"));
		
		//load component entries
		for(JsonValue e = profile.child; e != null; e = e.next()){
			if(e.name.matches(baseId.name) ||
					e.name.matches(name.name)) continue;
			
			componentProfile = editor.componentList.get(e.name);
			
			entry = editor.createEntry(componentProfile);
			entry.load(e);
			
			editor.scroll.add(entry).row();
		}
		
		editor.scroll.layout();
	}
	
	public void saveProfile(){
		String id = "";
		String chkboxType = editor.getSelectedType();
		TextFieldEntry baseIdEntry = null;
		
		for(ComponentEntry e: editor.componentEntries){
			if(e.getLabel().getName().matches("baseid")){
				baseIdEntry = (TextFieldEntry) e;
				id = baseIdEntry.getTextField().getText();			
			}
		}
		
		if(baseIdEntry != null){
			/*
			 * check to see if the entity type(denoted by its id)
			 *  has changed via checkbox selection
			 */
			
			String[] idSplit = id.split("\\.");
			
			if(chkboxType.contains(".")){
				String[] chkBoxSplit = chkboxType.split("\\.");
				
				if(idSplit[0].matches(chkBoxSplit[0])){
					if(!idSplit[1].startsWith(chkBoxSplit[1])){
						do{
							id = chkboxType + IdGenerator.getShortId();
						}while(checkExistingIds(id));
					}
				}else{
					do{
						id = chkboxType + IdGenerator.getShortId();
					}while(checkExistingIds(id));
				}
			}else if(!idSplit[0].matches(chkboxType)){
				do{
					id = chkboxType + "." + IdGenerator.getUniqueId();
				}while(checkExistingIds(id));
				
			}
			
			baseIdEntry.getTextField().setText(id);
		}else{
			//no base id entry found, gen id + add component to list
			if(chkboxType.contains(".")){
				do{
					id = chkboxType + IdGenerator.getShortId();
				}while(checkExistingIds(id));
			}else{
				do{
					id = chkboxType + "." + IdGenerator.getUniqueId();
				}while(checkExistingIds(id));
			}
			createBaseIdComponent(id);
		}
		
		//NEEDS TO CHECK IF INTERNAL PROFILES CONTAIN NEW ID
		
		FileHandle file;
		file = Gdx.files.external(externalPath + id + fileExt);

		if(file.exists()){
			overwrite.showWindow(Gdx.graphics.getWidth() / 2 , Gdx.graphics.getHeight() / 2, id);
			overwrite.toFront();
		}else{
			createFile(file.file());
			
			//splash confirmation on screen
			saveAlert("Profile Saved");
		}
	}
	
	private boolean checkExistingIds(String newId){
		String[] split = newId.split("\\.");
		for(String entry: EntityFactory.getEntityData().get(split[0]).keys){
			if(entry.matches(split[1])){
				return true;
			}
		}
		return false;
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
		
		for(ComponentEntry e: editor.componentEntries){
			e.save(json);
		}
		
		json.writeObjectEnd();
		
		//close writer
		try {
			json.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void createBaseIdComponent(String id){
		TextFieldEntry idEntry = (TextFieldEntry) editor.createEntry(editor.componentList.get("baseid"));
		idEntry.getTextField().setText(id);
		editor.scroll.add(idEntry).expandX().fillX().row();
	}
	
	@Override
	public void showWindow(float x, float y, boolean loading){
		super.showWindow(x, y, loading);
		
		populateList(loading);
		
	}
	
	@Override
	protected void populateList(boolean loading){
		Array<String> list = new Array<String>();
		
		for(ArrayMap<String, JsonValue> entities: EntityFactory.getEntityData().values()){
			for(JsonValue e: entities.values()){
				list.add(e.getString("baseid"));
			}
		}	
		
		folderList.setItems(list);
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
						String newId =  "";
						String chkboxType = editor.getSelectedType();
						
						if(chkboxType.contains(".")){
							do{
								newId = chkboxType + IdGenerator.getShortId();
							}while(checkExistingIds(newId));
						}else{
							do{
								newId = chkboxType + "." + IdGenerator.getUniqueId();
							}while(checkExistingIds(newId));
						}
						
						for(ComponentEntry e: editor.componentEntries){
							if(e.getLabel().getName().matches("baseid")){
								((TextFieldEntry)e).getTextField().setText(newId);
							}
						}
						
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
}
