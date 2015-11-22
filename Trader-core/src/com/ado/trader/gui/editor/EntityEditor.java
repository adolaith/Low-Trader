package com.ado.trader.gui.editor;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.gui.BasicWindow;
import com.ado.trader.gui.GuiUtils;
import com.ado.trader.gui.ToolTip;
import com.ado.trader.rendering.SpriteManager;
import com.ado.trader.systems.AiSystem;
import com.ado.trader.utils.GameServices;
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
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter.DigitsOnlyFilter;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class EntityEditor extends BasicWindow {
	GameServices gameRes;
	
	ArrayMap<Label, Actor> dataObjects;
	Table scroll, checkBoxes;
	JsonValue componentList;
	
	LabelStyle lStyle;
	TextFieldStyle tfStyle;
	ButtonStyle bStyle;
	
	public EntityEditor(GameServices gameRes) {
		super("Entity Editor", 500, 350, gameRes.getFont(), gameRes.getSkin(), gameRes.getStage());
		this.gameRes = gameRes;
		setName("entityEditor");
		
//		setDebug(true, true);
		
		dataObjects = new ArrayMap<Label, Actor>();
		
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
				loader.saveProfile();
			}
		});
		body.add(b).width(64).left().padRight(6);
		
		//type checkboxes
		createCheckBoxes();
		
		//component list button
		ImageButton add = GuiUtils.createImageButton("gui/arrow", null, "gui/button", null, gameRes.getSkin());
		body.add(add).right().expandX().row();
		
		//scroll table
		scroll = new Table();
		
		//scrollpane
		ScrollPane sp = GuiUtils.createScrollTable(gameRes.getSkin());
		sp.setWidget(scroll);

		body.add(sp).center().colspan(4).top().expand().fillX().padTop(6).row();
		
		//component selection
		Json j = new Json();
		FileHandle file = Gdx.files.internal("data/entities/entityEditor.cfg");
		componentList = j.fromJson(null, file);
		
		//create and populate gui table with 'add component' buttons
		loadComponentButtons(add);
		
		//create default, empty view
