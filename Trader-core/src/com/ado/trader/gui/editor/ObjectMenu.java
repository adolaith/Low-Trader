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
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.JsonValue;

public class ObjectMenu extends BasicWindow {
	ArrayMap<String, Array<Button>> buttonList;
	float buttonSize = 32 * 1.2f;
	String current;
	
	SpriteManager spriteMan;
	
	Cell<Actor> topBar;
	
	Table scrollTable, tileOptions;
	TextField search;	
	
	@SuppressWarnings("unchecked")
	public ObjectMenu(GameServices gameRes) {
		super("Object menu", 310, 420, gameRes.getFont(), gameRes.getSkin(), gameRes.getStage());
		
//		setDebug(true, true);
		
		current = "";
		buttonList = new ArrayMap<String, Array<Button>>();
		spriteMan = gameRes.getRenderer().getRenderEntitySystem().getSpriteManager();
		
		search = createSearchField(gameRes);
		
		topBar = body.add().expandX().fillX().padLeft(8).padRight(8);
		topBar.setActor(search);
		
		body.row();
		
		createTileOptions(gameRes);
		
		ScrollPane pane = GuiUtils.createScrollTable(gameRes.getSkin());
		
		scrollTable = new Table();
		scrollTable.setFillParent(false);
		scrollTable.top().left();
		
		pane.setWidget(scrollTable);
		
		body.add(pane).expand().fill();
			
		buttonList.put("terrainMenu", tileMenu(gameRes));
		buttonList.put("maskMenu", maskMenu(gameRes));
		buttonList.put("entityMenu", createMenu("entity", gameRes));
		buttonList.put("wallMenu", createMenu("wall", gameRes));
		buttonList.put("npcMenu", createMenu("npc", gameRes));
		buttonList.put("itemsMenu", createMenu("item", gameRes));
		
	}
	
