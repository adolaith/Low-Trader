package com.ado.trader.gui.editor;

import com.ado.trader.gui.BasicWindow;
import com.ado.trader.gui.GuiUtils;
import com.ado.trader.utils.GameServices;
import com.ado.trader.utils.IdGenerator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SnapshotArray;

public class EntityEditor extends BasicWindow {
	GameServices gameRes;
	
	Array<ComponentEntry> componentEntries;
	Table scroll, checkBoxes;
	JsonValue componentList;
	
	LabelStyle lStyle;
	TextFieldStyle tfStyle;
	ButtonStyle bStyle;
	
	public EntityEditor(GameServices gameRes) {
		super("Entity Editor", 560, 350, gameRes.getFont(), gameRes.getSkin(), gameRes.getStage());
		this.gameRes = gameRes;
		setName("entityEditor");
		
		body.defaults().top();
		
//		setDebug(true, true);
		
		componentEntries = new Array<ComponentEntry>();
		
		final EntityProfileLoader loader = new EntityProfileLoader(this);
		
		//styles
		lStyle = new LabelStyle(gameRes.getFont(), Color.BLACK);
		tfStyle = new TextFieldStyle();
		tfStyle.background = gameRes.getSkin().getDrawable("gui/tooltip");
		tfStyle.font = gameRes.getFont();
		tfStyle.fontColor = Color.BLACK;
		NinePatch n = new NinePatch(gameRes.getSkin().getPatch("gui/tooltip"));
		n.setColor(new Color(n.getColor().r, n.getColor().g, n.getColor().b, 0.6f));
		tfStyle.disabledBackground = new NinePatchDrawable(n);
		
		//new profile button
		bStyle = GuiUtils.setButtonStyle(gameRes.getSkin().getDrawable("gui/button"), null);
		Button b = new Button(bStyle);
		Label l = new Label("New", lStyle);
		b.add(l).left();
		b.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				newProfile();
			}
		});
		body.add(b).width(64).left().padLeft(6).padRight(6);
		
		//save/load buttons
		b = new Button(bStyle);
		l = new Label("Load", lStyle);
		b.add(l).left();
		b.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				loader.showWindow(getX(), getY(), true);
			}
		});
		body.add(b).width(64).left().padLeft(6).padRight(6);
		
		b = new Button(bStyle);
		l = new Label("Save", lStyle);
		b.add(l).left();
		b.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				TextFieldEntry name = (TextFieldEntry) getEntry("name");

				if(name.getTextField().getText().isEmpty()){
					name.getTextField().setColor(Color.RED);
					name.getTextField().getStyle().fontColor = Color.YELLOW;
					
				}else if(!isCheckBoxSelected()){
					setCheckBoxColour(Color.RED);
					
				}else{
					loader.saveProfile();
				
					name.getTextField().setColor(Color.WHITE);
					name.getTextField().getStyle().fontColor = Color.BLACK;
					setCheckBoxColour(Color.WHITE);
				}
			}
		});
		body.add(b).width(64).left().padRight(6);
		
		//type checkboxes
		createCheckBoxes();
		
		//component list button
		ImageButton add = GuiUtils.createImageButton("gui/arrowPlay", null, "gui/button", null, gameRes.getSkin());
		body.add(add).right().expandX().padRight(4).row();
		
		//main body scroll table
		scroll = new Table();
		scroll.top().padTop(4);
		scroll.defaults().top().expandX().fillX();
		
		//scrollpane
		ScrollPane sp = GuiUtils.createScrollTable(gameRes.getSkin());
		sp.setWidget(scroll);

		body.add(sp).colspan(5).top().expand().fillY().fillX().padTop(2).row();
		
		//component selection
		Json j = new Json();
		FileHandle file = Gdx.files.internal("data/entities/entityEditor.cfg");
		componentList = j.fromJson(null, file);
		
		//create and populate gui table with 'add component' buttons
		loadComponentButtons(add);
		
		scroll.layout();
	}
	
	private void newProfile(){
		scroll.clearChildren();
		componentEntries.clear();
		
		disableCheckboxes(false);
		
		scroll.add(createEntry(componentList.get("name"))).expandX().fillX().row();

		scroll.layout();
	}
	
	public boolean isCheckBoxSelected(){
		for(Actor a: checkBoxes.getChildren()){
			CheckBox b = (CheckBox) a;
			if(b.getName().matches("wall") && b.isChecked()){
				return true;
			}else if(b.getName().matches("base")){
				if(((CheckBox)checkBoxes.findActor("item")).isChecked() ||
						((CheckBox)checkBoxes.findActor("npc")).isChecked() ||
						((CheckBox)checkBoxes.findActor("ent")).isChecked()){
					return true;
				}
			}else if(b.getName().matches("spawn") && b.isChecked()){
				if(((CheckBox)checkBoxes.findActor("item")).isChecked() ||
						((CheckBox)checkBoxes.findActor("npc")).isChecked()){
					return true;
				}
			}
		}
		return false;
	}
	
	public void markCheckBoxesSelected(String baseid){
		String[] id = baseid.split("\\.");
		
		if(id[0].matches(IdGenerator.BASE_PROFILE)){
			((CheckBox)checkBoxes.findActor("base")).setChecked(true);
			
			if(id[1].charAt(0) == IdGenerator.ENTITY_ID){
				((CheckBox)checkBoxes.findActor("ent")).setChecked(true);
			}else if(id[1].charAt(0) == IdGenerator.ITEM_ID){
				((CheckBox)checkBoxes.findActor("item")).setChecked(true);
			}else if(id[1].charAt(0) == IdGenerator.NPC_ID){
				((CheckBox)checkBoxes.findActor("npc")).setChecked(true);
			}
		}else if(id[0].matches(IdGenerator.WALL)){
			((CheckBox)checkBoxes.findActor("wall")).setChecked(true);
		}else if(id[0].matches(IdGenerator.SPAWNABLE_ITEM)){
			((CheckBox)checkBoxes.findActor("spawn")).setChecked(true);
			((CheckBox)checkBoxes.findActor("item")).setChecked(true);
		}else if(id[0].matches(IdGenerator.SPAWNABLE_NPC)){
			((CheckBox)checkBoxes.findActor("spawn")).setChecked(true);
			((CheckBox)checkBoxes.findActor("npc")).setChecked(true);
		}
	}
	
	public void disableCheckboxes(boolean isDisabled){
		SnapshotArray<Actor> boxes = checkBoxes.getChildren();
		
		for(int i = 0; i < boxes.size; i++){
			CheckBox b = (CheckBox) boxes.get(i);
			if(b != null){
				b.setDisabled(isDisabled);
			}
		}
	}
	
	public String getSelectedType(){
		String type = "";
		
		for(Actor a: checkBoxes.getChildren()){
			CheckBox b = (CheckBox) a;
			
			if(b.getName().matches("wall") && b.isChecked()){
				type = IdGenerator.WALL;
			}else if(b.getName().matches("base") && b.isChecked()){
				
				type = IdGenerator.BASE_PROFILE + ".";
				
				if(((CheckBox)checkBoxes.findActor("item")).isChecked()){
					type += IdGenerator.ITEM_ID;
				}else if(((CheckBox)checkBoxes.findActor("npc")).isChecked()){
					type += IdGenerator.NPC_ID;
				}else if(((CheckBox)checkBoxes.findActor("ent")).isChecked()){
					type += IdGenerator.ENTITY_ID;
				}
			}else if(b.getName().matches("spawn") && b.isChecked()){

				if(((CheckBox)checkBoxes.findActor("item")).isChecked()){
					type = IdGenerator.SPAWNABLE_ITEM;
				}else if(((CheckBox)checkBoxes.findActor("npc")).isChecked()){
					type = IdGenerator.SPAWNABLE_NPC;
				}
			}
		}
		
		return type;
	}
	
	private void createCheckBoxes(){
		checkBoxes = new Table();
		checkBoxes.defaults().padRight(4).left();
		
		CheckBoxStyle chkStyle = new CheckBoxStyle(gameRes.getSkin().getDrawable("gui/checkbox"), 
				gameRes.getSkin().getDrawable("gui/checkboxT"), gameRes.getFont(), Color.BLACK);
		
		CheckBox chkBox = new CheckBox("Base", chkStyle);
		chkBox.getImageCell().size(18).padRight(4);
		chkBox.setName("base");
		chkBox.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				if(((CheckBox)checkBoxes.findActor("base")).isDisabled()) return;
				
				CheckBox chk = checkBoxes.findActor("wall");
				if(chk.isChecked()){
					chk.setChecked(false);
				}
				
				chk = checkBoxes.findActor("spawn");
				if(chk.isChecked()){
					chk.setChecked(false);
				}
			}
		});
		checkBoxes.add(chkBox);
		
		chkBox = new CheckBox("Wall", chkStyle);
		chkBox.getImageCell().size(18).padRight(4);
		chkBox.setName("wall");
		chkBox.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				if(((CheckBox)checkBoxes.findActor("wall")).isDisabled()) return;
				
				CheckBox chk = checkBoxes.findActor("base");
				if(chk.isChecked()){
					chk.setChecked(false);
				}
				
				chk = checkBoxes.findActor("spawn");
				if(chk.isChecked()){
					chk.setChecked(false);
				}
			}
		});
		checkBoxes.add(chkBox);
		
		chkBox = new CheckBox("Spawnable", chkStyle);
		chkBox.getImageCell().size(18).padRight(4);
		chkBox.setName("spawn");
		chkBox.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				if(((CheckBox)checkBoxes.findActor("spawn")).isDisabled()) return;
				
				CheckBox chk = checkBoxes.findActor("base");
				if(chk.isChecked()){
					chk.setChecked(false);
				}
				
				chk = checkBoxes.findActor("wall");
				if(chk.isChecked()){
					chk.setChecked(false);
				}
			}
		});
		checkBoxes.add(chkBox).row();
		
		chkBox = new CheckBox("NPC", chkStyle);
		chkBox.getImageCell().size(18).padRight(4);
		chkBox.setName("npc");
		chkBox.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				if(((CheckBox)checkBoxes.findActor("npc")).isDisabled()) return;
				
				CheckBox chk = checkBoxes.findActor("item");
				if(chk.isChecked()){
					chk.setChecked(false);
				}
				
				chk = checkBoxes.findActor("wall");
				if(chk.isChecked()){
					chk.setChecked(false);
				}
				
				chk = checkBoxes.findActor("ent");
				if(chk.isChecked()){
					chk.setChecked(false);
				}
			}
		});
		checkBoxes.add(chkBox);
		
		chkBox = new CheckBox("Entity", chkStyle);
		chkBox.getImageCell().size(18).padRight(4);
		chkBox.setName("ent");
		chkBox.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				if(((CheckBox)checkBoxes.findActor("ent")).isDisabled()) return;
				
				CheckBox chk = checkBoxes.findActor("item");
				if(chk.isChecked()){
					chk.setChecked(false);
				}
				
				chk = checkBoxes.findActor("wall");
				if(chk.isChecked()){
					chk.setChecked(false);
				}
				
				chk = checkBoxes.findActor("npc");
				if(chk.isChecked()){
					chk.setChecked(false);
				}
			}
		});
		checkBoxes.add(chkBox);
		
		chkBox = new CheckBox("Item", chkStyle);
		chkBox.getImageCell().size(18).padRight(4);
		chkBox.setName("item");
		chkBox.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				if(((CheckBox)checkBoxes.findActor("item")).isDisabled()) return;
				
				CheckBox chk = checkBoxes.findActor("wall");
				if(chk.isChecked()){
					chk.setChecked(false);
				}
				
				chk = checkBoxes.findActor("ent");
				if(chk.isChecked()){
					chk.setChecked(false);
				}
				
				chk = checkBoxes.findActor("npc");
				if(chk.isChecked()){
					chk.setChecked(false);
				}
			}
		});
		checkBoxes.add(chkBox);
		
		body.add(checkBoxes).left();
	}
	
	private void loadComponentButtons(ImageButton add){
		Table listTable = new Table();
		listTable.setBackground(gameRes.getSkin().getDrawable("gui/bGround"));
		listTable.top().padTop(4);
		ScrollPane buttonList = GuiUtils.createScrollTable(gameRes.getSkin());
		buttonList.setWidget(listTable);
		buttonList.setSize(125, getHeight() - 35);
		buttonList.setVisible(false);
		gameRes.getStage().addActor(buttonList);
		
		add.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				if(buttonList.isVisible()){
					buttonList.setVisible(false);
				}else{
					Vector2 vec = localToStageCoordinates(new Vector2(add.getX(), add.getY()));
					buttonList.setPosition(vec.x + 30, vec.y - (buttonList.getHeight() * 0.9f));
					buttonList.setVisible(true);
				}
			}
		});
		
		for(JsonValue c = componentList.child; c != null; c = c.next){
			
			Button button = new Button(bStyle);
			Label l = new Label(c.name, lStyle);
			final JsonValue component = c;
			button.add(l);
			button.addListener(new ClickListener(){
				public void clicked (InputEvent event, float x, float y) {
					scroll.add(createEntry(component)).expandX().fillX().row();
					scroll.layout();
//					buttonList.setVisible(false);
				}
			});
			
			listTable.add(button).padLeft(4).padRight(4).expandX().fillX().left().padBottom(1).row();
		}
	}
	
	public ComponentEntry createEntry(JsonValue componentProfile){
		String inputType = componentProfile.get("input").getString("type");
		ComponentEntry entry = null;
		
		switch(inputType){
		case "textfield":
			entry = new TextFieldEntry(this, componentProfile);
			break;
		case "intfield":
			entry = new TextFieldEntry(this, componentProfile);
			break;
		case "spritelist":
			entry = new SpriteEntry(this, componentProfile);
			break;
		case "area":
			entry = new AreaEntry(this, componentProfile);
			break;
		case "ailist":
			entry = new AiComponentEntry(this, componentProfile);
			break;
		case "animlist":
			entry = new AnimationEntry(this, componentProfile);
			break;
		case "extEnt":
			entry = new ExtendedEntity(this, componentProfile);
			break;
		}
		
		componentEntries.add(entry);
		
		return entry;
	}
	
	private void setCheckBoxColour(Color colour){
		for(Actor a: checkBoxes.getChildren()){
			((CheckBox)a).getImage().setColor(colour);
		}
	}

	public void showWindow(float x, float y){
		super.showWindow(x, y);
		
		newProfile();
		
		//disable game input
//		InputHandler.getMultiplexer().removeProcessor(gameRes.getInput());
	}
	
	public void hideWindow(){
		super.hideWindow();
		
		//enable game input
//		InputHandler.getMultiplexer().addProcessor(gameRes.getInput());
	}
	
	public ComponentEntry getEntry(String labelName){
		for(ComponentEntry e: componentEntries){
			if(e.getLabel().getName().matches(labelName)){
				return e;
			}
		}
		
		return null;
	}

	public Array<ComponentEntry> getEntries() {
		return componentEntries;
	}
	public Table getCheckBoxes(){
		return checkBoxes;		
	}
}
