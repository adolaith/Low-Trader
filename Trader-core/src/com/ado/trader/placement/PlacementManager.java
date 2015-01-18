package com.ado.trader.placement;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.gui.GameServices;
import com.ado.trader.input.InputHandler;
import com.ado.trader.items.ItemFactory;
import com.ado.trader.map.Map;
import com.ado.trader.rendering.EntityRenderSystem;
import com.ado.trader.rendering.EntityRenderSystem.Direction;
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
	
	boolean editMode;
	InputHandler input;
	EntityRenderSystem entityRenderer;
	EntityFactory entities;

	public PlacementManager(InputHandler input, Map map, GameServices gui, EntityFactory entities, ItemFactory items, EntityRenderSystem entityRenderer) {
		editMode = true;
		placementSelection=null;
		editMode = true;
		
		this.input = input;
		this.entityRenderer = entityRenderer;
		this.entities = entities;
		
		entityPl = new EntityPlaceable(map, input, gui, entities);
		tilePl = new TilePlaceable(map, input);
		wallPl = new WallPlaceable(map, entities, entityRenderer);
		featurePl = new FeaturePlaceable(input, map, entities);
		itemPl = new ItemPlaceable(map, items);
	}
	
	public boolean handleClick(Vector2 mapUp, InputHandler input){
		if(placementSelection==null || !editMode){return false;}

		//mouse was dragged
		if(mapUp.x!=input.mapClicked.x&&mapUp.y!=input.mapClicked.y){
			//get smallest x,y
			Vector2 start = new Vector2(Math.min((int)mapUp.x, (int)input.getMapClicked().x), 
					Math.min((int)mapUp.y, (int)input.getMapClicked().y));
			//get largest x,y
			Vector2 widthHeight = new Vector2(Math.max((int)mapUp.x, (int)input.getMapClicked().x), 
					Math.max((int)mapUp.y, (int)input.getMapClicked().y));
			
			//delete area
			if(placementSelection.delete){
				for(int x=(int)start.x;x<=widthHeight.x;x++){
					for(int y=(int)start.y;y<=widthHeight.y;y++){
						placementSelection.remove(x, y);
					}
				}
				placementSelection.delete = false;
			//run area placement
			}else{
				placementSelection.dragPlace(start, widthHeight);	
			}
		//single tile delete
		}else if(placementSelection.delete){
			placementSelection.remove((int)mapUp.x, (int)mapUp.y);
		//single tile placement
		}else{
			placementSelection.place((int)mapUp.x, (int)mapUp.y);
		}
		//not holding shit when placing clears currently selected placement type after placement
		if(!Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)){
			if(placementSelection == wallPl)wallPl.resetDirections();
			placementSelection.delete = false;
			placementSelection = null;
		}
		return true;
	}
	
	public void rotateSelection(){
		placementSelection.rotateSelection(entityRenderer);
	}
	
	public void setPlacementSelection(String type, String name){
		switch(type){
		case "feature":
			placementSelection = featurePl;
			featurePl.featureId = name;
			featurePl.spriteId = Integer.valueOf(entities.getFeatures().getFeature(name).get("sprite").split(",")[0]);
			featurePl.sprite = entityRenderer.getStaticSprites().get(featurePl.spriteId);
			break;
		case "item":
			placementSelection = itemPl;
			itemPl.itemId = name;
			break;
		}
	}
	
	public void setPlacementSelection(String type, int id) {
		switch(type){
		case "entity":
			placementSelection = entityPl;
			if(id == 0){
				entityPl.entityTypeID = 0;
				entityPl.spriteId = 0;
				entityPl.sprite = null;
				break;
			}
			entityPl.entityTypeID = id;
			entityPl.spriteId = Integer.valueOf(entities.getEntities().get(id).get("sprite").split(",")[0]);
			entityPl.sprite = entityRenderer.getStaticSprites().get(entityPl.spriteId);
			break;
		case "tile":
			placementSelection = tilePl;
			tilePl.tileId = id;
			break;
		case "wall":
			placementSelection = wallPl;
			wallPl.entityTypeID = id;
			if(id==0){
				wallPl.firstId = 0;
				wallPl.firstSprite = null;
				wallPl.first = null;
				break;
			}
			wallPl.firstId = Integer.valueOf(entities.getEntities().get(id).get("sprite").split(",")[0]);
			wallPl.firstSprite = entityRenderer.getStaticSprites().get(wallPl.firstId);
			wallPl.first = Direction.SW;
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
	public void resetSelection(){
		placementSelection = null;
	}
	public Placeable getPlacementSelection() {
		return placementSelection;
	}
	public boolean isEditMode() {
		return editMode;
	}
	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}
}