	private TextField createSearchField(GameServices gameRes){
		TextFieldStyle style = new TextFieldStyle();
		style.font = gameRes.getFont();
		style.fontColor = Color.BLACK;
		style.background = gameRes.getSkin().getDrawable("gui/tooltip");
		
		TextField field = new TextField("", style);
		
		field.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				String search = field.getText();
				
				scrollTable.clear();
				
				if(current.matches("npcMenu")){
					for(Button a: buttonList.get(current)){
						ImageTextButton butt = (ImageTextButton) a;
						String buttText = butt.getLabel().getText().toString(); 
						if(!buttText.contains(search)){
							butt.setVisible(false);
						}else{
							butt.setVisible(true);
						}
					}
				}else{
					for(Button a: buttonList.get(current)){
						ImageTextButton butt = (ImageTextButton) a;
						String buttText = butt.getLabel().getText().toString();
						
						if(buttText.contains(search)){
							scrollTable.add(butt).padTop(2).padRight(2).expandX().fillX().height(42).row();
						}
					}
				}
				
				scrollTable.layout();
			}
		});
		
		return field;
	}
	
	private Array<Button> createMenu(String placeableType, final GameServices gameRes){
		Array<Button> array = new Array<Button>();
		
		//loop entity profiles array root
		for(String typeId: EntityFactory.getEntityData().keys()){
			
			//check prefix-id type
			if(placeableType.matches("wall") && !typeId.matches(IdGenerator.WALL)){
				continue;
			}else if(placeableType.matches("item") && (!typeId.matches(IdGenerator.BASE_PROFILE) &&
					!typeId.matches(IdGenerator.SPAWNABLE_ITEM))){
				continue;
			}else if(placeableType.matches("entity") &&
					!typeId.matches(IdGenerator.BASE_PROFILE)){
				continue;
			}else if(placeableType.matches("npc") && (!typeId.matches(IdGenerator.BASE_PROFILE) &&
					!typeId.matches(IdGenerator.SPAWNABLE_NPC))){
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
				}else if(placeableType.matches("npc") &&
						typeId.matches(IdGenerator.BASE_PROFILE) &&
						id.charAt(0) != (IdGenerator.NPC_ID)){
					continue;
				}
							
				final JsonValue data = list.get(id);
				
				SpriteDrawable tmp = null;
				
				//get sprite for image button
				if(data.has("sprite")){
					String spriteName = data.get("sprite").asStringArray()[0];
									
					switch(placeableType){
					case "item":
						tmp = new SpriteDrawable(new Sprite(spriteMan.getItemSprite(spriteName)));	
						break;
					case "wall":
						Sprite s = spriteMan.getWallSprites(spriteName)[0];
						tmp = new SpriteDrawable(new Sprite(s));
						break;
					case "entity":
						tmp = new SpriteDrawable(new Sprite(spriteMan.getEntitySprites(spriteName)[0]));
						break;
					}
				}
				
				//create image button
				ImageTextButton butt = createButton(tmp, data, placeableType, gameRes);
				
				array.add(butt);
			}
		}
				
		return array;
	}
	
	private ImageTextButton createButton(SpriteDrawable img, JsonValue data, String placementType, GameServices gameRes){
		//button styling and creation
		ImageTextButtonStyle style = new ImageTextButtonStyle();
		style.up = gameRes.getSkin().getDrawable("gui/button");
		
		Drawable over = gameRes.getSkin().newDrawable("gui/button", Color.LIGHT_GRAY);
		style.over = over;
		
		style.imageUp = img;
		style.font = gameRes.getFont();
		style.fontColor = Color.WHITE;
		style.overFontColor = Color.BLUE;
		ImageTextButton butt = new ImageTextButton(data.getString("name"), style);
		
		//button config
		butt.getLabelCell().expandX().center();
		butt.getImageCell().left().padLeft(6).padRight(6);
		
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
	
	private void createTileOptions(final GameServices gameRes){
		tileOptions = new Table();
		tileOptions.setFillParent(false);
		
		final ToolTip toolTip = (ToolTip)(gameRes.getStage().getRoot().findActor("tooltip"));
		
		Button b = new Button(GuiUtils.setButtonStyle("gui/button", null, gameRes.getSkin()));
		LabelStyle lStyle = new LabelStyle(gameRes.getFont(), Color.WHITE);
		b.add(new Label("Tiles", lStyle));
		b.addListener(new ClickListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if(!current.matches("terrainMenu")){
					removeTable();
					
					for(Button b: buttonList.get("terrainMenu")){
						if(scrollTable.getCells().size != 0 && (scrollTable.getCells().size + 1) % 5 == 0){
							scrollTable.add(b).padTop(2).padRight(2).width(56).height(56).row();
						}else{
							scrollTable.add(b).padTop(2).padRight(2).width(56).height(56);
						}
					}
					scrollTable.layout();
				}
				return true;
			}
		});
		
		tileOptions.add(b).left().expandX();
		
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
				if(!current.matches("maskMenu")){
					removeTable();
					
					for(Button b: buttonList.get("maskMenu")){
						if(scrollTable.getCells().size != 0 && (scrollTable.getCells().size + 1) % 5 == 0){
							scrollTable.add(b).padTop(2).padRight(2).width(56).height(56).row();
						}else{
							scrollTable.add(b).padTop(2).padRight(2).width(56).height(56);
						}
					}
					scrollTable.layout();
				}
				return true;
			}
		});
		
		tileOptions.add(b).right().expandX();
		
	}
	
	private Array<Button> tileMenu(final GameServices gameRes){
		Array<Button> array = new Array<Button>();
//		menu.defaults().size(buttonSize);
		
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
			
			array.add(imgb);
		}
		return array;
	}
	
	private Array<Button> maskMenu(final GameServices gameRes){
		Array<Button> array = new Array<Button>();
		
		int profileCount = gameRes.getMap().getTileMasks().getMaskSprites().length;

//		for(int x = 0; x < profileCount; x++){
//			final int i = x;
//			
//			Sprite s = gameRes.getMap().getTileMasks().getMaskSprites()[i][0];
//			ImageButton imgb = new ImageButton(GuiUtils.setImgButtonStyle(
//					new SpriteDrawable(s), null, gameRes.getSkin().getDrawable("gui/button"), null));
//			imgb.addListener(new ChangeListener() {
//				public void changed(ChangeEvent event, Actor actor) {
//					PlacementManager placement = ((MapEditorInput) gameRes.getInput()).getPlacementManager();
//					if(placement.getPlacementSelection() == placement.getTilePl()){
//						placement.getTilePl().mask = i;
//						placement.getTilePl().dir = 0;
//					}
//				}
//			});
//			
//			array.add(imgb);
//		}
		
		return array;
	}
	
	public void hideWindow(){
		super.hideWindow();
		
		removeTable();
	}
	
	public void setCurrentTable(String newTable){
		if(current.matches(newTable)){
			hideWindow();
			return;
		}
		
		removeTable();
		
		if(newTable.matches("tileMenu||terrainMenu||maskMenu")){
			Array<Button> list = null;
			if(newTable.matches("tileMenu")){
				topBar.setActor(tileOptions);
				
				list = buttonList.get("terrainMenu");
				
				current = "terrainMenu";
			}else{
				list = buttonList.get(newTable);
				
				current = newTable;
			}
			
			for(Button b: list){
				if(scrollTable.getCells().size != 0 && (scrollTable.getCells().size + 1) % 5 == 0){
					scrollTable.add(b).padTop(2).padRight(2).width(56).height(56).row();
				}else{
					scrollTable.add(b).padTop(2).padRight(2).width(56).height(56);
				}
			}
		}else{
			topBar.setActor(search);
			
			for(Button b: buttonList.get(newTable)){
				scrollTable.add(b).padTop(2).padRight(2).expandX().fillX().height(42).row();
			}
			
			current = newTable;
		}
		
		scrollTable.layout();		
	}
	
	private void removeTable(){
		scrollTable.clear();
		scrollTable.layout();
		
		current = "";
	}
}
