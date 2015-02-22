package com.ado.trader.gui.editor;

import com.ado.trader.gui.BasicWindow;
import com.ado.trader.gui.GuiUtils;
import com.ado.trader.input.MapEditorInput;
import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.ArrayMap;


public class ObjectMenu extends BasicWindow {
	Table current;
	ScrollPane pane;
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
			t.top().left();
			t.setVisible(false);
		}
		
		ScrollPaneStyle spS = new ScrollPaneStyle();
		spS.vScroll = gameRes.getSkin().getDrawable("gui/scrollBar");
		spS.vScrollKnob = gameRes.getSkin().getDrawable("gui/scrollBar");
		
		pane = new ScrollPane(null, spS);
		pane.setScrollingDisabled(true, false);
		pane.setScrollBarPositions(false, true);
		root.add(pane).fill().expand();
	}

	private Table itemsMenu(final GameServices gameRes){
		Table itemsMenu = new Table();
		itemsMenu.setFillParent(false);
		itemsMenu.defaults().size(buttonSize);
		
		ArrayMap<Integer, Sprite> spriteList = gameRes.getItems().getItemSprites();
		ArrayMap<String, ArrayMap<String, String>> list = gameRes.getItems().getItemProfiles();
		
		for(final String id: list.keys()){
			ArrayMap<String, String> profile = list.get(id);
			
			Sprite tmp = new Sprite(spriteList.get(Integer.valueOf(profile.get("sprite"))));
			tmp.setScale(1f);

			Button butt = new ImageButton(GuiUtils.setImgButtonStyle(new SpriteDrawable(tmp), null, gameRes.getSkin().getDrawable("gui/button"), null));
			butt.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					MapEditorInput input = (MapEditorInput) gameRes.getInput();
					input.getPlacementManager().setPlacementSelection("item",id);
				}
			});
			addToTable(butt, itemsMenu);
		}
		
		return itemsMenu;
	}
	private Table wallMenu(final GameServices gameRes){
		Table wallMenu = new Table();
		wallMenu.setFillParent(false);
		wallMenu.defaults().size(buttonSize);
		
		ArrayMap<Integer, Sprite> spriteList = gameRes.getRenderer().getRenderEntitySystem().getStaticSprites();
		ArrayMap<Integer, ArrayMap<String, String>> list = gameRes.getEntities().getEntities();
		
		for(int x=0;x<list.size; x++){
			final int i = x;
			if(list.getValueAt(i).get("tags")==null||!list.getValueAt(i).get("tags").contains("wall")){
				continue;
			}
			
			final String[] str = list.getValueAt(i).get("sprite").split(",");
			Sprite tmp = new Sprite(spriteList.get(Integer.valueOf(str[0])));
			tmp.setScale(1f);

			Button butt = new ImageButton(GuiUtils.setImgButtonStyle(new SpriteDrawable(tmp), null, gameRes.getSkin().getDrawable("gui/button"), null));
			butt.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					MapEditorInput input = (MapEditorInput) gameRes.getInput();
					input.getPlacementManager().setPlacementSelection("wall", gameRes.getEntities().getEntities().getKeyAt(i));
				}
			});
			addToTable(butt, wallMenu);
		}
		
		return wallMenu;
	}
	
	private Table entityMenu(final GameServices gameRes){
		final MapEditorInput input = (MapEditorInput) gameRes.getInput();
		Table entityMenu = new Table();
		entityMenu.setFillParent(false);
		entityMenu.defaults().size(buttonSize);
		
		final ArrayMap<Integer, ArrayMap<String, String>> list = gameRes.getEntities().getEntities();
		
		
		//static entity buttons (tables, containers, signs etc)
		for(int x = 0; x < list.size; x++){
			final int i = x;
			
			if(list.getValueAt(i).containsKey("animation"))continue;
			if(list.getValueAt(i).get("tags")!=null){
				if(list.getValueAt(i).get("tags").contains("wall")){continue;}
			}
			
			final String[] str = list.getValueAt(i).get("sprite").split(",");
			Sprite tmp = new Sprite(gameRes.getRenderer().getRenderEntitySystem().getStaticSprites().get(Integer.valueOf(str[0])));
			tmp.setScale(1f);

			Button butt = new ImageButton(GuiUtils.setImgButtonStyle(new SpriteDrawable(tmp), null, gameRes.getSkin().getDrawable("gui/button"), null));
			butt.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					input.getPlacementManager().setPlacementSelection("entity", gameRes.getEntities().getEntities().getKeyAt(i));	
				}
			});
			addToTable(butt, entityMenu);
		}
		
		//feature(decoration) buttons(lamps, windows)
		final ArrayMap<String, ArrayMap<String, String>> features = input.getPlacementManager().getFeaturePl().getFeatures().getFeaturesList();
		//loop profiles
		for(final ArrayMap<String, String> f: features.values()){
			final String[] str = f.get("sprite").split(",");
			Sprite tmp = new Sprite(gameRes.getRenderer().getRenderEntitySystem().getStaticSprites().get(Integer.valueOf(str[0])));
			tmp.setScale(1f);
			
			//make button with icon
			Button butt = new ImageButton(GuiUtils.setImgButtonStyle(new SpriteDrawable(tmp), null, gameRes.getSkin().getDrawable("gui/button"), null));
			butt.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					MapEditorInput input = (MapEditorInput) gameRes.getInput();
					input.getPlacementManager().setPlacementSelection("feature", f.get("id"));						
				}
			});
			addToTable(butt, entityMenu);
		}
		
		return entityMenu;
	}
	//terrain tile menu
	private Table tileMenu(final GameServices gameRes){
		Table terrainMenu = new Table();
		terrainMenu.setFillParent(false);
		terrainMenu.defaults().size(buttonSize);

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
			addToTable(imgb, terrainMenu);
		}
		
		return terrainMenu;
	}

	//lays buttons out in a grid with a width of 5 buttons then a new row
	private void addToTable(Button b, Table t){
		if(t.getCells().size != 0 && (t.getCells().size + 1) % 5 == 0){
			t.add(b).padTop(2).padLeft(2).width(56).height(56).row();
		}else{
			t.add(b).padTop(2).padLeft(2).width(56).height(56);				
		}
	}
	public Table getTable(String name){
		return tableList.get(name);
	}
	@Override
	public void hideWindow(){
		current.setVisible(false);
		super.hideWindow();
	}
	public void setCurrentTable(Table t){
		if(current != null){
			current.setVisible(false);
		}
		current = t;
		current.setVisible(true);
		pane.setWidget(current);
	}
	public Table getCurrentTable(){
		return current;
	}
}
