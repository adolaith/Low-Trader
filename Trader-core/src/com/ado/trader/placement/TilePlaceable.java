package com.ado.trader.placement;

import com.ado.trader.input.InputHandler;
import com.ado.trader.map.Chunk;
import com.ado.trader.map.Map;
import com.ado.trader.map.MapRegion;
import com.ado.trader.map.Tile;
import com.ado.trader.map.TileMask;
import com.ado.trader.utils.IsoUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;

public class TilePlaceable extends Placeable {
	Integer id;
	public Integer mask;
	public int dir;
	int nextRegionId;
	
	public TilePlaceable(Map map) {
		super(map, null);
	}
	
	@Override
	public void place(int mapX, int mapY) {
		//create new map region
		if(map.getRegion(mapX, mapY) == null){
			createRegion(mapX, mapY);
		}
		
		Chunk c = map.getChunk(mapX, mapY);
		//create new region chunk
		if(c == null){
			c = createChunk(mapX, mapY);
		}
		
		Vector2 tile = Map.worldVecToTile(mapX, mapY);
		TileMask m = null;
		Tile t = c.getTiles().map[(int) tile.x][(int) tile.y];
		
		//apply mask only if tile exists
		if(t != null){
			if(mask != null){
				m = new TileMask(c.getTiles().map[(int) tile.x][(int) tile.y].getId(), mask, dir);
			}
		}else{
			t = new Tile(id);
			c.getTiles().map[(int) tile.x][(int) tile.y] = t;
		}

		changeTile(t, m);
	}
	
	@Override
	public void dragPlace(Vector2 start, Vector2 widthHeight) {
		for(int x=(int) start.x; x < widthHeight.x + 1; x++){
			for(int y=(int) start.y; y < widthHeight.y + 1; y++){
				place(x, y);
			}
		}
	}
	
	private void createRegion(int mapX, int mapY){
		Vector2 regVec = Map.worldVecToRegion(mapX, mapY);
		MapRegion r = new MapRegion();
		
		r.setId(nextRegionId);
		incrementID();
		
		map.getRegionMap()[(int) regVec.x][(int) regVec.y] = r;
		
		//search for neighbour regions
		MapRegion n;
		if((int) regVec.x + 1 < map.getRegionMap().length){
			n = map.getRegionMap()[(int) regVec.x + 1][(int) regVec.y];
			if(n != null){
				r.addConnectedRegion("e", n.getId());
				n.addConnectedRegion("w", r.getId());
			}
		}
		if((int) regVec.x - 1 >= 0){
			n = map.getRegionMap()[(int) regVec.x - 1][(int) regVec.y];
			if(n != null){
				r.addConnectedRegion("w", n.getId());
				n.addConnectedRegion("e", r.getId());
			}
		}
		if((int) regVec.y + 1 < map.getRegionMap()[(int) regVec.x].length){
			n = map.getRegionMap()[(int) regVec.x][(int) regVec.y + 1];
			if(n != null){
				r.addConnectedRegion("n", n.getId());
				n.addConnectedRegion("s", r.getId());
			}
		}
		if((int) regVec.y - 1 >= 0){
			n = map.getRegionMap()[(int) regVec.x][(int) regVec.y - 1];
			if(n != null){
				r.addConnectedRegion("s", n.getId());
				n.addConnectedRegion("n", r.getId());
			}
		}
	}
	private Chunk createChunk(int mapX, int mapY){
		Vector2 cVec = Map.worldVecToChunk(mapX, mapY);
		Chunk c = new Chunk(map.getWorld());
		MapRegion r = map.getRegion(mapX, mapY);
		r.setChunk((int) cVec.x, (int) cVec.y, c);
		
		return c;
	}
	
	public void changeTile(Tile t, TileMask mask){
		JsonValue profile = map.getTilePool().getTileProfile(id);
		t.setId(id);
		for(JsonValue v = profile.child; v != null; v = v.next){
			switch(v.name){
			case "travel":
				t.setTravelCost(v.asInt());
				break;
			}
		}
		t.setMask(mask);
	}
	
	public void renderPreview(SpriteBatch batch){
		Vector2 mousePos = IsoUtils.getColRow((int)InputHandler.getMousePos().x, (int)InputHandler.getMousePos().y, map.getTileWidth(), map.getTileHeight());
		
		//button pressed
		if(Gdx.input.isButtonPressed(Buttons.LEFT) && !InputHandler.getMapClicked().isZero() && !InputHandler.getMousePos().isZero()){
			Vector2 start = new Vector2(Math.min((int)mousePos.x, (int)InputHandler.getMapClicked().x), Math.min((int)mousePos.y, (int)InputHandler.getMapClicked().y));
			Vector2 widthHeight = new Vector2(Math.max((int)mousePos.x, (int)InputHandler.getMapClicked().x), Math.max((int)mousePos.y, (int)InputHandler.getMapClicked().y));
			batch.begin();
			for(int x=(int)start.x;x<=widthHeight.x;x++){
				for(int y=(int)start.y;y<=widthHeight.y;y++){
					Vector2 tmp = IsoUtils.getIsoXY(x, y, map.getTileWidth(), map.getTileHeight());
					batch.draw(map.getTileSprites().get(id), tmp.x, tmp.y, map.getTileWidth(), map.getTileHeight() + ( map.getTileHeight()/2 ));
					
					drawMask(batch, new Vector2(x,y), tmp);
				}
			}
			batch.end();
		}else{
		//preview
			batch.begin();
			Vector2 tmp = IsoUtils.getIsoXY((int) mousePos.x, (int) mousePos.y, map.getTileWidth(), map.getTileHeight());
			
			batch.draw(map.getTileSprites().get(id), tmp.x, tmp.y, map.getTileWidth(), map.getTileHeight() + ( map.getTileHeight()/2 ));
			
			drawMask(batch, mousePos, tmp);
			
			batch.end();
		}
	}
	private void drawMask(SpriteBatch batch, Vector2 mousePos, Vector2 mouseIso){
		if(mask != null){
			Chunk c = map.getChunk((int) mousePos.x, (int) mousePos.y);
			if(c != null){
				Vector2 tile = Map.worldVecToTile((int) mousePos.x, (int) mousePos.y);
				Tile t = c.getTiles().map[(int) tile.x][(int) tile.y];
				if(t != null){
					map.getTileMasks().drawMask(batch, mouseIso.x, mouseIso.y, mask, dir);
					map.getTileMasks().drawOverlay(batch, mouseIso.x, mouseIso.y, map.getTileSprites().get(t.getId()));
				}
			}
		}
	}
	
	@Override
	public void rotateSelection() {
		Sprite[][] sprites = map.getTileMasks().getMaskSprites();
		
		if(mask != null){
			if(dir + 1 < sprites[mask].length){
				if(sprites[mask][dir + 1] != null){
					dir++;
				}else{
					dir = 0;
				}
				
			}else{
				dir = 0;
			}
		}
	}
	
	@Override
	public void clearSettings() {
		id = null;
		mask = null;
		dir = 0;
	}
	
	public void setSelection(int id){
		this.id = id;
	}
	
	public void incrementID(){
		this.nextRegionId++;
	}
	public int getNextRegionId(){
		return nextRegionId;
	}

	@Override
	public void setSelection(String baseid) {
	}
}
