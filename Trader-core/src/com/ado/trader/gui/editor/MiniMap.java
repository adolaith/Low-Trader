package com.ado.trader.gui.editor;

import com.ado.trader.gui.BasicWindow;
import com.ado.trader.gui.GuiUtils;
import com.ado.trader.map.Chunk;
import com.ado.trader.map.Map;
import com.ado.trader.map.MapRegion;
import com.ado.trader.map.Tile;
import com.ado.trader.map.TileLayer;
import com.ado.trader.utils.GameServices;
import com.ado.trader.utils.IsoUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.JsonValue;

public class MiniMap extends BasicWindow {
	final int NULL_COLOUR = 99;
	final int CAMERA_COLOUR = 98;
	final int TILE_MODE = 0;
	final int CHUNK_MODE = 1;
	final int REGION_MODE = 2;
	
	int mapMode;
	ArrayMap<Integer, Texture> mapColours;
	
	Map map;
	GameServices gameRes;

	public MiniMap(GameServices gameRes) {
		super("MiniMap", 200, 252, gameRes.getFont(), gameRes.getSkin(), gameRes.getStage());
		setName("map");
		body.top();
		this.map = gameRes.getMap();
		this.gameRes = gameRes;
		
		createTextures();
		
		ButtonStyle bStyle = GuiUtils.setButtonStyle("gui/button", null, gameRes.getSkin());
		Button b = new Button(bStyle);
		LabelStyle ls = new LabelStyle(gameRes.getFont(), Color.BLACK);
		Label l = new Label("R", ls);
		b.add(l);
		b.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				mapMode = REGION_MODE;
			}
		});
		
		body.add(b).center().padRight(2).size(24);
		
		b = new Button(bStyle);
		l = new Label("C", ls);
		b.add(l);
		b.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				mapMode = CHUNK_MODE;
			}
		});
		
		body.add(b).center().padRight(2).size(24);
		
		b = new Button(bStyle);
		l = new Label("T", ls);
		b.add(l);
		b.addListener(new ClickListener(){
			public void clicked (InputEvent event, float x, float y) {
				mapMode = TILE_MODE;
			}
		});
		
		body.add(b).center().padRight(2).size(24).row();
	}
	
	@Override
	public void draw (Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		int pxWidth, pxHeight;
		float isoX, isoY;
		MapRegion r;
		TileLayer tLayer;
		
		Vector3 camVec = gameRes.getCam().position;
		Vector2 camMap = IsoUtils.getColRow((int) camVec.x, (int) camVec.y, Map.tileWidth, Map.tileHeight);
		
		switch(mapMode){
		case 0:
			pxWidth = 6;
			pxHeight = 6;
			
			Chunk c = map.getChunk((int) camMap.x, (int) camMap.y);
			
			if(c == null) return;
			
			tLayer = c.getTiles();
			
			//draw tiles
			for(int x = 0; x < tLayer.getWidth(); x++){
				for(int y = 0; y < tLayer.getHeight(); y++){
					Tile t = tLayer.map[x][y];
					
					if(t == null){
						batch.draw(mapColours.get(NULL_COLOUR), getX() + 4 + (x * pxWidth), getY() + 4 + (y * pxHeight), pxWidth, pxHeight);
					}else{
						batch.draw(mapColours.get(t.getId()), getX() + 4 + (x * pxWidth), getY() + 4 + (y * pxHeight), pxWidth, pxHeight);
					}
				}
			}
			
			//draw camera dot
			camMap = Map.worldVecToTile((int) camMap.x, (int) camMap.y);
			batch.draw(mapColours.get(CAMERA_COLOUR), getX() + 4 + (camMap.x * pxWidth), getY() + 4 + (camMap.y * pxHeight), pxWidth, pxHeight);
			
			break;
		case 1:
			pxWidth = 2;
			pxHeight = 2;
			r = map.getRegionMap()[1][1];
			
			if(r == null) return;
			
			Vector2 tmp = Map.worldVecToChunk((int) camMap.x, (int) camMap.y);
			for(int rX = 0; rX < 3; rX++){
				for(int rY = 0; rY < 3; rY++){
					if(r.getChunk(rX, rY) == null){
						isoX = getX() + 4 + (rX * 32) * pxWidth;
						isoY = getY() + 4 + (rY * 32) * pxHeight;
						
						batch.draw(mapColours.get(NULL_COLOUR), isoX, isoY, 64, 64);
						
						continue;
					}
					
					tLayer = r.getChunk(rX, rY).getTiles();
					for(int x = 0; x < tLayer.getWidth(); x++){
						for(int y = 0; y < tLayer.getHeight(); y++){
							Tile t = tLayer.map[x][y];
							
							isoX = getX() + 4 + (x * pxWidth) + (rX * tLayer.getWidth()) * pxWidth;
							isoY = getY() + 4 + (y * pxHeight) + (rY * tLayer.getHeight()) * pxHeight;
							
							if(t == null){
								batch.draw(mapColours.get(NULL_COLOUR), isoX, isoY, pxWidth, pxHeight);
							}else{
								batch.draw(mapColours.get(t.getId()), isoX, isoY, pxWidth, pxHeight);
							}
						}
					}
				}
			}
			
			//draw camera dot
			tmp = Map.worldVecToChunk((int) camMap.x, (int) camMap.y);
			camMap = Map.worldVecToTile((int) camMap.x, (int) camMap.y);
			isoX = getX() + 4 + (camMap.x * pxWidth) + (tmp.x * 32) * pxWidth;
			isoY = getY() + 4 + (camMap.y * pxHeight) + (tmp.y * 32) * pxHeight;

			batch.draw(mapColours.get(CAMERA_COLOUR), isoX, isoY, pxWidth, pxHeight);
			break;
		case 2:
			pxWidth = 21;
			pxHeight = 21;
			
			tmp = Map.worldVecToRegion((int) camMap.x, (int) camMap.y);
			for(int rX = 0; rX < 3; rX++){
				for(int rY = 0; rY < 3; rY++){
					if(map.getRegionMap()[rX][rY] == null){
						isoX = getX() + 4 + (rX * (pxWidth * 3));
						isoY = getY() + 4 + (rY * (pxHeight * 3));
						batch.draw(mapColours.get(NULL_COLOUR), isoX, isoY, pxWidth * 3, pxHeight * 3);
						
						continue;
					}
					
					r = map.getRegionMap()[rX][rY];
					
					for(int cX = 0; cX < 3; cX++){
						for(int cY = 0; cY < 3; cY++){
							isoX = getX() + 4 + (rX * (pxWidth * 3)) + (cX * pxWidth);
							isoY = getY() + 4 + (rY * (pxHeight * 3)) + (cY * pxHeight);
							
							if(r.getChunk(cX, cY) == null){
								batch.draw(mapColours.get(NULL_COLOUR), isoX, isoY, pxWidth, pxHeight);
							}else{
								if(cY % 2 == 0){
									if(cX % 2 == 0){
										batch.draw(mapColours.get(0), isoX, isoY, pxWidth, pxHeight);
									}else{
										batch.draw(mapColours.get(1), isoX, isoY, pxWidth, pxHeight);
									}
								}else{
									if(cX % 2 == 0){
										batch.draw(mapColours.get(1), isoX, isoY, pxWidth, pxHeight);
									}else{
										batch.draw(mapColours.get(0), isoX, isoY, pxWidth, pxHeight);
									}
								}
							}
						}
					}
					
					
				}
			}
			
			//draw camera dot
			camMap = Map.worldVecToChunk((int) camMap.x, (int) camMap.y);

			isoX = getX() + 4 + (tmp.x * (pxWidth * 3)) + (camMap.x * pxWidth);
			isoY = getY() + 4 + (tmp.y * (pxHeight * 3)) + (camMap.y * pxHeight);

			batch.draw(mapColours.get(CAMERA_COLOUR), isoX, isoY, pxWidth, pxHeight);
			break;
		}
	}

	private void createTextures(){
		mapColours = new ArrayMap<Integer, Texture>();
		
		ArrayMap<Integer, JsonValue> profiles = map.getTilePool().getTileProfiles();
		
		for(Integer i: profiles.keys()){
			float[] values = profiles.get(i).get("mapColour").asFloatArray();
			
			Pixmap pix = new Pixmap(4, 4, Format.RGBA8888);
			pix.setColor(values[0], values[1], values[2], 1);
			pix.fill();
			Texture tex = new Texture(pix);
			pix.dispose();
			
			mapColours.put(i, tex);
		}
		
		Pixmap pix = new Pixmap(4, 4, Format.RGBA8888);
		pix.setColor(0, 0, 0, 1);
		pix.fill();
		Texture tex = new Texture(pix);
		pix.dispose();
		
		mapColours.put(NULL_COLOUR, tex);
		
		pix = new Pixmap(4, 4, Format.RGBA8888);
		pix.setColor(1, 1, 0, 1);
		pix.fill();
		tex = new Texture(pix);
		pix.dispose();
		
		mapColours.put(CAMERA_COLOUR, tex);
	}
}
