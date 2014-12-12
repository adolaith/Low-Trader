package com.ado.trader.map;

import com.ado.trader.screens.GameScreen;

public class LayerGroup {
	public TileLayer tileLayer;
	public EntityLayer entityLayer;
	public WallLayer wallLayer;
	public ZoneLayer zoneLayer;
	public ItemLayer itemLayer;
	
	public LayerGroup(GameScreen game, int w, int h){
		tileLayer = new TileLayer(game, w, h);
		entityLayer = new EntityLayer(game, w, h);
		wallLayer = new WallLayer(game, w, h);
		zoneLayer = new ZoneLayer(game,w,h);
		itemLayer = new ItemLayer(game, w, h);
	}
	public void saveLayers(GameScreen game, StringBuilder tile, StringBuilder zone, StringBuilder items){
		tileLayer.saveMap(game, tile);
		zoneLayer.saveZones(zone);
		itemLayer.saveMap(game, items);
	}
}
