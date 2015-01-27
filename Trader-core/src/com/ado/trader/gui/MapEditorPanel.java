package com.ado.trader.gui;

import com.ado.trader.input.InputHandler;
import com.ado.trader.input.MapEditorInput;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.ArrayMap;

public class MapEditorPanel extends Actor{
	Table root, current;
	ArrayMap<String, Table> tableList;

	public MapEditorPanel(GameServices gameRes) {
		tableList = new ArrayMap<String, Table>();
		root = new Table();
		root.setVisible(true);
		root.setWidth(38);
		root.setHeight(5 * 38);
		root.defaults().center().width(30).height(30).pad(2);
		root.setBackground(gameRes.skin.getDrawable("gui/bGround"));
		
		tableList.put("tileMenu", tileMenu(gameRes));
		tableList.put("entityMenu", entityMenu(gameRes));
		tableList.put("wallMenu", wallMenu(gameRes));
		tableList.put("itemsMenu", itemsMenu(gameRes));
		
		createButton("zoneTile", "Tile menu", "tileMenu", gameRes);
		createButton("wallIcon", "Wall menu", "wallMenu", gameRes);
		createButton("entityIcon", "Entity menu", "entityMenu", gameRes);
		createButton("workIcon", "Item menu", "itemsMenu", gameRes);
		
		//exit game button
		final ImageButton exitButton = GuiUtils.createImageButton("gui/exitIcon", null, "gui/button", null, gameRes.skin);
		final ToolTip toolTip = (ToolTip)(gameRes.getStage().getRoot().findActor("tooltip"));
		exitButton.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.show("Exit game");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.hide();
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.exit();
				return true;
			}
		});
		root.add(exitButton);
		
		for(Table t: tableList.values()){
			t.setVisible(false);
			gameRes.getStage().addActor(t);
		}
		
		float x = gameRes.getCam().position.x + (Gdx.graphics.getWidth() / 2) - root.getWidth() - 2;
		float y = gameRes.getCam().position.y - root.getHeight() / 2;
		
		root.setPosition(x, y);
		gameRes.getStage().addActor(root);
	}
	
	public void act(float delta){
		super.act(delta);
		
		//if camera moved, move gui elements
		if(InputHandler.getVelocity().x != 0 || InputHandler.getVelocity().y != 0){
			root.moveBy(InputHandler.getVelocity().x, InputHandler.getVelocity().y);
			current.moveBy(InputHandler.getVelocity().x, InputHandler.getVelocity().y);
		}
	}
	
	private Table itemsMenu(final GameServices gameRes){
		Table itemsMenu = new Table();
		itemsMenu.setFillParent(false);
		Sprite bg = new Sprite(gameRes.skin.getSprite("gui/button"));
		itemsMenu.defaults().size(bg.getWidth()*1.2f);
		
		ArrayMap<Integer, Sprite> spriteList = gameRes.getItems().getItemSprites();
		ArrayMap<String, ArrayMap<String, String>> list = gameRes.getItems().getItemProfiles();
		
		for(final String id: list.keys()){
			ArrayMap<String, String> profile = list.get(id);
			
			Sprite tmp = new Sprite(spriteList.get(Integer.valueOf(profile.get("sprite"))));
			tmp.setScale(1f);

			Button butt = new ImageButton(GuiUtils.setImgButtonStyle(new SpriteDrawable(tmp), null, gameRes.skin.getDrawable("gui/button"), null));
			butt.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					MapEditorInput input = (MapEditorInput) gameRes.getInput();
					input.getPlacementManager().setPlacementSelection("item",id);
				}
			});
			addToTable(butt, itemsMenu);
		}
		
//		ImageButton deleteModeButton = new ImageButton(GameServices.setImgButtonStyle(guiRes.skin.getDrawable("gui/delete"), null, guiRes.skin.getDrawable("gui/button"), null));
//		deleteModeButton.addListener(new InputListener() {
//			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//				root.game.getPlaceManager().setPlacementSelection("item", "");
//				root.game.getPlaceManager().getItemPl().delete = true;
//				return true;
//			}
//		});
//		itemsMenu.add(deleteModeButton).width(56).height(56);
		
		return itemsMenu;
	}
	private Table wallMenu(final GameServices gameRes){
		Table wallMenu = new Table();
		wallMenu.setFillParent(false);
		Sprite bg = new Sprite(gameRes.skin.getSprite("gui/button"));
		wallMenu.defaults().size(bg.getWidth()*1.2f);
		
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

			Button butt = new ImageButton(GuiUtils.setImgButtonStyle(new SpriteDrawable(tmp), null, gameRes.skin.getDrawable("gui/button"), null));
			butt.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					MapEditorInput input = (MapEditorInput) gameRes.getInput();
					input.getPlacementManager().setPlacementSelection("wall", gameRes.getEntities().getEntities().getKeyAt(i));
				}
			});
			addToTable(butt, wallMenu);
		}
		
