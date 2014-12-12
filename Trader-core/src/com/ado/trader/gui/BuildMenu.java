 package com.ado.trader.gui;

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

	public BuildMenu(Gui root){
		super();
		rightClickMenu(root);
	}

	//Right click menu
	private void rightClickMenu(final Gui root) {
		buildMenu = new ArrayMap<String, Table>();
		
		//Terrain table
		Table terrainMenu = terrainMenu(root);
		terrainMenu.setVisible(false);
		root.stage.addActor(terrainMenu);
		buildMenu.put("terrainmenu", terrainMenu);

		//Wall table
		Table wallMenu = createWallTable(root);
		wallMenu.setVisible(false);
		root.stage.addActor(wallMenu);
		buildMenu.put("wallmenu", wallMenu);
		
		//entity menu table
		Table entityMenu = entityMenu(root);
		entityMenu.setVisible(false);
		root.stage.addActor(entityMenu);
		buildMenu.put("entitymenu", entityMenu);
		
		//zone menu
		Table zoneMenu = zoneMenu(root);
		zoneMenu.setVisible(false);
		root.stage.addActor(zoneMenu);
		buildMenu.put("zonemenu", zoneMenu);
		
		//items menu
		Table itemsMenu = itemsMenu(root);
		itemsMenu.setVisible(false);
		root.stage.addActor(itemsMenu);
		buildMenu.put("itemsmenu", itemsMenu);

		//Main rightclick menu
		Table mainMenu = new Table();
		mainMenu.defaults().height(24);

		mainMenu.setFillParent(false);

		// Make a bunch of filler buttons
		LabelStyle lStyle = new LabelStyle(root.font, Color.BLACK);
		for (int i = 0; i < 2; i++) {
			Button b = new Button(GuiUtils.setButtonStyle(root.skin.getDrawable("gui/button"),null));
			b.add(new Label("[EMPTY]",lStyle));
			b.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					//System.out.println("Wait");
				}
			});
			mainMenu.add(b).row();
		}
		
		mainMenu.add(createNpcButton(root, root.font, root.skin)).width(80).height(44).row();
		mainMenu.add(createWallButton(root.font, root.skin)).row();
		mainMenu.add(createEntityButton(root.font, root.skin)).row();
		mainMenu.add(createItemsButton(root.font, root.skin)).row();
		mainMenu.add(createTerrainButton(root.font, root.skin)).row();
		mainMenu.add(createZoneButton(root.font, root.skin)).row();

		mainMenu.setVisible(false);
		root.stage.addActor(mainMenu);
		buildMenu.put("mainmenu", mainMenu);
	}
	
	private Button createEntityButton(BitmapFont font, Skin skin){
		LabelStyle lStyle = new LabelStyle(font, Color.BLACK);
		
		Button b = new Button(GuiUtils.setButtonStyle(skin.getDrawable("gui/button"),null));
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
	
	private Button createItemsButton(BitmapFont font, Skin skin){
		LabelStyle lStyle = new LabelStyle(font, Color.BLACK);

		Button b = new Button(GuiUtils.setButtonStyle(skin.getDrawable("gui/button"),null));
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
	
	private Button createWallButton(BitmapFont font, Skin skin){
		LabelStyle lStyle = new LabelStyle(font, Color.BLACK);
		
		Button b = new Button(GuiUtils.setButtonStyle(skin.getDrawable("gui/button"),null));
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
	
	private Button createNpcButton(final Gui root, BitmapFont font, Skin skin){
		LabelStyle lStyle = new LabelStyle(font, Color.BLACK);
		
		Button b = new Button(GuiUtils.setButtonStyle(skin.getDrawable("gui/button"),null));
		Label l = new Label("Create\nNpc",lStyle);
		l.setAlignment(Align.center);
		b.add(l);
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				root.game.getPlaceManager().setPlacementSelection("entity", 0);
			}
		});
		return b;
	}
	
	private Button createTerrainButton(BitmapFont font, Skin skin){
		LabelStyle lStyle = new LabelStyle(font, Color.BLACK);
		
		Button b = new Button(GuiUtils.setButtonStyle(skin.getDrawable("gui/button"),null));
		b.add(new Label("Terrain",lStyle));
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				if(buildMenu.get("terrainmenu").isVisible()){
					buildMenu.get("terrainmenu").setVisible(false);
				}else{
					buildMenu.get("terrainmenu").setPosition(buildMenu.get("mainmenu").getX()-160, buildMenu.get("mainmenu").getY());
					buildMenu.get("terrainmenu").layout();
					buildMenu.get("terrainmenu").setVisible(true);
				}
			}
		});
		return b;
	}
	
	private Button createZoneButton(BitmapFont font, Skin skin){
		LabelStyle lStyle = new LabelStyle(font, Color.BLACK);
		
		Button b = new Button(GuiUtils.setButtonStyle(skin.getDrawable("gui/button"),null));
		b.add(new Label("Zones",lStyle));
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				if(buildMenu.get("zonemenu").isVisible()){
					buildMenu.get("zonemenu").setVisible(false);
				}else{
					buildMenu.get("zonemenu").setPosition(buildMenu.get("mainmenu").getX()-175, buildMenu.get("mainmenu").getY());
					buildMenu.get("zonemenu").layout();
					buildMenu.get("zonemenu").setVisible(true);
				}
			}
		});
		return b;
	}
	
	//terrain tile menu
	private Table terrainMenu(final Gui root){
		Table terrainMenu = new Table();
		terrainMenu.setFillParent(false);
		
		terrainMenu.defaults().size(32*1.2f);
		
		for(int x=0;x<root.game.getMap().getTilePool().getTileProfiles().size; x++){
			final int i = x;
			ImageButton imgb = new ImageButton(GuiUtils.setImgButtonStyle(new SpriteDrawable(root.game.getMap().getTileSprites().get(i)), null, root.skin.getDrawable("gui/button"), null));
			imgb.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					root.game.getPlaceManager().setPlacementSelection("tile", i);
				}
			});
			addToTable(imgb, terrainMenu);
		}
		
		return terrainMenu;
	}
	
	private Table zoneMenu(final Gui root){
		Table zoneMenu = new Table();
		zoneMenu.left();
		zoneMenu.setFillParent(false);
		LabelStyle lStyle = new LabelStyle(root.font, Color.BLACK);
		
		Button b = new Button(GuiUtils.setButtonStyle(root.skin.getDrawable("gui/button"),null));
		
		b.add(new Label("Default Zone",lStyle));
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				root.game.getPlaceManager().setPlacementSelection("zone", 0);
			}
		});
		zoneMenu.add(b);
		zoneMenu.row();
		zoneMenu.setSize(b.getWidth(), 110);
		
		b = new Button(GuiUtils.setButtonStyle(root.skin.getDrawable("gui/button"),null));
		b.add(new Label("Work Tile",lStyle));
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				root.game.getPlaceManager().setPlacementSelection("zone", 1);
			}
		});
		zoneMenu.add(b);
		zoneMenu.row();
		
		ImageButton deleteModeButton = new ImageButton(GuiUtils.setImgButtonStyle(root.skin.getDrawable("gui/delete"), null, root.skin.getDrawable("gui/button"), null));
		deleteModeButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				root.game.getPlaceManager().setPlacementSelection("zone", 0);
				root.game.getPlaceManager().getZonePl().delete = true;
			}
		});
		zoneMenu.add(deleteModeButton).width(56).height(56);
		
		return zoneMenu;
	}

	private Table entityMenu(final Gui root){
		Table entityMenu = new Table();
		entityMenu.setFillParent(false);
		Sprite bg = new Sprite(root.skin.getSprite("gui/button"));
		entityMenu.defaults().size(bg.getWidth()*1.2f);
		
		//static entity buttons (tables, containers, signs etc)
		final ArrayMap<Integer, ArrayMap<String, String>> list = root.game.getEntities().getEntities();
		for(int x=0;x<list.size; x++){
			final int i = x;
			
			if(list.getValueAt(i).containsKey("animation"))continue;
			if(list.getValueAt(i).get("tags")!=null){
				if(list.getValueAt(i).get("tags").contains("wall")){continue;}
			}
			
			final String[] str = list.getValueAt(i).get("sprite").split(",");
			Sprite tmp = new Sprite(root.game.getRenderer().getRenderEntitySystem().getStaticSprites().get(Integer.valueOf(str[0])));
			tmp.setScale(1f);

			Button butt = new ImageButton(GuiUtils.setImgButtonStyle(new SpriteDrawable(tmp), null, root.skin.getDrawable("gui/button"), null));
			butt.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					root.game.getPlaceManager().setPlacementSelection("entity",root.game.getEntities().getEntities().getKeyAt(i));	
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

			Button butt = new ImageButton(GuiUtils.setImgButtonStyle(new SpriteDrawable(tmp), null, root.skin.getDrawable("gui/button"), null));
			butt.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					root.game.getPlaceManager().setPlacementSelection("feature", f.get("id"));						
				}
			});
			addToTable(butt, entityMenu);
		}
		
		//delete button
		ImageButton deleteModeButton = new ImageButton(GuiUtils.setImgButtonStyle(root.skin.getDrawable("gui/delete"), null, root.skin.getDrawable("gui/button"), null));
		deleteModeButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				root.game.getPlaceManager().setPlacementSelection("entity", 0);
				root.game.getPlaceManager().getEntityPl().delete = true;
			}
		});
		
		entityMenu.add(deleteModeButton).width(56).height(56);
		return entityMenu;
	}
	private Table createWallTable(final Gui root){
		Table wallMenu = new Table();
		wallMenu.setFillParent(false);
		Sprite bg = new Sprite(root.skin.getSprite("gui/button"));
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

			Button butt = new ImageButton(GuiUtils.setImgButtonStyle(new SpriteDrawable(tmp), null, root.skin.getDrawable("gui/button"), null));
			butt.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					root.game.getPlaceManager().setPlacementSelection("wall",root.game.getEntities().getEntities().getKeyAt(i));
				}
			});
			addToTable(butt, wallMenu);
		}
		
		ImageButton deleteModeButton = new ImageButton(GuiUtils.setImgButtonStyle(root.skin.getDrawable("gui/delete"), null, root.skin.getDrawable("gui/button"), null));
		deleteModeButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				root.game.getPlaceManager().setPlacementSelection("wall", 0);
				root.game.getPlaceManager().getWallPl().delete = true;
			}
		});
		
		wallMenu.add(deleteModeButton).width(56).height(56);
		return wallMenu;
	}
	
	private Table itemsMenu(final Gui root){
		Table itemsMenu = new Table();
		itemsMenu.setFillParent(false);
		Sprite bg = new Sprite(root.skin.getSprite("gui/button"));
		itemsMenu.defaults().size(bg.getWidth()*1.2f);
		
		ArrayMap<Integer, Sprite> spriteList = root.game.getItems().getItemSprites();
		ArrayMap<String, ArrayMap<String, String>> list = root.game.getItems().getItemProfiles();
		
		for(final String id: list.keys()){
			ArrayMap<String, String> profile = list.get(id);
			
			Sprite tmp = new Sprite(spriteList.get(Integer.valueOf(profile.get("sprite"))));
			tmp.setScale(1f);

			Button butt = new ImageButton(GuiUtils.setImgButtonStyle(new SpriteDrawable(tmp), null, root.skin.getDrawable("gui/button"), null));
			butt.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					root.game.getPlaceManager().setPlacementSelection("item",id);
				}
			});
			addToTable(butt, itemsMenu);
		}
		
		ImageButton deleteModeButton = new ImageButton(GuiUtils.setImgButtonStyle(root.skin.getDrawable("gui/delete"), null, root.skin.getDrawable("gui/button"), null));
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
		if(t.getCells().size % 5==0){
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
