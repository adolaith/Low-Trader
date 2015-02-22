package com.ado.trader.gui;

import com.ado.trader.entities.components.AiProfile;
import com.ado.trader.entities.components.Inventory;
import com.ado.trader.entities.components.Lockable;
import com.ado.trader.entities.components.Portal;
import com.ado.trader.input.InputHandler;
import com.ado.trader.items.Item;
import com.ado.trader.map.Map;
import com.ado.trader.utils.GameServices;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ArrayMap;

public class RightClickMenu extends ContextMenu{
	Button viewEntity, viewZone, viewItem;
	
	ArrayMap<String, Entity> entities;
	ArrayMap<String, BasicWindow> windows;
	Item i;
	
	BitmapFont font;
	Skin skin;
	Map map;
	
	ComponentMapper<Portal> portalMap;
	ComponentMapper<Inventory> inventoryMapper;
	ComponentMapper<AiProfile> aiMapper;
	ComponentMapper<Lockable> lockMap;
	TagManager tags;
	
	public RightClickMenu(GameServices gameRes){
		super(gameRes);
		
		setName("rightClickMenu");
		font = gameRes.getFont();
		skin = gameRes.getSkin();
		map = gameRes.getMap();
		
		windows = new ArrayMap<String, BasicWindow>();
		windows.put("item", new ItemWindow(gameRes));
		windows.put("npcInfo", new NpcInfoWindow(gameRes));
		windows.put("containerContents", new ContainerWindow(gameRes));
		
		World world = map.getWorld();
		inventoryMapper = world.getMapper(Inventory.class);
		aiMapper = world.getMapper(AiProfile.class);
		portalMap = world.getMapper(Portal.class);
		lockMap = world.getMapper(Lockable.class);
		tags = world.getManager(TagManager.class);
		
		entities = new ArrayMap<String, Entity>();
	}
	
	@Override
	public void show(){
		super.show();
		setupMenu();
		
		setHeight(height * getChildren().size);
	}
	
	public void setupMenu(){
		//sets up item button
		if(map.getItemLayer().isOccupied((int)InputHandler.getMapClicked().x, (int)InputHandler.getMapClicked().y, map.currentLayer)){
			add(createButton("View item", "itemInfo")).row();
			add(createButton("Pickup item", "pickupItem")).row();
			
			i = map.getItemLayer().map[(int)InputHandler.getMapClicked().x][(int)InputHandler.getMapClicked().y][map.currentLayer];
		}
		
		//click contains entity
		if(map.getEntityLayer().isOccupied((int)InputHandler.getMapClicked().x, (int)InputHandler.getMapClicked().y, map.currentLayer)){
			World world = map.getWorld();
			Entity e = world.getEntity(map.getEntityLayer().map[(int)InputHandler.getMapClicked().x][(int)InputHandler.getMapClicked().y][map.currentLayer]);
			
			//right clicked on player entity
			if(e.getId() == tags.getEntity("player").getId()) return;
			
			//open/close portal(doors, windows etc)
			if(portalMap.has(e)){
				add(createButton("Open/Close", "openClose")).row();
			}
			
			//non-npc container
			if(inventoryMapper.has(e) && !aiMapper.has(e)){
				add(createButton("View container", "container")).row();
				
				if(lockMap.has(e)){
					add(createButton("Lock/Unlock", "unlock")).row();;
					add(createButton("Break lock", "breakLock")).row();
				}
				entities.put("container", world.getEntity(map.getEntityLayer().map[(int)InputHandler.getMapClicked().x][(int)InputHandler.getMapClicked().y][map.currentLayer]));
			//npc
			}else if(aiMapper.has(e)){
				add(createButton("View entity", "entity")).row();
				
				entities.put("entity", world.getEntity(map.getEntityLayer().map[(int)InputHandler.getMapClicked().x][(int)InputHandler.getMapClicked().y][map.currentLayer]));
			}
		}
	}
	
	private Button createButton(String text, final String key){
		LabelStyle lStyle = new LabelStyle(font, Color.WHITE);
		Button b = GuiUtils.createButton("gui/button", null, skin);
		b.add(new Label(text,lStyle));
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				Vector2 tmp = new Vector2(InputHandler.getVec3Clicked().x, InputHandler.getVec3Clicked().y);
				tmp = getStage().screenToStageCoordinates(tmp);
				
				switch(key){
				case "item":
					((ItemWindow)windows.get("item")).showWindow(tmp.x, tmp.y, i);
					break;
				case "entity":
					((NpcInfoWindow)windows.get("npcInfo")).showWindow(tmp.x, tmp.y, entities.get("entity"));
					break;
				case "container":
					((ContainerWindow)windows.get("containerContents")).showWindow(tmp.x, tmp.y, entities.get("container").getComponent(Inventory.class));
					break;
				case "unlock":
					
					break;
				case "breakLock":
					
					break;
				case "openClose":
					
					break;
				}
				hide();
			}
		});
		return b;
	}
}
