 package com.ado.trader.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.ArrayMap;

public class BuildMenu{
	ArrayMap<String, Table> buildMenu;

	public BuildMenu(GameServices guiRes){
		super();
		rightClickMenu(guiRes);
	}

	//Right click menu
	private void rightClickMenu(GameServices gameRes) {
		buildMenu = new ArrayMap<String, Table>();
		
		//Terrain table
		Table terrainMenu = terrainMenu(gameRes);
		terrainMenu.setVisible(false);
		gameRes.stage.addActor(terrainMenu);
		buildMenu.put("terrainmenu", terrainMenu);

		//Wall table
		Table wallMenu = createWallTable(gameRes);
		wallMenu.setVisible(false);
		gameRes.stage.addActor(wallMenu);
		buildMenu.put("wallmenu", wallMenu);
		
		//entity menu table
		Table entityMenu = entityMenu(gameRes);
		entityMenu.setVisible(false);
		gameRes.stage.addActor(entityMenu);
		buildMenu.put("entitymenu", entityMenu);
		
		//items menu
		Table itemsMenu = itemsMenu(gameRes);
		itemsMenu.setVisible(false);
		gameRes.stage.addActor(itemsMenu);
		buildMenu.put("itemsmenu", itemsMenu);

		//Main rightclick menu
		Table mainMenu = new Table();
		mainMenu.defaults().height(24);

		mainMenu.setFillParent(false);

		// Make a bunch of filler buttons
		LabelStyle lStyle = new LabelStyle(gameRes.font, Color.BLACK);
		for (int i = 0; i < 2; i++) {
			Button b = GuiUtils.createButton("gui/button", null, gameRes.skin);
			b.add(new Label("[EMPTY]",lStyle));
			b.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					//System.out.println("Wait");
				}
			});
			mainMenu.add(b).row();
		}
		
		mainMenu.add(createNpcButton(gameRes)).width(80).height(44).row();
		mainMenu.add(createWallButton(gameRes)).row();
		mainMenu.add(createEntityButton(gameRes)).row();
		mainMenu.add(createItemsButton(gameRes)).row();
		mainMenu.add(createTerrainButton(gameRes)).row();

		mainMenu.setVisible(false);
		gameRes.stage.addActor(mainMenu);
		buildMenu.put("mainmenu", mainMenu);
	}
	
	private Button createEntityButton(GameServices gameRes){
		LabelStyle lStyle = new LabelStyle(gameRes.font, Color.BLACK);
		
		Button b = GuiUtils.createButton("gui/button", null, gameRes.skin);
		b.add(new Label("Entities",lStyle));
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				if(buildMenu.get("entitymenu").isVisible()){
					buildMenu.get("entitymenu").setVisible(false);
				}else{
					buildMenu.get("entitymenu").setPosition(buildMenu.get("mainmenu").getX()-190, buildMenu.get("mainmenu").getY());
					buildMenu.get("entitymenu").layout();
					buildMenu.get("entitymenu").setVisible(true);
				}
			}
		});
		return b;
	}
	
	private Button createItemsButton(GameServices gameRes){
		LabelStyle lStyle = new LabelStyle(gameRes.font, Color.BLACK);

		Button b = GuiUtils.createButton("gui/button", null, gameRes.skin);
		b.add(new Label("Items",lStyle));
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				if(buildMenu.get("itemsmenu").isVisible()){
					buildMenu.get("itemsmenu").setVisible(false);
				}else{
					buildMenu.get("itemsmenu").setPosition(buildMenu.get("mainmenu").getX()-190, buildMenu.get("mainmenu").getY());
					buildMenu.get("itemsmenu").layout();
					buildMenu.get("itemsmenu").setVisible(true);
				}
			}
		});
		return b;
	}
	
	private Button createWallButton(GameServices gameRes){
		LabelStyle lStyle = new LabelStyle(gameRes.font, Color.BLACK);
		
		Button b = GuiUtils.createButton("gui/button", null, gameRes.skin);
		b.add(new Label("Walls",lStyle));
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				if(buildMenu.get("wallmenu").isVisible()){
					buildMenu.get("wallmenu").setVisible(false);
				}else{
					buildMenu.get("wallmenu").setPosition(buildMenu.get("mainmenu").getX()-160, buildMenu.get("mainmenu").getY());
					buildMenu.get("wallmenu").layout();
					buildMenu.get("wallmenu").setVisible(true);
				}
			}
		});
		return b;
	}
	
	private Button createNpcButton(GameServices gameRes){
		LabelStyle lStyle = new LabelStyle(gameRes.font, Color.BLACK);
		
		Button b = GuiUtils.createButton("gui/button", null, gameRes.skin);
		Label l = new Label("Create\nNpc",lStyle);
		l.setAlignment(Align.center);
		b.add(l);
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.log("BuildMenu: ", "This button doesnt work");
//				root.game.getPlaceManager().setPlacementSelection("entity", 0);
			}
		});
		return b;
	}
	
	private Button createTerrainButton(GameServices gameRes){
		LabelStyle lStyle = new LabelStyle(gameRes.font, Color.BLACK);
		
		Button b = GuiUtils.createButton("gui/button", null, gameRes.skin);
		b.add(new Label("Terrain",lStyle));
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				if(buildMenu.get("terrainmenu").isVisible()){
					buildMenu.get("terrainmenu").setVisible(false);
				}else{
					buildMenu.get("terrainmenu").setPosition(buildMenu.get("mainmenu").getX()-184, buildMenu.get("mainmenu").getY());
					buildMenu.get("terrainmenu").layout();
					buildMenu.get("terrainmenu").setVisible(true);
				}
			}
		});
		return b;
	}
	
	//terrain tile menu
	private Table terrainMenu(GameServices gameRes){
		Table terrainMenu = new Table();
		terrainMenu.setFillParent(false);
		
		terrainMenu.defaults().size(32*1.2f);
		
		for(int x=0;x<gameRes.getMap().getTilePool().getTileProfiles().size; x++){
			final int i = x;
			ImageButton imgb = new ImageButton(GameServices.setImgButtonStyle(new SpriteDrawable(gameRes.getMap().getTileSprites().get(i)), null, gameRes.skin.getDrawable("gui/button"), null));
			imgb.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					root.game.getPlaceManager().setPlacementSelection("tile", i);
				}
			});
			addToTable(imgb, terrainMenu);
		}
		
		return terrainMenu;
	}

	private Table entityMenu(final GameServices gameRes){
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

			Button butt = new ImageButton(GameServices.setImgButtonStyle(new SpriteDrawable(tmp), null, gameRes.skin.getDrawable("gui/button"), null));
			butt.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					root.game.getPlaceManager().setPlacementSelection("entity", gameRes.getEntities().getEntities().getKeyAt(i));	
				}
			});
			addToTable(butt, entityMenu);
		}
		
		//feature(decoration) buttons(lamps, windows)
		final ArrayMap<String, ArrayMap<String, String>> features = root.game.getEntities().getFeatures().getFeaturesList();
		for(final ArrayMap<String, String> f: features.values()){
			final String[] str = f.get("sprite").split(",");
			Sprite tmp = new Sprite(root.game.getRenderer().getRenderEntitySystem().getStaticSprites().get(Integer.valueOf(str[0])));
			tmp.setScale(1f);

			Button butt = new ImageButton(GameServices.setImgButtonStyle(new SpriteDrawable(tmp), null, gameRes.skin.getDrawable("gui/button"), null));
			butt.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					root.game.getPlaceManager().setPlacementSelection("feature", f.get("id"));						
				}
			});
			addToTable(butt, entityMenu);
		}
		
		//delete button
		ImageButton deleteModeButton = new ImageButton(GameServices.setImgButtonStyle(guiRes.skin.getDrawable("gui/delete"), null, guiRes.skin.getDrawable("gui/button"), null));
		deleteModeButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				root.game.getPlaceManager().setPlacementSelection("entity", 0);
				root.game.getPlaceManager().getEntityPl().delete = true;
			}
		});
		
		entityMenu.add(deleteModeButton).width(56).height(56);
		return entityMenu;
	}
	private Table createWallTable(GameServices guiRes){
		Table wallMenu = new Table();
		wallMenu.setFillParent(false);
		Sprite bg = new Sprite(guiRes.skin.getSprite("gui/button"));
		wallMenu.defaults().size(bg.getWidth()*1.2f);
		
		ArrayMap<Integer, Sprite> spriteList = root.game.getRenderer().getRenderEntitySystem().getStaticSprites();
		ArrayMap<Integer, ArrayMap<String, String>> list = root.game.getEntities().getEntities();
		
		for(int x=0;x<list.size; x++){
			final int i = x;
			if(list.getValueAt(i).get("tags")==null||!list.getValueAt(i).get("tags").contains("wall")){
				continue;
			}
			
			final String[] str = list.getValueAt(i).get("sprite").split(",");
			Sprite tmp = new Sprite(spriteList.get(Integer.valueOf(str[0])));
			tmp.setScale(1f);

			Button butt = new ImageButton(GameServices.setImgButtonStyle(new SpriteDrawable(tmp), null, guiRes.skin.getDrawable("gui/button"), null));
			butt.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					root.game.getPlaceManager().setPlacementSelection("wall",root.game.getEntities().getEntities().getKeyAt(i));
				}
			});
			addToTable(butt, wallMenu);
		}
		
		ImageButton deleteModeButton = new ImageButton(GameServices.setImgButtonStyle(guiRes.skin.getDrawable("gui/delete"), null, guiRes.skin.getDrawable("gui/button"), null));
		deleteModeButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				root.game.getPlaceManager().setPlacementSelection("wall", 0);
				root.game.getPlaceManager().getWallPl().delete = true;
			}
		});
		
		wallMenu.add(deleteModeButton).width(56).height(56);
		return wallMenu;
	}
	
	private Table itemsMenu(GameServices guiRes){
		Table itemsMenu = new Table();
		itemsMenu.setFillParent(false);
		Sprite bg = new Sprite(guiRes.skin.getSprite("gui/button"));
		itemsMenu.defaults().size(bg.getWidth()*1.2f);
		
		ArrayMap<Integer, Sprite> spriteList = root.game.getItems().getItemSprites();
		ArrayMap<String, ArrayMap<String, String>> list = root.game.getItems().getItemProfiles();
		
		for(final String id: list.keys()){
			ArrayMap<String, String> profile = list.get(id);
			
			Sprite tmp = new Sprite(spriteList.get(Integer.valueOf(profile.get("sprite"))));
			tmp.setScale(1f);

			Button butt = new ImageButton(GameServices.setImgButtonStyle(new SpriteDrawable(tmp), null, guiRes.skin.getDrawable("gui/button"), null));
			butt.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					root.game.getPlaceManager().setPlacementSelection("item",id);
				}
			});
			addToTable(butt, itemsMenu);
		}
		
		ImageButton deleteModeButton = new ImageButton(GameServices.setImgButtonStyle(guiRes.skin.getDrawable("gui/delete"), null, guiRes.skin.getDrawable("gui/button"), null));
		deleteModeButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				root.game.getPlaceManager().setPlacementSelection("item", "");
				root.game.getPlaceManager().getItemPl().delete = true;
				return true;
			}
		});
		
		itemsMenu.add(deleteModeButton).width(56).height(56);
		
		return itemsMenu;
	}
	private void addToTable(Button b, Table t){
		if(t.getCells().size != 0 && (t.getCells().size + 1) % 5 == 0){
			t.add(b).width(56).height(56).row();
		}else{
			t.add(b).width(56).height(56);				
		}
	}

	public void rightClickVis(Boolean flag){
		for(int x=0; x<buildMenu.size; x++){
			buildMenu.getValueAt(x).setVisible(flag);
		}
	}
	public ArrayMap<String, Table> getBuildMenu() {
		return buildMenu;
	}
}
