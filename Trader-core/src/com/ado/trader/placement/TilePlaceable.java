package com.ado.trader.placement;

import com.ado.trader.input.InputHandler;
import com.ado.trader.map.Map;
import com.ado.trader.map.Tile;
import com.ado.trader.map.TileOverlay.Mask;
import com.ado.trader.utils.IsoUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;

public class TilePlaceable extends Placeable {
	Integer tileId;
	InputHandler input;
	
	public TilePlaceable(Map map, InputHandler input) {
		super(map);
		this.input = input;
	}
	public void place(int x, int y) {
		Tile[][][] tileMap = map.getTileLayer().map;
		if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)){
			changeTile(tileMap[x][y][map.currentLayer], tileMap[x][y][map.currentLayer].id, Mask.CENTRE);	
		}else{
			changeTile(tileMap[x][y][map.currentLayer], null, null);
		}
	}
	public void changeTile(Tile t,Integer overlay, Mask mask){
		ArrayMap<String, String> profile = map.getTilePool().getTileProfile(tileId);
		t.id = tileId;
		for(String key: profile.keys()){
			switch(key){
			case "travel":
				t.travelCost = Integer.valueOf(profile.get(key));
				break;
			}
		}
		t.mask = mask;
		t.overlayId = overlay;
	}
	public void renderPreview(SpriteBatch batch){
		if(Gdx.input.isButtonPressed(Buttons.LEFT)){
			Vector2 mousePos = IsoUtils.getColRow((int)input.getMousePos().x, (int)input.getMousePos().y, map.getTileWidth(), map.getTileHeight());
			Vector2 start = new Vector2(Math.min((int)mousePos.x, (int)input.getMapClicked().x), Math.min((int)mousePos.y, (int)input.getMapClicked().y));
			Vector2 widthHeight = new Vector2(Math.max((int)mousePos.x, (int)input.getMapClicked().x), Math.max((int)mousePos.y, (int)input.getMapClicked().y));
			batch.begin();
			for(int x=(int)start.x;x<=widthHeight.x;x++){
				for(int y=(int)start.y;y<=widthHeight.y;y++){
					Vector2 tmp = IsoUtils.getIsoXY(x, y, map.getTileWidth(), map.getTileHeight());
					batch.draw(map.getTileSprites().get(tileId), tmp.x, tmp.y, map.getTileWidth(), map.getTileHeight() + ( map.getTileHeight()/2 ));
				}
			}
			batch.end();
		}
	}
	public void dragPlace(Vector2 start, Vector2 widthHeight) {
		Tile[][][] tileMap = map.getTileLayer().map;
		if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)){
			setTransitionTiles(start, widthHeight, tileMap);
		}else{
			for(int x=(int) start.x;x<widthHeight.x+1; x++){
				for(int y=(int) start.y;y<widthHeight.y+1; y++){
					changeTile(tileMap[x][y][map.currentLayer], null, null);
				}
			}
		}
	}
	private void setTransitionTiles(Vector2 start, Vector2 widthHeight, Tile[][][] tileMap){
		for(int x=(int) start.x;x<widthHeight.x+1; x++){
			for(int y=(int) start.y;y<widthHeight.y+1; y++){
				if(x==start.x&&y==start.y){		//s
					changeTile(tileMap[x][y][map.currentLayer], tileMap[x][y][map.currentLayer].id, Mask.SOUTH);
				}else if(x==start.x&&y==widthHeight.y){		//w
					changeTile(tileMap[x][y][map.currentLayer], tileMap[x][y][map.currentLayer].id, Mask.WEST);
				}else if(x==widthHeight.x&&y==widthHeight.y){		//n
					changeTile(tileMap[x][y][map.currentLayer], tileMap[x][y][map.currentLayer].id, Mask.NORTH);
				}else if(x==widthHeight.x&&y==start.y){		//e
					changeTile(tileMap[x][y][map.currentLayer], tileMap[x][y][map.currentLayer].id, Mask.EAST);
				}else if(x<=widthHeight.x&&y==start.y){		//se
					changeTile(tileMap[x][y][map.currentLayer], tileMap[x][y][map.currentLayer].id, Mask.SE);
				}else if(x<=widthHeight.x&&y==widthHeight.y){		//nw
					changeTile(tileMap[x][y][map.currentLayer], tileMap[x][y][map.currentLayer].id, Mask.NW);
				}else if(y<=widthHeight.y&&x==start.x){				//sw
					changeTile(tileMap[x][y][map.currentLayer], tileMap[x][y][map.currentLayer].id, Mask.SW);
				}else if(y<=widthHeight.y&&x==widthHeight.x){		//ne
					changeTile(tileMap[x][y][map.currentLayer], tileMap[x][y][map.currentLayer].id, Mask.NE);
				}else{
					changeTile(tileMap[x][y][map.currentLayer], null, null);
				}
			}
		}
	}
	public void remove(int x, int y) {
	}
}
