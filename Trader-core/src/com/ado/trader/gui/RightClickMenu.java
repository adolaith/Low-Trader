package com.ado.trader.gui;

import com.ado.trader.entities.components.AiProfile;
import com.ado.trader.entities.components.Inventory;
import com.ado.trader.input.InputHandler;
import com.ado.trader.items.Item;
import com.ado.trader.map.Map;
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
	
	public void setupMenu(Vector2 mapClicked, Map map){
		World w = gui.game.getWorld();
		if(map.getItemLayer().isOccupied((int)mapClicked.x, (int)mapClicked.y, map.currentLayer)){
			root.add(createButton("item", "item")).width(root.getWidth()).height(26).row();
			i = map.getItemLayer().map[(int)mapClicked.x][(int)mapClicked.y][map.currentLayer];
		}
		if(map.getEntityLayer().isOccupied((int)mapClicked.x, (int)mapClicked.y, map.currentLayer)){
			ComponentMapper<Inventory> inventoryMapper = w.getMapper(Inventory.class);
			ComponentMapper<AiProfile> aiMapper = w.getMapper(AiProfile.class);
			Entity e = w.getEntity(map.getEntityLayer().map[(int)mapClicked.x][(int)mapClicked.y][map.currentLayer]);
			if(inventoryMapper.has(e) && !aiMapper.has(e)){
				root.add(createButton("container", "container")).width(root.getWidth()).height(26).row();
				entities.put("container", w.getEntity(map.getEntityLayer().map[(int)mapClicked.x][(int)mapClicked.y][map.currentLayer]));
			}else if(aiMapper.has(e)){
				root.add(createButton("entity", "entity")).width(root.getWidth()).height(26).row();
				entities.put("entity", w.getEntity(map.getEntityLayer().map[(int)mapClicked.x][(int)mapClicked.y][map.currentLayer]));
			}
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
	public void showMenu(float x, float y, Vector2 mapClicked, Map map){
		setupMenu(mapClicked, map);
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
