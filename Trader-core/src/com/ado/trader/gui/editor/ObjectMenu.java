package com.ado.trader.gui.editor;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.gui.BasicWindow;
import com.ado.trader.gui.GuiUtils;
import com.ado.trader.gui.ToolTip;
import com.ado.trader.input.MapEditorInput;
import com.ado.trader.placement.PlacementManager;
import com.ado.trader.rendering.SpriteManager;
import com.ado.trader.utils.GameServices;
import com.ado.trader.utils.IdGenerator;
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
	ArrayMap<String, JsonValue> entityProfiles;
	float buttonSize = 32 * 1.2f;
	SpriteManager spriteMan;

	public ObjectMenu(GameServices gameRes) {
		super("Object menu", 310, 200, gameRes.getFont(), gameRes.getSkin(), gameRes.getStage());

		tableList = new ArrayMap<String, Table>();
		entityProfiles = new ArrayMap<String, JsonValue>();
		spriteMan = gameRes.getRenderer().getRenderEntitySystem().getSpriteManager();
		
		tableList.put("tileMenu", tileMenu(gameRes));
		tableList.put("entityMenu", createMenu("entity", gameRes));
		tableList.put("wallMenu", createMenu("wall", gameRes));
		tableList.put("itemsMenu", createMenu("item", gameRes));
		
		for(Table t: tableList.values()){
			t.top();
		}
	}
	
	private Table createMenu(String placeableType, final GameServices gameRes){
		Table t = new Table();
		t.setFillParent(false);
		
		ScrollPane pane = GuiUtils.createScrollTable(gameRes.getSkin());
		
		Table menu = new Table();
		menu.setFillParent(false);
		menu.top().left().defaults().size(buttonSize); 
		
		//loop entity profiles array root
		for(String typeId: EntityFactory.getEntityData().keys()){
			
			//check id types
			if(placeableType.matches("wall") && !typeId.matches(IdGenerator.WALL)){
				continue;
			}else if(placeableType.matches("item") && (!typeId.matches(IdGenerator.BASE_PROFILE) &&
					!typeId.matches(IdGenerator.SPAWNABLE_ITEM))){
				continue;
			}else if(placeableType.matches("entity") &&
					!typeId.matches(IdGenerator.BASE_PROFILE)){
				continue;
			}
			
			//loop sub-array
			ArrayMap<String, JsonValue> list = EntityFactory.getEntityData().get(typeId);
			for(String id: list.keys()){
				//type check
				if(placeableType.matches("item") &&
						typeId.matches(IdGenerator.BASE_PROFILE) &&
						id.charAt(0) != (IdGenerator.ITEM_ID)){
					continue;
				}else if(placeableType.matches("entity") &&
						id.charAt(0) != (IdGenerator.ENTITY_ID)){
					continue;
				}
							
				final JsonValue data = list.get(id);
				
				//get sprite for image button
				String spriteName = data.get("sprite").asStringArray()[0];
				SpriteDrawable tmp = null;
				
				switch(placeableType){
				case "item":
					tmp = new SpriteDrawable(new Sprite(spriteMan.getItemSprite(spriteName)));	
					break;
				case "wall":
					tmp = new SpriteDrawable(new Sprite(spriteMan.getWallSprites(spriteName)[0]));
					break;
				case "entity":
					tmp = new SpriteDrawable(new Sprite(spriteMan.getEntitySprites(spriteName)[0]));
					break;
				}
				
				//create image button
				ImageButton butt = createButton(tmp, data, placeableType, gameRes);
				
				addToTable(butt, menu);
			}
		}
		
		pane.setOverscroll(false, false);
		pane.setWidget(menu);
		t.add(pane).fill().expand().left();
		
		return t;
	}
	
	private ImageButton createButton(SpriteDrawable img, JsonValue data, String placementType, GameServices gameRes){
		ImageButton butt = new ImageButton(GuiUtils.setImgButtonStyle(
				img, null, gameRes.getSkin().getDrawable("gui/button"), null));
		
		butt.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				ToolTip toolTip = (ToolTip)(gameRes.getStage().getRoot().findActor("tooltip"));
				toolTip.show(data.getString("name"));
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
				ToolTip toolTip = (ToolTip)(gameRes.getStage().getRoot().findActor("tooltip"));
				toolTip.hide();
			}
			public void clicked (InputEvent event, float x, float y) {
				MapEditorInput input = (MapEditorInput) gameRes.getInput();
				input.getPlacementManager().setPlacementSelection(
						placementType, data.getString("baseid"));
			}
		});
		
		return butt;
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
			
			ImageButton imgb = new ImageButton(GuiUtils.setImgButtonStyle(
					new SpriteDrawable(gameRes.getMap().getTileSprites().get(i)), null, 
					gameRes.getSkin().getDrawable("gui/button"), null));
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
			ImageButton imgb = new ImageButton(GuiUtils.setImgButtonStyle(
					new SpriteDrawable(s), null, gameRes.getSkin().getDrawable("gui/button"), null));
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
		body.clear();
		super.hideWindow();
	}
	public void setCurrentTable(Table t){
		if(isVisible() && body.getChildren().size > 0){
			if(body.getChildren().first() == t){
				hideWindow();
				return;
			}
		}
		body.clear();
		body.add(t).expand().fill();
	}
}
