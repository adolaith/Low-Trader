package com.ado.trader.gui;

import com.ado.trader.entities.components.AiProfile;
import com.ado.trader.entities.components.Inventory;
import com.ado.trader.items.Item;
import com.ado.trader.map.FarmZone;
import com.ado.trader.map.HomeZone;
import com.ado.trader.map.LayerGroup;
import com.ado.trader.map.Zone;
import com.ado.trader.utils.InputHandler;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ArrayMap;

public class RightClickMenu {
	Table root, currentTable;
	Circle bounds;
	Button viewEntity, viewZone, viewItem;
	ArrayMap<String, Entity> entities;
	Item i;
	Zone z;
	Gui gui;
	
	public RightClickMenu(Gui gui){
		this.gui = gui;
		
		int width = 180;
		
		entities = new ArrayMap<String, Entity>();
		root = new Table();
		root.setWidth(width);
		root.setVisible(false);
		
		bounds = new Circle();
		
		gui.stage.addActor(root);
	}
	
	public void setupMenu(Vector2 mapClicked, LayerGroup currentGroup){
		World w = gui.game.getWorld();
		if(currentGroup.itemLayer.isOccupied((int)mapClicked.x, (int)mapClicked.y)){
			root.add(createButton("item", "item")).width(root.getWidth()).height(26).row();
			i = currentGroup.itemLayer.map[(int)mapClicked.x][(int)mapClicked.y];
		}
		if(currentGroup.entityLayer.isOccupied((int)mapClicked.x, (int)mapClicked.y)){
			ComponentMapper<Inventory> inventoryMapper = w.getMapper(Inventory.class);
			ComponentMapper<AiProfile> aiMapper = w.getMapper(AiProfile.class);
			Entity e = w.getEntity(currentGroup.entityLayer.map[(int)mapClicked.x][(int)mapClicked.y]);
			if(inventoryMapper.has(e) && !aiMapper.has(e)){
				root.add(createButton("container", "container")).width(root.getWidth()).height(26).row();
				entities.put("container", w.getEntity(currentGroup.entityLayer.map[(int)mapClicked.x][(int)mapClicked.y]));
			}else if(aiMapper.has(e)){
				root.add(createButton("entity", "entity")).width(root.getWidth()).height(26).row();
				entities.put("entity", w.getEntity(currentGroup.entityLayer.map[(int)mapClicked.x][(int)mapClicked.y]));
			}
		}
		if(currentGroup.wallLayer.isOccupied((int)mapClicked.x, (int)mapClicked.y)){
			root.add(createButton("wall", "wall")).width(root.getWidth()).height(26).row();
			entities.put("wall", w.getEntity(currentGroup.wallLayer.map[(int)mapClicked.x][(int)mapClicked.y]));
		}
		if(currentGroup.zoneLayer.isOccupied((int)mapClicked.x, (int)mapClicked.y)){
			root.add(createButton("zone", "zone")).width(root.getWidth()).height(26);
			z = currentGroup.zoneLayer.zoneMap[(int)mapClicked.x][(int)mapClicked.y];
		}
		root.layout();
	}
	private Button createButton(String text, final String key){
		LabelStyle lStyle = new LabelStyle(gui.font, Color.WHITE);
		Button b = new Button(GuiUtils.setButtonStyle(gui.skin.getDrawable("gui/button"),null));
		final float x = gui.game.getInput().getMousePos().x;
		final float y =  gui.game.getInput().getMousePos().y;
		b.add(new Label("View "+text,lStyle));
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				switch(key){
				case "zone":
					switch(z.type){
					case FARM:
						gui.farmWindow.showWindow(x, y, (FarmZone)z, gui.game);
						break;
					case HOME:
						gui.homeWindow.showWindow(x, y, (HomeZone)z, gui.game);
						break;
					default:
						break;
					}
					break;
				case "item":
					gui.itemWindow.showWindow(x, y, i);
					break;
				case "entity":
					gui.npcWindow.showWindow(x, y, entities.get("entity"));
					break;
				case "container":
					gui.containerWindow.showWindow(x, y, entities.get("container").getComponent(Inventory.class));
					break;
				}
				hideMenu();
			}
		});
		return b;
	}
	public void checkBounds(InputHandler input){
		if(!root.isVisible())return;
		if(bounds.contains(input.getMousePos().x, input.getMousePos().y))return;
		
		hideMenu();
	}
	public void showMenu(float x, float y, Vector2 mapClicked, LayerGroup currentGroup){
		setupMenu(mapClicked, currentGroup);
		if(root.getChildren().size==0)return;
		
		root.setPosition(x, y);
		bounds.set(x+root.getWidth()/2, y+root.getHeight()/2, (float) (Math.sqrt(root.getWidth()*root.getWidth()+root.getHeight()*root.getHeight())/2+8));
		
		root.setVisible(true);
	}
	public void hideMenu(){
		root.setVisible(false);
		root.clearChildren();
		entities.clear();
	}
}
