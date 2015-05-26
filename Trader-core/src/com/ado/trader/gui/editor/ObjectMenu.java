package com.ado.trader.gui.editor;

import com.ado.trader.gui.BasicWindow;
import com.ado.trader.gui.GuiUtils;
import com.ado.trader.gui.ToolTip;
import com.ado.trader.input.MapEditorInput;
import com.ado.trader.items.ItemFactory;
import com.ado.trader.placement.PlacementManager;
import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.JsonValue;


public class ObjectMenu extends BasicWindow {
	
	ArrayMap<String, Table> tableList;
	float buttonSize = 32 * 1.2f;

	public ObjectMenu(GameServices gameRes) {
		super("Object menu", 310, 200, gameRes.getFont(), gameRes.getSkin(), gameRes.getStage());

		tableList = new ArrayMap<String, Table>();
		
		tableList.put("tileMenu", tileMenu(gameRes));
		tableList.put("entityMenu", entityMenu(gameRes));
		tableList.put("wallMenu", wallMenu(gameRes));
		tableList.put("itemsMenu", itemsMenu(gameRes));
		
		for(Table t: tableList.values()){
			t.top();
		}
	}
	
	private Table itemsMenu(final GameServices gameRes){
		Table t = new Table();
		t.setFillParent(false);
		
		ScrollPane pane = GuiUtils.createScrollTable(gameRes.getSkin());
		
		Table itemsMenu = new Table();
		itemsMenu.setFillParent(false);
		itemsMenu.defaults().size(buttonSize);
		
		gameRes.getItems();
		ArrayMap<String, Sprite> spriteList = ItemFactory.getItemSprites();
		gameRes.getItems();
		ArrayMap<String, JsonValue> data = ItemFactory.getItemData();
		
		for(final String name: data.keys()){
			
			SpriteDrawable tmp = new SpriteDrawable(new Sprite(spriteList.get(name)));

			ImageButton butt = new ImageButton(GuiUtils.setImgButtonStyle(tmp, null, gameRes.getSkin().getDrawable("gui/button"), null));
			butt.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					MapEditorInput input = (MapEditorInput) gameRes.getInput();
					input.getPlacementManager().setPlacementSelection("item",name);
				}
			});
			addToTable(butt, itemsMenu);
		}
		
		pane.setWidget(itemsMenu);
		t.add(pane).fill().expand().left();
		
		return t;
	}
	private Table wallMenu(final GameServices gameRes){
		Table t = new Table();
		t.setFillParent(false);
		
		ScrollPane pane = GuiUtils.createScrollTable(gameRes.getSkin());
		
		Table wallMenu = new Table();
		wallMenu.setFillParent(false);
		wallMenu.defaults().size(buttonSize);
		
		ArrayMap<String, Sprite[]> spriteList = gameRes.getRenderer().getRenderEntitySystem().getSprites();
		ArrayMap<String, JsonValue> list = gameRes.getEntities().getEntityData();
		
		for(JsonValue e: list.values()){
			final JsonValue d = e;
			if(d.has("group")){
				if(d.get("group").asString().matches("wall")){
					Sprite tmp = new Sprite(spriteList.get(d.getString("name"))[0]);
					
					ImageButton butt = new ImageButton(GuiUtils.setImgButtonStyle(new SpriteDrawable(tmp), null, gameRes.getSkin().getDrawable("gui/button"), null));
					butt.addListener(new ChangeListener() {
						public void changed(ChangeEvent event, Actor actor) {
							MapEditorInput input = (MapEditorInput) gameRes.getInput();
							input.getPlacementManager().setPlacementSelection("wall", d.getString("name"));
						}
					});
					addToTable(butt, wallMenu);
				}
			}
		}
		pane.setWidget(wallMenu);
		t.add(wallMenu).top().left().fill().expand();
		
		return t;
	}
	
	private Table entityMenu(final GameServices gameRes){
		final MapEditorInput input = (MapEditorInput) gameRes.getInput();
		
		Table t = new Table();
		t.setFillParent(false);
//		t.setDebug(true);
		
		ScrollPane pane = GuiUtils.createScrollTable(gameRes.getSkin());
		
		Table entityMenu = new Table();
		entityMenu.setFillParent(false);
		entityMenu.top().left().defaults().size(buttonSize);
		
		ArrayMap<String, Sprite[]> spriteList = gameRes.getRenderer().getRenderEntitySystem().getSprites();
		ArrayMap<String, JsonValue> list = gameRes.getEntities().getEntityData();
		
		//static entity buttons (tables, containers, signs etc)
		for(JsonValue e: list.values()){
			final JsonValue d = e;
			
			if(d.has("animation"))continue;
			
			if(d.has("group")){
				if(d.get("group").asString().matches("wall")){
					continue;
				}
			}
			
			final Sprite tmp = new Sprite(spriteList.get(d.getString("name"))[0]);
			
			ImageButton butt = new ImageButton(GuiUtils.setImgButtonStyle(new SpriteDrawable(tmp), null, gameRes.getSkin().getDrawable("gui/button"), null));
			butt.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					
					input.getPlacementManager().setPlacementSelection("entity", d.getString("name"));	
				}
			});
			addToTable(butt, entityMenu);
		}
		
		//feature(decoration) buttons(lamps, windows)
		ArrayMap<String, JsonValue> features = input.getPlacementManager().getFeaturePl().getFeatures().getFeaturesList();
		//loop profiles
		for(JsonValue e: features.values()){
			final JsonValue d = e;
			
			Sprite tmp = new Sprite(spriteList.get(d.getString("name"))[0]);
			
			//make button with icon
			ImageButton butt = new ImageButton(GuiUtils.setImgButtonStyle(new SpriteDrawable(tmp), null, gameRes.getSkin().getDrawable("gui/button"), null));
			butt.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					MapEditorInput input = (MapEditorInput) gameRes.getInput();
					input.getPlacementManager().setPlacementSelection("feature", d.getString("name"));						
				}
			});
			addToTable(butt, entityMenu);
		}
		
		pane.setWidget(entityMenu);
		t.add(pane).fill().expand().left();
		
		return t;
	}
	//terrain tile menu
	private Table tileMenu(final GameServices gameRes){
		Table t = new Table();
		t.setFillParent(false);
		
		final ToolTip toolTip = (ToolTip)(gameRes.getStage().getRoot().findActor("tooltip"));
		
		final Table terrainMenu = tileTable(gameRes);
		
		final Table maskMenu = maskTable(gameRes);
		
		final ScrollPane pane = GuiUtils.createScrollTable(gameRes.getSkin());
		
		Button b = new Button(GuiUtils.setButtonStyle("gui/button", null, gameRes.getSkin()));
		LabelStyle lStyle = new LabelStyle(gameRes.getFont(), Color.WHITE);
		b.add(new Label("Tiles", lStyle));
		b.addListener(new ClickListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if(pane.getWidget() != terrainMenu){
					pane.setWidget(terrainMenu);
				}
				return true;
			}
		});
		
		t.add(b).left();
		
		b = new Button(GuiUtils.setButtonStyle("gui/button", null, gameRes.getSkin()));
		b.add(new Label("Masks", lStyle));
		b.addListener(new ClickListener(){
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.show("Select tile first.\n'r' to rotate mask");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
				toolTip.hide();
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if(pane.getWidget() != maskMenu){
					pane.setWidget(maskMenu);
				}
				return true;
			}
		});
		
		t.add(b).left().row();
		t.add(pane).colspan(2).fill().expand().left();
		pane.setWidget(terrainMenu);

		return t;
	}
	
	private Table tileTable(final GameServices gameRes){
		Table menu = new Table();
		menu.top().left();
		menu.setFillParent(false);
		
		menu.defaults().size(buttonSize);
		
		int profileCount = gameRes.getMap().getTilePool().getTileProfiles().size;
		
		for(int x = 0; x < profileCount; x++){
			final int i = x;
			ImageButton imgb = new ImageButton(GuiUtils.setImgButtonStyle(new SpriteDrawable(gameRes.getMap().getTileSprites().get(i)), null, gameRes.getSkin().getDrawable("gui/button"), null));
			imgb.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					MapEditorInput input = (MapEditorInput) gameRes.getInput();
					input.getPlacementManager().setPlacementSelection("tile", i);
				}
			});
			addToTable(imgb, menu);
		}
		
		return menu;
	}
	
	private Table maskTable(final GameServices gameRes){
		Table menu = new Table();
		menu.top().left();
		menu.setFillParent(false);
		menu.defaults().size(buttonSize);
		
		int profileCount = gameRes.getMap().getTileMasks().getMaskSprites().length;

		for(int x = 0; x < profileCount; x++){
			final int i = x;
			Sprite s = gameRes.getMap().getTileMasks().getMaskSprites()[i][0];
			ImageButton imgb = new ImageButton(GuiUtils.setImgButtonStyle(new SpriteDrawable(s), null, gameRes.getSkin().getDrawable("gui/button"), null));
			imgb.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					PlacementManager placement = ((MapEditorInput) gameRes.getInput()).getPlacementManager();
					if(placement.getPlacementSelection() == placement.getTilePl()){
						placement.getTilePl().mask = i;
						placement.getTilePl().dir = 0;
					}
				}
			});
			addToTable(imgb, menu);
		}
		
		return menu;
	}
	
	//lays buttons out in a grid with a width of 5 buttons then a new row
	private void addToTable(ImageButton b, Table t){
		if(t.getCells().size != 0 && (t.getCells().size + 1) % 5 == 0){
			t.add(b).padTop(2).padRight(2).width(56).height(56).row();
		}else{
			t.add(b).padTop(2).padRight(2).width(56).height(56);
		}
	}
	public Table getTable(String name){
		return tableList.get(name);
	}
	@Override
	public void hideWindow(){
		root.clear();
		super.hideWindow();
	}
	public void setCurrentTable(Table t){
		if(isVisible() && root.getChildren().first() == t){
			hideWindow();
		}
		root.clear();
		root.add(t).expand().fill();
	}
}
