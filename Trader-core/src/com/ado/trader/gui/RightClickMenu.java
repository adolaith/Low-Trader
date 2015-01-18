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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ArrayMap;

public class RightClickMenu {
	Table root;
	Circle bounds;
	Button viewEntity, viewZone, viewItem;
	
	ArrayMap<String, Entity> entities;
	Item i;
	
	BitmapFont font;
	Skin skin;
	ItemWindow itemWin;
	NpcInfoWindow npcWin;
	ContainerWindow containerWin;
	
	public RightClickMenu(GameServices guiRes){
		int width = 180;
		font = guiRes.font;
		skin = guiRes.skin;
		itemWin = new ItemWindow(guiRes);
		npcWin = new NpcInfoWindow(guiRes);
		containerWin = new ContainerWindow(guiRes);
		
		entities = new ArrayMap<String, Entity>();
		root = new Table();
		root.setWidth(width);
		root.setVisible(false);
		
		bounds = new Circle();
		
		guiRes.stage.addActor(root);
	}
	
	public void setupMenu(float x, float y, Vector2 mapClicked, Map map){
		//sets up item button
		if(map.getItemLayer().isOccupied((int)mapClicked.x, (int)mapClicked.y, map.currentLayer)){
			
			root.add(createButton(x, y, "item", "item")).width(root.getWidth()).height(26).row();
			i = map.getItemLayer().map[(int)mapClicked.x][(int)mapClicked.y][map.currentLayer];
		}
		
		//click contains entity
		if(map.getEntityLayer().isOccupied((int)mapClicked.x, (int)mapClicked.y, map.currentLayer)){
			
			World world = map.getWorld();
			ComponentMapper<Inventory> inventoryMapper = world.getMapper(Inventory.class);
			ComponentMapper<AiProfile> aiMapper = world.getMapper(AiProfile.class);
			Entity e = world.getEntity(map.getEntityLayer().map[(int)mapClicked.x][(int)mapClicked.y][map.currentLayer]);
			
			//non npc container
			if(inventoryMapper.has(e) && !aiMapper.has(e)){
				root.add(createButton(x, y, "container", "container")).width(root.getWidth()).height(26).row();
				
				entities.put("container", world.getEntity(map.getEntityLayer().map[(int)mapClicked.x][(int)mapClicked.y][map.currentLayer]));
			//npc
			}else if(aiMapper.has(e)){
				root.add(createButton(x, y, "entity", "entity")).width(root.getWidth()).height(26).row();
				
				entities.put("entity", world.getEntity(map.getEntityLayer().map[(int)mapClicked.x][(int)mapClicked.y][map.currentLayer]));
			}
		}
		root.layout();
	}
	
	private Button createButton(final float x, final float y, String text, final String key){
		LabelStyle lStyle = new LabelStyle(font, Color.WHITE);
		Button b = new Button(GameGui.setButtonStyle(skin.getDrawable("gui/button"),null));
		b.add(new Label("View "+text,lStyle));
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				switch(key){
				case "item":
					itemWin.showWindow(x, y, i);
					break;
				case "entity":
					npcWin.showWindow(x, y, entities.get("entity"));
					break;
				case "container":
					containerWin.showWindow(x, y, entities.get("container").getComponent(Inventory.class));
					break;
				}
				hideMenu();
			}
		});
		return b;
	}
	public void update(){
		checkBounds();
	}
	public void checkBounds(){
		if(!root.isVisible())return;
		if(bounds.contains(InputHandler.getMousePos().x, InputHandler.getMousePos().y))return;
		
		hideMenu();
	}
	public void showMenu(float x, float y, Vector2 mapClicked, Map map){
		setupMenu(x, y, mapClicked, map);
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
