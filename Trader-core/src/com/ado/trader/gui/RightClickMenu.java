package com.ado.trader.gui;

import com.ado.trader.entities.components.AiProfile;
import com.ado.trader.entities.components.Inventory;
import com.ado.trader.entities.components.Lockable;
import com.ado.trader.entities.components.Name;
import com.ado.trader.entities.components.Portal;
import com.ado.trader.input.InputHandler;
import com.ado.trader.map.Chunk;
import com.ado.trader.map.Map;
import com.ado.trader.utils.GameServices;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.annotations.Wire;
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

@Wire
public class RightClickMenu extends ContextMenu{
	Button viewEntity, viewZone, viewItem;
	
	ArrayMap<String, BasicWindow> windows;
	
	BitmapFont font;
	Skin skin;
	Map map;
	
	ComponentMapper<Name> nameMap;
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
		
	}
	
	@Override
	public void show(){
		super.show();
		setupMenu();
		
		setHeight(height * getChildren().size);
	}
	
	public void setupMenu(){
		Vector2 isoClick = InputHandler.getMapClicked();
		World world = map.getWorld();
		
		Chunk chunk = map.getChunk((int) isoClick.x, (int) isoClick.y);
		
		Vector2 tileVec = map.worldVecToTile((int) isoClick.x, (int) isoClick.y);
		
		//sets up item button
		for(int c = 0; c < chunk.getItems().map[(int) tileVec.x][(int) tileVec.y].length; c++){
			if(chunk.getItems().map[(int) tileVec.x][(int) tileVec.y][c] == null){
				continue;
			}
			Entity i = world.getEntity(chunk.getItems().map[(int) tileVec.x][(int) tileVec.y][c]);
			add(createButton("View " + nameMap.get(i).getName(), "itemInfo", i)).row();
			add(createButton("Pickup " + nameMap.get(i).getName(), "pickupItem", i)).row();
		}
		
		//click contains entity
		for(int c = 0; c < chunk.getEntities().map[(int) tileVec.x][(int) tileVec.y].length; c++){
			if(chunk.getEntities().map[(int) tileVec.x][(int) tileVec.y][c] == null){
				continue;
			}
			
			Entity e = world.getEntity(chunk.getEntities().map[(int) tileVec.x][(int) tileVec.y][c]);

			//right clicked on player entity
			if(e.getId() == tags.getEntity("player").getId()) return;

			//open/close portal(doors, windows etc)
			if(portalMap.has(e)){
				add(createButton("Open/Close", "openClose", e)).row();
			}

			//non-npc container
			if(inventoryMapper.has(e) && !aiMapper.has(e)){
				add(createButton("View container", "container", e)).row();

				if(lockMap.has(e)){
					add(createButton("Lock/Unlock", "unlock", e)).row();
					add(createButton("Break lock", "breakLock", e)).row();
				}
			//npc
			}else if(aiMapper.has(e)){
				add(createButton("View NPC", "npc", e)).row();
			}
		}
	}
	
	private Button createButton(String text, final String key, final Entity e){
		LabelStyle lStyle = new LabelStyle(font, Color.WHITE);
		Button b = GuiUtils.createButton("gui/button", null, skin);
		b.add(new Label(text,lStyle));
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				Vector2 tmp = new Vector2(InputHandler.getVec3Clicked().x, InputHandler.getVec3Clicked().y);
				tmp = getStage().screenToStageCoordinates(tmp);
				
				switch(key){
				case "item":
					((ItemWindow)windows.get("item")).showWindow(tmp.x, tmp.y, e);
					break;
				case "entity":
//					((NpcInfoWindow)windows.get("npcInfo")).showWindow(tmp.x, tmp.y, entities.get("entity"));
					break;
				case "container":
					Inventory i = inventoryMapper.get(e);
					((ContainerWindow)windows.get("containerContents")).showWindow(tmp.x, tmp.y, i);
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
