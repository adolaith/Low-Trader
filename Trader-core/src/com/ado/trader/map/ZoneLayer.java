package com.ado.trader.map;

import com.ado.trader.map.WorkZone.WorkArea;
import com.ado.trader.map.Zone.ZoneType;
import com.ado.trader.screens.GameScreen;
import com.ado.trader.utils.FileParser;
import com.ado.trader.utils.IsoUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

public class ZoneLayer implements Layer{
	public Zone[][] zoneMap;
	ArrayMap<ZoneType, Array<Zone>> zoneList;
	GameScreen game;

	public ZoneLayer(GameScreen game, int w, int h) {
		this.game = game;
		zoneMap = new Zone[w][h];
		initZoneList();
	}
	private void initZoneList(){
		zoneList = new ArrayMap<Zone.ZoneType, Array<Zone>>();
		for(ZoneType t: ZoneType.values()){
			zoneList.put(t, new Array<Zone>());
		}
	}
	
	public void saveZones(StringBuilder str){
		FileParser p = game.getParser();  
		p.string = str;
		
		for(Array<Zone> zones: zoneList.values()){
			for(Zone z:zones){
				Array<String> pos = new Array<String>();
				for(Vector2 vec: z.zoneTiles){
					pos.add(vec.x+"'"+vec.y);
				}
				//save general data
				p.addElement("id", "" + z.getId());
				p.addElement("pos", pos);
				p.addElement("type", z.type.name());
				
				//save work tile data
				if(z instanceof WorkZone){
					WorkZone wZone = (WorkZone) z;
					for(WorkArea a: wZone.workAreas){
						if(a.vec != null){
							p.addElement("workTiles", a.vec.x + "'" + a.vec.y);
						}
						if(a.area != null){
							String value = "";
							for(Vector2 key: a.area){
								value += key.x + "'" + key.y + ",";
							}
							p.addElement("workArea", value);
						}	
						p.addElement("workProfile", a.aiWorkProfile);
					}
				}
				
				//save type specific data
				switch(z.type){
				case FARM:
					FarmZone f = (FarmZone) z;
					p.addElement("item", f.itemName);
					p.addElement("days", ""+f.daysGrowing);
					p.addElement("grow", ""+f.growScore);
					p.addElement("maintenance", ""+f.maintenance);
					break;
				case HOME:
					HomeZone h = (HomeZone) z;
					p.addElement("maxOccupants", ""+h.maxOccupants);
					if(h.garden != null){
						p.addElement("garden", ""+h.garden.id);
					}
					break;
				}
				p.newNode();
			}
		}
	}
	@Override
	public boolean isOccupied(int x, int y) {
		return zoneMap[x][y]!=null;
	}
	@Override
	public void deleteFromMap(int x, int y) {
		Zone z = zoneMap[x][y];
		zoneMap[x][y] = null;
		z.removeTile(new Vector2(x,y));
		if(z.zoneTiles.size==0){
			zoneList.get(z.type).removeValue(z, false);
		}
	}
	public void addToZone(Vector2 vec, Zone z){
		zoneMap[(int)vec.x][(int)vec.y] = z;
		z.addZoneTile(vec);
	}
	public void addToZone(Array<Vector2> area, Zone z){
		for(Vector2 vec: area){
			addToZone(vec, z);
		}
	}
	public void renderAllZones(SpriteBatch batch){
		Sprite s = game.getPlaceManager().getZonePl().getTileSprite();
		batch.begin();
		for(Array<Zone> arr: zoneList.values()){
			
			for(Zone z: arr){
				for(Vector2 vec: z.zoneTiles){
					vec = IsoUtils.getIsoXY((int)vec.x, (int)vec.y, 
							game.getMap().getTileWidth(), game.getMap().getTileHeight());
			
					batch.draw(s, vec.x, vec.y, 
							game.getMap().getTileWidth(), game.getMap().getTileHeight());
				}
				if(z instanceof WorkZone){
					batch.setColor(Color.GREEN);
					WorkZone wZone = (WorkZone) z;
					for(WorkArea a: wZone.workAreas){
						//work tiles
						if(a.vec != null){
							//isometric/screen coords
							Vector2 vec = IsoUtils.getIsoXY((int)a.vec.x, (int)a.vec.y, 
									game.getMap().getTileWidth(), game.getMap().getTileHeight());

							batch.draw(s, vec.x, vec.y, 
									game.getMap().getTileWidth(), game.getMap().getTileHeight());				
						}
						//work area
						if(a.area != null){
							for(Vector2 vec : a.area){
								//isometric/screen coords
								Vector2 v = IsoUtils.getIsoXY((int)vec.x, (int)vec.y, 
										game.getMap().getTileWidth(), game.getMap().getTileHeight());

								batch.draw(s, v.x, v.y, 
										game.getMap().getTileWidth(), game.getMap().getTileHeight());
							}

						}
					}

					batch.setColor(Color.WHITE);
				}
			}
		}
		batch.end();
	}
	
	public Zone checkAreaForZone(Vector2 origin, Vector2 widthHeight){
		for(int x=(int)origin.x; x<=(int)widthHeight.x; x++){
			for(int y=(int)origin.y; y<=(int)widthHeight.y; y++){
				if(zoneMap[x][y]!=null){
					return zoneMap[x][y];
				}
			}
		}
		return null;
	}
	public boolean checkForZone(Vector2 click){
		if(zoneMap[(int)click.x][(int)click.y]!=null){
			return true;
		}
		return false;
	}
	//finds neighbours of tile at x,y to a depth of n (unordered)
	public Array<Zone> getNeighborZones(int x, int y, int n) {
		Array<Zone> neighbours = new Array<Zone>();
		for(int i = x-n; i<x+n+n; i++){
			for(int j = y-n; j<y+n+n; j++){
				if(i==x&&j==y){
					continue;
				}
				if(zoneMap[i][j]!=null){
					neighbours.add(zoneMap[i][j]);
				}
			}
		}
		return neighbours;
	}
	public Array<Zone> getZoneList(ZoneType type){
		return zoneList.get(type);
	}
}