//		scroll.add(createEntry(componentList.get("name"))).expand().fillX().row();
		
		scroll.layout();
	}
	
	private void newProfile(){
		scroll.clearChildren();
		dataObjects.clear();
		
		Table idEntry = createEntry(componentList.get("baseid")); 
		scroll.add(idEntry).expand().fillX().row();
		
//		((TextField)idEntry.findActor("id")).setText(IdGenerator.);
		
		scroll.layout();
	}
	
	private void createCheckBoxes(){
		checkBoxes = new Table();
		checkBoxes.defaults().padRight(4).left();
		
		CheckBoxStyle chkStyle = new CheckBoxStyle(gameRes.getSkin().getDrawable("gui/checkbox"), 
				gameRes.getSkin().getDrawable("gui/checkboxT"), gameRes.getFont(), Color.BLACK);
		
		CheckBox chkBox = new CheckBox("NPC", chkStyle);
		chkBox.getImageCell().size(18).padRight(4);
		chkBox.setName("npc");
		chkBox.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
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
		
		chkBox = new CheckBox("Wall", chkStyle);
		chkBox.getImageCell().size(18).padRight(4);
		chkBox.setName("wall");
		chkBox.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				CheckBox chk = checkBoxes.findActor("item");
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
		
		chkBox = new CheckBox("Item", chkStyle);
		chkBox.getImageCell().size(18).padRight(4);
		chkBox.setName("item");
		chkBox.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
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
		ScrollPane buttonList = GuiUtils.createScrollTable(gameRes.getSkin());
		buttonList.setWidget(listTable);
		buttonList.setSize(110, 100);
		buttonList.setVisible(false);
		gameRes.getStage().addActor(buttonList);
		
		add.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				if(buttonList.isVisible()){
					buttonList.setVisible(false);
				}else{
					Vector2 vec = localToStageCoordinates(new Vector2(add.getX(), add.getY()));
					buttonList.setPosition(vec.x + 30, vec.y - buttonList.getHeight() / 2);
					buttonList.setVisible(true);
				}
			}
		});
		
		for(JsonValue c = componentList.child; c != null; c = c.next){
			if(c.name.matches("name")) continue;
			
			Button button = new Button(bStyle);
			Label l = new Label(c.name, lStyle);
			final JsonValue component = c;
			button.add(l);
			button.addListener(new ClickListener(){
				public void clicked (InputEvent event, float x, float y) {
					scroll.add(createEntry(component)).expand().fillX().row();
					scroll.layout();
					buttonList.setVisible(false);
				}
			});
			
			listTable.add(button).width(105).expandX().left().padBottom(1).row();
		}
	}
	
	public Table createEntry(JsonValue componentDesc){
		Table t = new Table();
		t.padBottom(3);
		
		if(!componentDesc.name.matches("name") && !componentDesc.name.matches("baseid")){
			ImageButton del = GuiUtils.createImageButton("gui/exitIcon", null, "gui/button", null, gameRes.getSkin());
			del.addListener(new ClickListener(){
				public void clicked (InputEvent event, float x, float y) {
					t.clear();
					scroll.removeActor(t);
				}
			});
			t.add(del).padRight(4).size(24);
		}
		
		Label l = new Label(componentDesc.name, lStyle);
		if(componentDesc.has("class")){
			l.setName(componentDesc.getString("class"));
		}
		l.addListener(new ClickListener(){
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				ToolTip tt = getStage().getRoot().findActor("tooltip");
				tt.show(componentDesc.getString("desc"));
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
				ToolTip tt = getStage().getRoot().findActor("tooltip");
				tt.hide();
			}
		});
		t.add(l).expandX().fillX();
		
		JsonValue input = componentDesc.get("input");
		
		switch(input.getString("type")){
		case "textfield":
			TextField tfField = new TextField("", tfStyle);
			tfField.setMessageText(input.getString("eg"));
			tfField.setName(input.getString("name"));
			t.add(tfField).padRight(4);
			
			dataObjects.put(l, tfField);
			break;
		case "intfield":
			TextField intField = new TextField("", tfStyle);
			intField.setTextFieldFilter(new DigitsOnlyFilter());
			intField.setName(input.getString("name"));
			t.add(intField).padRight(4);
			
			dataObjects.put(l, intField);
			break;
		case "spritelist":
			SelectBox<String> spriteBox = GuiUtils.createSelectBox(gameRes.getSkin(), gameRes.getFont());
			
			Array<String> sprites = new Array<String>();
			SpriteManager spriteMan = gameRes.getRenderer().getRenderEntitySystem().getSpriteManager();
			
			//load sprite name lists
			loadSprites(spriteMan.getEntitySprites().keys(), sprites);
			loadSprites(spriteMan.getItemSprites().keys(), sprites);
			loadSprites(spriteMan.getWallSprites().keys(), sprites);
			loadSprites(spriteMan.getFeatureSprites().keys(), sprites);
			
			spriteBox.setItems(sprites);
			spriteBox.setName(input.getString("name"));
			t.add(spriteBox).padRight(4);
			
			dataObjects.put(l, spriteBox);
			break;
		case "area":
			SelectBox<String> areaBox = GuiUtils.createSelectBox(gameRes.getSkin(), gameRes.getFont());
			
			Array<String> areaList = new Array<String>();
			areaBox.setItems(areaList);
			areaBox.setName(input.getString("name"));
			t.add(areaBox).expandX().fillX().padRight(2);
			
			dataObjects.put(l, areaBox);
			
			final TextField xField = new TextField("", tfStyle);
			xField.setTextFieldFilter(new DigitsOnlyFilter());
			xField.setMessageText("x");
			t.add(xField).width(30).padRight(2);
			
			final TextField yField = new TextField("", tfStyle);
			yField.setTextFieldFilter(new DigitsOnlyFilter());
			yField.setMessageText("y");
			t.add(yField).width(30).padRight(2);
			
			Button sub = new Button(bStyle);
			l = new Label("Add", lStyle);
			sub.add(l);
			sub.addListener(new ClickListener(){
				public void clicked (InputEvent event, float x, float y) {
					if(xField.getText().isEmpty() || yField.getText().isEmpty()) return;
					
					Array<String> list = new Array<String>(areaBox.getItems());
					list.add("[" + xField.getText() + "," + yField.getText() + "]");
					areaBox.setItems(list);
					xField.setText("");
					yField.setText("");
				}
			});
			t.add(sub);
			
			break;
		case "ailist":
			SelectBox<String> aiBox = GuiUtils.createSelectBox(gameRes.getSkin(), gameRes.getFont());
			
			Array<String> aiList = new Array<String>();
			AiSystem aiSys = GameServices.getWorld().getSystem(AiSystem.class);
			for(String s: aiSys.getAllAiProfiles().keys()){
				aiList.add(s);
			}
			aiBox.setItems(aiList);
			aiBox.setName(input.getString("name"));
			t.add(aiBox);
			
			dataObjects.put(l, aiBox);
			break;
		case "animlist":
			SelectBox<String> box = GuiUtils.createSelectBox(gameRes.getSkin(), gameRes.getFont());
			
			Array<String> anims = new Array<String>();
			for(String s: EntityFactory.getAnimationPool().keys()){
				anims.add(s);
			}
			box.setItems(anims);
			box.setName(input.getString("name"));
			t.add(box);
			
			dataObjects.put(l, box);
			break;
		}
		
		return t;
	}
	
	private void loadSprites(ArrayMap.Keys<String> list, Array<String> editorList){
		for(String s: list){
			editorList.add(s);
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

	public ArrayMap<Label, Actor> getDataObjects() {
		return dataObjects;
	}
	public Table getCheckBoxes(){
		return checkBoxes;		
	}
}
