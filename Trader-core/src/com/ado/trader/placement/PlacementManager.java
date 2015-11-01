package com.ado.trader.placement;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.entities.WallDirection;
import com.ado.trader.input.InputHandler;
import com.ado.trader.utils.FileLogger;
import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class PlacementManager {
	Placeable placementSelection;
	EntityPlaceable entityPl;
	TilePlaceable tilePl;
	WallPlaceable wallPl;
	FeaturePlaceable featurePl;
	ItemPlaceable itemPl;
	
	InputHandler input;
	EntityFactory entities;

	public PlacementManager(GameServices gameRes) {
		placementSelection=null;
		
		this.entities = gameRes.getEntities();
		
		entityPl = new EntityPlaceable(gameRes);
		tilePl = new TilePlaceable(gameRes.getMap());
		wallPl = new WallPlaceable(gameRes.getMap(), gameRes.getRenderer().getRenderEntitySystem());
		
		featurePl = new FeaturePlaceable(gameRes);
		
		itemPl = new ItemPlaceable(gameRes.getMap());
		
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
	
	public void setPlacementSelection(String type, String name){
		switch(type){
		case "feature":
			placementSelection = featurePl;
			featurePl.featureName = name;
			featurePl.spriteIndex = 0;
			break;
		case "item":
			placementSelection = itemPl;
			itemPl.itemId = name;
			break;
		case "entity":
			placementSelection = entityPl;
			entityPl.entityName = name;
			entityPl.spriteIndex = 0;
			break;
		case "wall":
			placementSelection = wallPl;
			wallPl.entityName = name;

			wallPl.first = WallDirection.SW;
			break;
		}
	}
	
	public void setPlacementSelection(String type, int id) {
		switch(type){
		case "tile":
			placementSelection = tilePl;
			tilePl.id = id;
			break;
		}
	}
	
	public void render(SpriteBatch batch){
		if(placementSelection == null) return;
		placementSelection.renderPreview(batch);
	}
	public EntityPlaceable getEntityPl() {
		return entityPl;
	}
	public TilePlaceable getTilePl() {
		return tilePl;
	}
	public WallPlaceable getWallPl() {
		return wallPl;
	}
	public ItemPlaceable getItemPl() {
		return itemPl;
	}
	public FeaturePlaceable getFeaturePl() {
		return featurePl;
	}
	public void resetSelection(){
		placementSelection.clearSettings();
		placementSelection = null;
	}
	public Placeable getPlacementSelection() {
		return placementSelection;
	}
}