//		ImageButton deleteModeButton = new ImageButton(GameServices.setImgButtonStyle(guiRes.skin.getDrawable("gui/delete"), null, guiRes.skin.getDrawable("gui/button"), null));
//		deleteModeButton.addListener(new ChangeListener() {
//			public void changed(ChangeEvent event, Actor actor) {
//				root.game.getPlaceManager().setPlacementSelection("wall", 0);
//				root.game.getPlaceManager().getWallPl().delete = true;
//			}
//		});
//		wallMenu.add(deleteModeButton).width(56).height(56);
		
		return wallMenu;
	}
	
	private Table entityMenu(final GameServices gameRes){
		final MapEditorInput input = (MapEditorInput) gameRes.getInput();
		Table entityMenu = new Table();
		entityMenu.setFillParent(false);
		Sprite bg = new Sprite(gameRes.skin.getSprite("gui/button"));
		entityMenu.defaults().size(bg.getWidth()*1.2f);
		
		//static entity buttons (tables, containers, signs etc)
		final ArrayMap<Integer, ArrayMap<String, String>> list = gameRes.getEntities().getEntities();
		for(int x=0;x<list.size; x++){
			final int i = x;
			
			if(list.getValueAt(i).containsKey("animation"))continue;
			if(list.getValueAt(i).get("tags")!=null){
				if(list.getValueAt(i).get("tags").contains("wall")){continue;}
			}
			
			final String[] str = list.getValueAt(i).get("sprite").split(",");
			Sprite tmp = new Sprite(gameRes.getRenderer().getRenderEntitySystem().getStaticSprites().get(Integer.valueOf(str[0])));
			tmp.setScale(1f);

			Button butt = new ImageButton(GuiUtils.setImgButtonStyle(new SpriteDrawable(tmp), null, gameRes.skin.getDrawable("gui/button"), null));
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
			Button butt = new ImageButton(GuiUtils.setImgButtonStyle(new SpriteDrawable(tmp), null, gameRes.skin.getDrawable("gui/button"), null));
			butt.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					MapEditorInput input = (MapEditorInput) gameRes.getInput();
					input.getPlacementManager().setPlacementSelection("feature", f.get("id"));						
				}
			});
			addToTable(butt, entityMenu);
		}
		
		//delete button
//		ImageButton deleteModeButton = new ImageButton(GuiUtils.setImgButtonStyle(guiRes.skin.getDrawable("gui/delete"), null, guiRes.skin.getDrawable("gui/button"), null));
//		deleteModeButton.addListener(new ChangeListener() {
//			public void changed(ChangeEvent event, Actor actor) {
//				root.game.getPlaceManager().setPlacementSelection("entity", 0);
//				root.game.getPlaceManager().getEntityPl().delete = true;
//			}
//		});
//		entityMenu.add(deleteModeButton).width(56).height(56);
		
		return entityMenu;
	}
	//terrain tile menu
	private Table tileMenu(final GameServices gameRes){
		Table terrainMenu = new Table();
		terrainMenu.setFillParent(false);

		terrainMenu.defaults().size(32*1.2f);

		for(int x=0;x<gameRes.getMap().getTilePool().getTileProfiles().size; x++){
			final int i = x;
			ImageButton imgb = new ImageButton(GuiUtils.setImgButtonStyle(new SpriteDrawable(gameRes.getMap().getTileSprites().get(i)), null, gameRes.skin.getDrawable("gui/button"), null));
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
	private void createButton(String icon, final String tooltip, final String menuName, final GameServices gameRes){
		final ImageButton b = GuiUtils.createImageButton("gui/" +icon, null, "gui/button", null, gameRes.skin);
		final ToolTip toolTip = (ToolTip)(gameRes.getStage().getRoot().findActor("tooltip"));
		
		b.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.show(tooltip);
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.hide();
			}
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				Table t = tableList.get(menuName);
				if(t.isVisible()){
					t.setVisible(false);
				}else{
					t.setPosition(root.getX() - t.getWidth(), b.getY());
					t.setVisible(true);
					current = t;
				}
				return true;
			}
		});
		root.add(b).row();
	}
	//lays buttons out in a grid with a width of 5 buttons then a new row
	private void addToTable(Button b, Table t){
		if(t.getCells().size != 0 && (t.getCells().size + 1) % 5 == 0){
			t.add(b).width(56).height(56).row();
		}else{
			t.add(b).width(56).height(56);				
		}
	}
}
