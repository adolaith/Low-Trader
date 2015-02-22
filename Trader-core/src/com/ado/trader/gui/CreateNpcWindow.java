package com.ado.trader.gui;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.entities.components.Animation;
import com.ado.trader.entities.components.Health;
import com.ado.trader.entities.components.Hunger;
import com.ado.trader.entities.components.Money;
import com.ado.trader.entities.components.Position;
import com.ado.trader.map.Map;
import com.ado.trader.utils.GameServices;
import com.ado.trader.utils.IsoUtils;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

public class CreateNpcWindow extends BasicWindow{
	float x,y;
	int type;
	Map map;
	EntityFactory entities;

	public CreateNpcWindow(GameServices gameRes) {
		super("New Npc", 270, 34 * 2, gameRes.getFont(), gameRes.getSkin(), gameRes.getStage());
		this.entities = gameRes.getEntities();
		this.map = gameRes.getMap();
		Cell<Table> c = functionTable.getCell(root);
		c.width(width - 10);
	}
	public void showWindow(final float x, final float y){
		this.x = x;
		this.y = y;
		
		LabelStyle ls = new LabelStyle(font, Color.WHITE);
		Label l = new Label("Select npc: ", ls);
		root.add(l).left();
		
		//select box for npc type goes here
		ScrollPaneStyle styleScroll = new ScrollPaneStyle();
		styleScroll.vScroll = skin.getDrawable("gui/scrollBar");
		styleScroll.vScrollKnob = skin.getDrawable("gui/scrollBar");
		
		ListStyle styleList = new ListStyle(font, Color.YELLOW, Color.GRAY, 
				skin.getDrawable("gui/guiBG"));
		styleList.background = skin.getDrawable("gui/guiBG");
		
		SelectBoxStyle styleSelect = new SelectBoxStyle(font, Color.BLACK,
				skin.getDrawable("gui/guiBG"), styleScroll, styleList);
		
		final SelectBox<String> box = new SelectBox<String>(styleSelect);
		root.add(box).height(25).right().row();
		
		Array<String> list = new Array<String>();
		ArrayMap<Integer, ArrayMap<String, String>> npcs = entities.getNpcs();
		//selectbox list
		for(Integer npcId: npcs.keys()){
			String s = npcId + ":" + npcs.get(npcId).get("animation"); 
			list.add(s);
		}
		box.setItems(list);
		
		final Table body = new Table();
		root.add(body).left().colspan(2).padTop(2).fill().expand();
		
		//updates window body according to selection
		box.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				body.clearChildren();
				
				String[] selected = box.getSelected().split(":");
				type = Integer.valueOf(selected[0]);
				if(selected[1].matches("human")){
					selectedHuman(body);
					updateSize(270, 28 * (body.getRows() + 2));
				}else{
					TextButtonStyle bStyle = new TextButtonStyle();
					bStyle.font = font;
					bStyle.up = skin.getDrawable("gui/button");
					TextButton b = new TextButton("Create " + selected[1] + " NPC", bStyle);
					b.addListener(new ChangeListener() {
						public void changed(ChangeEvent event, Actor actor) {
							Entity e = EntityFactory.createEntity(type);
							
							Vector2 tmp = IsoUtils.getColRow((int)x, (int)y, map.getTileWidth(), map.getTileHeight());
							
							e.getComponent(Position.class).setPosition((int)tmp.x, (int)tmp.y, map.currentLayer);
							map.getEntityLayer().addToMap(e.getId(), (int)tmp.x, (int)tmp.y, map.currentLayer);
							
							hideWindow();
						}
					});
					body.add(b).colspan(2);
					updateSize(270, 32 * (body.getRows() + 3));
				}
			}
		});
		
		super.showWindow(x, y);
	}
	public void hideWindow(){
		super.hideWindow();
		root.clearChildren();
		updateSize(270, 34 * 2);
	}
	protected void updateSize(int width, int height){
		super.updateSize(width, height);
	}
	private void selectedHuman(Table body){
		addInputpair("Head #:", "head", true, body);
		addInputpair("Shirt colour:", "skin", false, body);
		addInputpair("Role:", "role", false, body);
		addInputpair("Health:", "health", true, body);
		addInputpair("Hunger", "hunger", true, body);
		addInputpair("Money:", "money", true, body);
		
		TextButtonStyle bStyle = new TextButtonStyle();
		bStyle.font = font;
		bStyle.up = skin.getDrawable("gui/button");
		TextButton b = new TextButton("Test NPC", bStyle);
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				createCustomEntity(10, 10, 10, "", "Red", 1);
				hideWindow();
			}
		});
		body.add(b).right();
		
		b = new TextButton("Create", bStyle);
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				TextField hp = root.findActor("health");
				TextField hunger = root.findActor("hunger");
				TextField money = root.findActor("money");
				TextField role = root.findActor("role");
				TextField outfit = root.findActor("skin");
				TextField head = root.findActor("head");
				
				createCustomEntity(Integer.valueOf(hp.getText()), Integer.valueOf(hunger.getText()), Integer.valueOf(money.getText()), 
						role.getText(), outfit.getText(), Integer.valueOf(head.getText()));
				hideWindow();
			}
		});
		body.add(b).right();
	}
	private void addInputpair(String key, String value, boolean digitsOnly, Table body){
		LabelStyle ls = new LabelStyle(font, Color.WHITE);
		Label l = new Label(key, ls);
		float w = (body.getWidth() / 2);
		body.add(l).left().width(w).padBottom(2);
		
		TextFieldStyle fieldStyle = new TextFieldStyle();
		fieldStyle.background = skin.getDrawable("gui/bGround");
		fieldStyle.font = font;
		fieldStyle.fontColor = Color.YELLOW;
		
		TextField field = new TextField("", fieldStyle);
		field.setName(value);
		field.setAlignment(Align.right);
		if(digitsOnly){
			field.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
		}
		
		body.add(field).width(w).right().padBottom(2).row();
	}
	public void createCustomEntity(int health, int hunger, int money, String role, String outfit, int head){
		Entity e = EntityFactory.createEntity(type);
		e.getComponent(Animation.class).getSkeleton().setSkin("m"+outfit+"_Front");
		e.getComponent(Animation.class).getSkeleton().setAttachment("head", "human/guyF_head"+head);
		e.getComponent(Health.class).setMax(health);
		e.getComponent(Hunger.class).setMax(hunger);
		e.getComponent(Money.class).value = money;
		
		Vector2 tmp = IsoUtils.getColRow((int)x, (int)y, map.getTileWidth(), map.getTileHeight());
		
		e.getComponent(Position.class).setPosition((int)tmp.x, (int)tmp.y, map.currentLayer);
		map.getEntityLayer().addToMap(e.getId(), (int)tmp.x, (int)tmp.y, map.currentLayer);
	}
}
