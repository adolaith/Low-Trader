package com.ado.trader.placement;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.input.InputHandler;
import com.ado.trader.utils.FileLogger;
import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;

public class PlacementManager {
	Placeable placementSelection;
	
	ArrayMap<String, Placeable> placeableList; 
	
	InputHandler input;
	EntityFactory entities;

	public PlacementManager(GameServices gameRes) {
		placementSelection = null;
		placeableList = new ArrayMap<String, Placeable>();
		
		this.entities = gameRes.getEntities();
				
		placeableList.put("entity", new EntityPlaceable(gameRes));
		placeableList.put("tile", new TilePlaceable(gameRes.getMap()));
		placeableList.put("wall", new WallPlaceable(gameRes.getMap(), gameRes.getRenderer().getRenderEntitySystem()));
		placeableList.put("npc", new NpcPlaceable(gameRes));
		placeableList.put("item", new ItemPlaceable(gameRes.getMap()));
		placeableList.put("feature", new FeaturePlaceable(gameRes));
		
		FileLogger.writeLog("PlacementManager: started.");
	}
	
	public boolean handleClick(Vector2 mapUp){
		if(placementSelection == null){
			return false;
		}

		//mouse was dragged
		if(mapUp.x != InputHandler.mapClicked.x && mapUp.y != InputHandler.mapClicked.y){
			//get smallest x,y
			Vector2 start = new Vector2(Math.min((int)mapUp.x, (int)InputHandler.getMapClicked().x), 
					Math.min((int)mapUp.y, (int)InputHandler.getMapClicked().y));
			//get largest x,y
			Vector2 widthHeight = new Vector2(Math.max((int)mapUp.x, (int)InputHandler.getMapClicked().x), 
					Math.max((int)mapUp.y, (int)InputHandler.getMapClicked().y));
			
			placementSelection.dragPlace(start, widthHeight);
			
		//single tile placement
		}else{
			placementSelection.place((int)mapUp.x, (int)mapUp.y);
		}
		//clear settings after placement
		if(!Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)){
			placementSelection.clearSettings();
			placementSelection = null;
		}
		return true;
	}
	
	public void rotateSelection(){
		placementSelection.rotateSelection();
	}
	
	public void setPlacementSelection(String type, String baseid){
		placementSelection = placeableList.get(type);
		placementSelection.setSelection(baseid);
	}
	
	public void setPlacementSelection(String type, int id) {
		placementSelection = placeableList.get(type);
		((TilePlaceable)placementSelection).setSelection(id);
	}
	
	public void render(SpriteBatch batch){
		if(placementSelection == null) return;
		placementSelection.renderPreview(batch);
	}
	
	public Placeable getPlaceable(String name){
		return placeableList.get(name);
	}
	public void resetSelection(){
		placementSelection.clearSettings();
		placementSelection = null;
	}
	public Placeable getPlacementSelection() {
		return placementSelection;
	}
}
