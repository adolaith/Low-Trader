package com.ado.trader.gui;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.entities.components.Animation;
import com.ado.trader.entities.components.Health;
import com.ado.trader.entities.components.Hunger;
import com.ado.trader.entities.components.Money;
import com.ado.trader.entities.components.Name;
import com.ado.trader.entities.components.Position;
import com.ado.trader.input.InputHandler;
import com.ado.trader.map.Map;
import com.ado.trader.utils.GameServices;
import com.ado.trader.utils.IsoUtils;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class ConfigurePlayer extends BasicWindow {
	Map map;

	public ConfigurePlayer(GameServices gameRes) {
		super("Configure player", Gdx.graphics.getWidth() * 0.2f, Gdx.graphics.getHeight() * 0.3f, gameRes.getFont(), gameRes.getSkin(), gameRes.getStage());
		this.map = gameRes.getMap();

		addInputpair("Head #:", "head", true);
		addInputpair("Shirt colour:", "skin", false);
		addInputpair("Health:", "health", true);
		addInputpair("Hunger", "hunger", true);
		addInputpair("Money:", "money", true);
		
		TextButtonStyle bStyle = new TextButtonStyle();
		bStyle.font = font;
		bStyle.up = skin.getDrawable("gui/button");
		TextButton b = new TextButton("Test NPC", bStyle);
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				createCustomEntity(10, 10, 10, "Red", 1);
				hideWindow();
			}
		});
		root.add(b).right();
		
		b = new TextButton("Create", bStyle);
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				TextField hp = root.findActor("health");
				TextField hunger = root.findActor("hunger");
				TextField money = root.findActor("money");
				TextField outfit = root.findActor("skin");
				TextField head = root.findActor("head");
				
				createCustomEntity(Integer.valueOf(hp.getText()), Integer.valueOf(hunger.getText()), Integer.valueOf(money.getText()), 
						outfit.getText(), Integer.valueOf(head.getText()));
				hideWindow();
			}
		});
		root.add(b).right();
	}

	private void addInputpair(String key, String value, boolean digitsOnly){
		LabelStyle ls = new LabelStyle(font, Color.WHITE);
		Label l = new Label(key, ls);
		float w = (root.getWidth() / 2);
		root.add(l).left().width(w).padBottom(2);
		
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
		
		root.add(field).width(w).right().padBottom(2).row();
	}
	
	public void createCustomEntity(int health, int hunger, int money, String outfit, int head){
		Entity e = EntityFactory.createEntity(31);
		ComponentMapper<Animation> animMap = map.getWorld().getMapper(Animation.class);
		if(animMap.has(e)){
			if(animMap.get(e).getSkeleton().getData().getName().matches("human")){
				e.edit().add(new Name("player"));
				
				e.getComponent(Animation.class).getSkeleton().setSkin("m"+outfit+"_Front");
				e.getComponent(Animation.class).getSkeleton().setAttachment("head", "human/guyF_head"+head);
				e.getComponent(Health.class).setMax(health);
				e.getComponent(Hunger.class).setMax(hunger);
				e.getComponent(Money.class).value = money;
				
				Vector2 tmp = IsoUtils.getColRow((int)InputHandler.getMapClicked().x, (int)InputHandler.getMapClicked().y, map.getTileWidth(), map.getTileHeight());
				
				e.getComponent(Position.class).setPosition((int)tmp.x, (int)tmp.y, map.currentLayer);
				map.getEntityLayer().addToMap(e.getId(), (int)tmp.x, (int)tmp.y, map.currentLayer);
			}else{
				e.deleteFromWorld();
				return;
			}
		}
		TagManager manager = map.getWorld().getManager(TagManager.class);
		manager.register("player", e);
	}
}
