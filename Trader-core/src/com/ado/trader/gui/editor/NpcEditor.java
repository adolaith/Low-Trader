package com.ado.trader.gui.editor;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.gui.BasicWindow;
import com.ado.trader.gui.GuiUtils;
import com.ado.trader.systems.AiSystem;
import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;

public class NpcEditor extends BasicWindow {
	GameServices gameRes;
	
	Array<Actor> dataObjects;
	
	LabelStyle lStyle;
	TextFieldStyle tfStyle;
	ButtonStyle bStyle;

	public NpcEditor(GameServices gameRes) {
		super("Entity Editor", 350, 250, gameRes.getFont(), gameRes.getSkin(), gameRes.getStage());
		this.gameRes = gameRes;
		setName("entityEditor");
		
		dataObjects = new Array<Actor>();
		
		//styles
		lStyle = new LabelStyle(gameRes.getFont(), Color.BLACK);
		tfStyle = new TextFieldStyle();
		tfStyle.background = gameRes.getSkin().getDrawable("gui/tooltip");
		tfStyle.font = gameRes.getFont();
		tfStyle.fontColor = Color.BLACK;
		NinePatch n = new NinePatch(gameRes.getSkin().getPatch("gui/tooltip"));
		n.setColor(new Color(n.getColor().r, n.getColor().g, n.getColor().b, 0.6f));
		tfStyle.disabledBackground = new NinePatchDrawable(n);
		
		final NpcLoader loader = new NpcLoader(gameRes, this);
		
		//save/load buttons
		bStyle = GuiUtils.setButtonStyle(gameRes.getSkin().getDrawable("gui/button"), null);
		Button b = new Button(bStyle);
		Label l = new Label("Load", lStyle);
		b.add(l).left();
		b.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				loader.showWindow(getX(), getY(), true);
			}
		});
		body.add(b).right();
		
		b = new Button(bStyle);
		l = new Label("Save", lStyle);
		b.add(l).left();
		b.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				loader.showWindow(getX(), getY(), false);
			}
		});
		body.add(b).left().row();
		
		//dropdown list of anim types
		SelectBox<String> box = createSelectBox(gameRes.getSkin(), gameRes.getFont());
		
		Array<String> anims = new Array<String>();
		for(String s: EntityFactory.getAnimationPool().keys()){
			anims.add(s);
		}
		box.setItems(anims);
		box.setName("anim");
		body.add(box).center().colspan(2).width(200).height(18).padBottom(2).row();

		//scroll table
		Table t = new Table();
		t.defaults().padTop(2);
		
		//entity name
		t.add();
		l = new Label("Name: ", lStyle);
		t.add(l).expandX().left().height(18);
		
		TextField tf = new TextField("", tfStyle);
		tf.setName("name");
		dataObjects.add(tf);
		t.add(tf).expandX().left().height(18).row();
		
		//ai profile
		//checkbox button
		bStyle = new ButtonStyle(gameRes.getSkin().newDrawable("gui/checkbox"), null, gameRes.getSkin().newDrawable("gui/checkboxT"));
		final Button aiCheck = new Button(bStyle);
		t.add(aiCheck).size(18).center();
		
		l = new Label("Ai Profile: ", lStyle);
		t.add(l).expandX().left().height(18);
		
		final SelectBox<String> aiBox = createSelectBox(gameRes.getSkin(), gameRes.getFont());
		Array<String> profiles = new Array<String>();
		AiSystem aiSys = gameRes.getWorld().getSystem(AiSystem.class);
		for(String s: aiSys.getAllAiProfiles().keys()){
			profiles.add(s);
		}
		aiBox.setItems(profiles);
		aiBox.setDisabled(true);
		
		aiBox.setName("ai");
		dataObjects.add(aiBox);
		
		t.add(aiBox).expandX().left().height(18).row();
		t.add();
		
		l = new Label("Movement speed: ", lStyle);
		t.add(l).expandX().left().height(18);

		final TextField field = new TextField("", tfStyle);
		field.setName("move");
		field.setDisabled(true);
		field.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());

		dataObjects.add(field);
		
		t.add(field).expandX().left().height(18).row();
		
		aiCheck.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				if(aiCheck.isChecked()){
					aiBox.setDisabled(false);
					field.setDisabled(false);
				}else{
					aiBox.setDisabled(true);
					field.setText("");
					field.setDisabled(true);
				}
			}
		});
		
		//health
		createEntry("Health: ", "hp", true, t);
		
		//money 
		createEntry("Money: ", "money", true, t);
				
		//inventory 
		createEntry("Inventory size: ", "inven", true, t);
				
		//group 
		createEntry("Group: ", "group", false, t);
				
		//tag 
		createEntry("Tag: ", "tag", false, t);
		
		//scrollpane
		ScrollPane sp = GuiUtils.createScrollTable(gameRes.getSkin());
		sp.setWidget(t);
		
		body.add(sp).left().colspan(2).top().expand().row();
		
	}
	
	
	
	private void createEntry(String label, String name,boolean digitsOnly, Table t){
		final Button b = new Button(bStyle);
		t.add(b).size(18).center();

		Label l = new Label(label, lStyle);
		t.add(l).expandX().left().height(18);

		final TextField field = new TextField("", tfStyle);
		field.setName(name);
		field.setDisabled(true);
		
		if(digitsOnly){
			field.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
		}

		dataObjects.add(field);
		
		b.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				if(b.isChecked()){
					field.setDisabled(false);
				}else{
					field.setText("");
					field.setDisabled(true);
				}
			}
		});

		t.add(field).expandX().left().height(18).row();
	}

	private SelectBox<String> createSelectBox(Skin skin, BitmapFont font){
		ScrollPaneStyle styleScroll = new ScrollPaneStyle();
		styleScroll.vScroll = skin.getDrawable("gui/scrollBar");
		styleScroll.vScrollKnob = skin.getDrawable("gui/scrollBar");
		
		ListStyle styleList = new ListStyle(font, Color.BLUE, Color.GRAY, 
				skin.getDrawable("gui/tooltip"));
		styleList.background = skin.getDrawable("gui/tooltip");
		
		SelectBoxStyle styleSelect = new SelectBoxStyle(font, Color.BLACK,
				skin.getDrawable("gui/tooltip"), styleScroll, styleList);
		
		NinePatch n = new NinePatch(gameRes.getSkin().getPatch("gui/tooltip"));
		n.setColor(new Color(n.getColor().r, n.getColor().g, n.getColor().b, 0.6f));
		styleSelect.backgroundDisabled = new NinePatchDrawable(n);
		
		return new SelectBox<String>(styleSelect);
	}
	
	public void showWindow(float x, float y){
		super.showWindow(x, y);
		
		//disable game input
//		InputHandler.getMultiplexer().removeProcessor(gameRes.getInput());
	}
	
	public void hideWindow(){
		super.hideWindow();
		
		//enable game input
//		InputHandler.getMultiplexer().addProcessor(gameRes.getInput());
	}
	
}
