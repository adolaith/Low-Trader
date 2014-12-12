package com.ado.trader.utils.placement;

import com.ado.trader.map.Map;
import com.ado.trader.map.Tile;
import com.ado.trader.map.TileOverlay.Mask;
import com.ado.trader.screens.GameScreen;
import com.ado.trader.utils.InputHandler;
import com.ado.trader.utils.IsoUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;

public class TilePlaceable extends Placeable {
	Integer tileId;
	
	public TilePlaceable(GameScreen game) {
		super(game);
	}
	public void place(int x, int y) {
		Tile[][] map = game.getMap().getCurrentLayerGroup().tileLayer.map;
		if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)){
			changeTile(map[x][y], map[x][y].id, Mask.CENTRE);	
		}else{
			changeTile(map[x][y], null, null);
		}
	}
	public void changeTile(Tile t,Integer overlay, Mask mask){
		ArrayMap<String, String> profile = game.getMap().getTilePool().getTileProfile(tileId);
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
			InputHandler input = game.getInput();
			Map m = game.getMap();
			Vector2 mousePos = IsoUtils.getColRow((int)input.getMousePos().x, (int)input.getMousePos().y, m.getTileWidth(), m.getTileHeight());
			Vector2 start = new Vector2(Math.min((int)mousePos.x, (int)input.getMapClicked().x), Math.min((int)mousePos.y, (int)input.getMapClicked().y));
			Vector2 widthHeight = new Vector2(Math.max((int)mousePos.x, (int)input.getMapClicked().x), Math.max((int)mousePos.y, (int)input.getMapClicked().y));
			batch.begin();
			for(int x=(int)start.x;x<=widthHeight.x;x++){
				for(int y=(int)start.y;y<=widthHeight.y;y++){
					Vector2 tmp = IsoUtils.getIsoXY(x, y, m.getTileWidth(), m.getTileHeight());
					batch.draw(game.getMap().getTileSprites().get(tileId), tmp.x, tmp.y, m.getTileWidth(), m.getTileHeight() + ( m.getTileHeight()/2 ));
				}
			}
			batch.end();
		}
	}
	public void dragPlace(Vector2 start, Vector2 widthHeight) {
		Tile[][] map = game.getMap().getCurrentLayerGroup().tileLayer.map;
		if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)){
			setTransitionTiles(start, widthHeight, map);
		}else{
			for(int x=(int) start.x;x<widthHeight.x+1; x++){
				for(int y=(int) start.y;y<widthHeight.y+1; y++){
					changeTile(map[x][y], null, null);
				}
			}
		}
	}
	private void setTransitionTiles(Vector2 start, Vector2 widthHeight, Tile[][] map){
		for(int x=(int) start.x;x<widthHeight.x+1; x++){
			for(int y=(int) start.y;y<widthHeight.y+1; y++){
				if(x==start.x&&y==start.y){		//s
					changeTile(map[x][y], map[x][y].id, Mask.SOUTH);
				}else if(x==start.x&&y==widthHeight.y){		//w
					changeTile(map[x][y], map[x][y].id, Mask.WEST);
				}else if(x==widthHeight.x&&y==widthHeight.y){		//n
					changeTile(map[x][y], map[x][y].id, Mask.NORTH);
				}else if(x==widthHeight.x&&y==start.y){		//e
					changeTile(map[x][y], map[x][y].id, Mask.EAST);
				}else if(x<=widthHeight.x&&y==start.y){		//se
					changeTile(map[x][y], map[x][y].id, Mask.SE);
				}else if(x<=widthHeight.x&&y==widthHeight.y){		//nw
					changeTile(map[x][y], map[x][y].id, Mask.NW);
				}else if(y<=widthHeight.y&&x==start.x){				//sw
					changeTile(map[x][y], map[x][y].id, Mask.SW);
				}else if(y<=widthHeight.y&&x==widthHeight.x){		//ne
					changeTile(map[x][y], map[x][y].id, Mask.NE);
				}else{
					changeTile(map[x][y], null, null);
				}
			}
		}
	}
	public void remove(int x, int y) {
	}
}