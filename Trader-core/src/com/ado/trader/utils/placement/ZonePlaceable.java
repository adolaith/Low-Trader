package com.ado.trader.utils.placement;

import com.ado.trader.map.FarmZone;
import com.ado.trader.map.HomeZone;
import com.ado.trader.map.LayerGroup;
import com.ado.trader.map.StoreZone;
import com.ado.trader.map.TileLayer;
import com.ado.trader.map.WorkZone;
import com.ado.trader.map.Zone;
import com.ado.trader.map.ZoneLayer;
import com.ado.trader.map.Zone.ZoneType;
import com.ado.trader.screens.GameScreen;
import com.ado.trader.utils.IsoUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class ZonePlaceable extends Placeable {
	Sprite zoneTile;
	public boolean workZone;

	public ZonePlaceable(GameScreen game) {
		super(game);
		zoneTile = game.getAtlas().createSprite("gui/zoneTile");
		zoneTile.setScale(2f);
		workZone = false;
	}

	public void place(int x, int y) {
		ZoneLayer zLayer = game.getMap().getCurrentLayerGroup().zoneLayer;
		
		//sets or unsets work tiles in zone
		if(workZone){
			if(zLayer.isOccupied(x, y)){
				Zone z = zLayer.zoneMap[x][y];
				if(z instanceof WorkZone){
					WorkZone wZone = (WorkZone) z;
					if(wZone.isWorkTile(x, y)){
						wZone.removeWorkTile(new Vector2(x, y));
					}else{
						wZone.addWorkTile(new Vector2(x, y));
					}
					if(!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
						workZone = false;
					}
					return;
				}
			}
		}else{
			//add to existing layer
			if(zLayer.isOccupied(x, y)){
				zLayer.addToZone(new Vector2(x,y), zLayer.zoneMap[x][y]);
			//show zone gui window
			}else{
				Vector2 tmp = IsoUtils.getIsoXY(x, y, game.getMap().getTileWidth(), game.getMap().getTileHeight());
				game.getGui().getZoneDialog().showDialog((int)tmp.x, (int)tmp.y);
			}
		}
	}

	public void dragPlace(Vector2 start, Vector2 widthHeight) {
		Array<Vector2> area = new Array<Vector2>();
		Zone existingZone = null;
		ZoneLayer zLayer = game.getMap().getCurrentLayerGroup().zoneLayer;
		//populate area array
		for(int x=(int) start.x;x<widthHeight.x+1; x++){
			for(int y=(int) start.y;y<widthHeight.y+1; y++){
				Vector2 tmp = new Vector2(x,y);
				area.add(tmp);
				if(zLayer.isOccupied(x,y)&&existingZone==null){
					existingZone = zLayer.zoneMap[x][y];
				}
			}
		}
		//add area to existing zone
		if(existingZone!=null){
			zLayer.addToZone(area, existingZone);
		//show zone gui window		
		}else{
			Vector2 tmp = IsoUtils.getIsoXY((int)start.x, (int)start.y, game.getMap().getTileWidth(), game.getMap().getTileHeight());
			game.getGui().getZoneDialog().showDialog((int)tmp.x, (int)tmp.y);
			game.getGui().getZoneDialog().area = area;
		}
	}
	
	//create single tile zone
	public Zone createNewZone(int id, Vector2 zoneArea, ZoneType type, LayerGroup lGroup){
		Zone z = null;
		switch(type){
		case FARM:
			z = new FarmZone(id, zoneArea);
			TileLayer tLayer = lGroup.tileLayer;
			tLayer.map[(int)zoneArea.x][(int)zoneArea.y].id = 2;
			break;
		case HOME:
			z = new HomeZone(id, zoneArea, type);
			break;
		case STORE:
			z = new StoreZone(id, zoneArea, type);
			break;
		}
		ZoneLayer zLayer = lGroup.zoneLayer;
		zLayer.getZoneList(type).add(z);
		zLayer.zoneMap[(int)zoneArea.x][(int)zoneArea.y] = z;
		return z;
	}
	//create area zone
	public Zone createNewZone(int id, Array<Vector2> area, ZoneType type, LayerGroup lGroup){
		Zone z = null;
		switch(type){
		case FARM:
			z = new FarmZone(id, area);
			TileLayer tLayer = lGroup.tileLayer;
			for(Vector2 v: area){
				tLayer.map[(int)v.x][(int)v.y].id = 2;
			}
			break;
		case HOME:
			z= new HomeZone(id, area, type);
			break;
		case STORE:
			z = new StoreZone(id, area, type);
			break;
		}
		
		ZoneLayer zLayer = lGroup.zoneLayer;
		zLayer.getZoneList(type).add(z);
		
		for(Vector2 v: area){
			zLayer.zoneMap[(int)v.x][(int)v.y] = z;
		}
		return z;
	}
	
	public void remove(int x, int y) {
		if(game.getMap().getCurrentLayerGroup().zoneLayer.isOccupied(x, y)){
			game.getMap().getCurrentLayerGroup().zoneLayer.deleteFromMap(x, y);
		}
	}
	
	public void renderPreview(SpriteBatch batch){
		//on click, render preview
		if(Gdx.input.isButtonPressed(Buttons.LEFT)){
			if(batch.isDrawing())batch.end();
			batch.begin();
			Vector2 mousePos = IsoUtils.getColRow((int)game.getInput().getMousePos().x, 
					(int)game.getInput().getMousePos().y, game.getMap().getTileWidth(), game.getMap().getTileHeight());
			Vector2 start = new Vector2(Math.min((int)mousePos.x, (int)game.getInput().getMapClicked().x), 
					Math.min((int)mousePos.y, (int)game.getInput().getMapClicked().y));
			Vector2 widthHeight = new Vector2(Math.max((int)mousePos.x, (int)game.getInput().getMapClicked().x), 
					Math.max((int)mousePos.y, (int)game.getInput().getMapClicked().y));
			
			for(int x=(int)start.x;x<=widthHeight.x;x++){
				for(int y=(int)start.y;y<=widthHeight.y;y++){
					Vector2 tmp = IsoUtils.getIsoXY(x, y, 
							game.getMap().getTileWidth(), game.getMap().getTileHeight());
					batch.draw(zoneTile, tmp.x, tmp.y,
							game.getMap().getTileWidth(),game.getMap().getTileHeight());
				}
			}
			batch.end();
		}
	}
	public Sprite getTileSprite(){
		return zoneTile;
	}
}
