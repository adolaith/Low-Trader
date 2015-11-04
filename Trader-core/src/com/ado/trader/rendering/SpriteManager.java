package com.ado.trader.rendering;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

public class SpriteManager {
	ArrayMap<String, Sprite[]> entitySprites;
	ArrayMap<String, Sprite> itemSprites;
	ArrayMap<String, Sprite[]> wallSprites;
	ArrayMap<String, Sprite[]> featureSprites;

	public SpriteManager(TextureAtlas atlas) {
		loadSprites(atlas);

	}
	private void loadSprites(TextureAtlas atlas){
		entitySprites = new ArrayMap<String, Sprite[]>();
		itemSprites = new ArrayMap<String, Sprite>();
		wallSprites = new ArrayMap<String, Sprite[]>();
		featureSprites = new ArrayMap<String, Sprite[]>();
		
		Array<AtlasRegion> regions = atlas.getRegions();
		
		for(AtlasRegion r: regions){
			if(!r.name.contains("/")){
				loadEntitySprites(atlas, r);
			}else if(r.name.startsWith("walls")){
				loadWallSprites(atlas, r);
			}else if(r.name.startsWith("items")){
				loadItemSprites(atlas, r);
			}else if(r.name.startsWith("features")){
				loadFeatureSprites(atlas, r);
			}
		}
		
	}
	
	private void loadEntitySprites(TextureAtlas atlas, AtlasRegion region){
		if(entitySprites.containsKey(region.name) || region.index > 0){
			return;
		}
		
		Sprite[] list = new Sprite[4];
		
		if(region.index == -1){
			list[0] = new Sprite(region);
			list[0].scale(1);
			
			entitySprites.put(region.name, list);
			return;
		}
		
		Sprite sprite = new Sprite(region);
		sprite.scale(1);
		list[0] = sprite;
		
		sprite = new Sprite(sprite);
		sprite.flip(true, false);
		list[1] = sprite;
		
		sprite = atlas.createSprite(region.name, region.index + 1);
		if(sprite != null){
			sprite.scale(1);
			list[2] = sprite;

			sprite = new Sprite(sprite);
			sprite.flip(true, false);
			list[3] = sprite;
		}
		
		entitySprites.put(region.name, list);
	}
	//wall sprite direction index; SE/NW = 0, SW/NE = 1
	private void loadWallSprites(TextureAtlas atlas, AtlasRegion region){
		String name = region.name.substring(region.name.indexOf('/') + 1);
		
		if(wallSprites.containsKey(name)){
			Sprite s = new Sprite(region);
			s.scale(1);
			wallSprites.get(name)[region.index] = s;
			return;
		}
		
		Sprite[] list = new Sprite[2];
		Sprite s = new Sprite(region);
		s.scale(1);
		list[region.index] = s;
		
		wallSprites.put(name, list);
	}
	private void loadFeatureSprites(TextureAtlas atlas, AtlasRegion region){
		if(featureSprites.containsKey(region.name) || region.index > 0){
			return;
		}
		
		Sprite[] list = new Sprite[4];
		
		if(region.index == -1){
			list[0] = new Sprite(region);
			list[0].scale(1);
			
			featureSprites.put(region.name, list);
			return;
		}
		
		Sprite sprite = new Sprite(region);
		sprite.scale(1);
		list[0] = sprite;
		
		sprite = new Sprite(sprite);
		sprite.flip(true, false);
		list[1] = sprite;
		
		sprite = atlas.createSprite(region.name, region.index + 1);
		if(sprite != null){
			sprite.scale(1);
			list[2] = sprite;

			sprite = new Sprite(sprite);
			sprite.flip(true, false);
			list[3] = sprite;
		}
		
		featureSprites.put(region.name, list);
	}
	private void loadItemSprites(TextureAtlas atlas, AtlasRegion region){
		Sprite s = new Sprite(region);
		s.scale(1);
		itemSprites.put(region.name, s);
	}
	
	public Sprite[] getEntitySprites(String key){
		return entitySprites.get(key);
	}
	public Sprite[] getWallSprites(String key){
		return wallSprites.get(key);
	}
	public Sprite getItemSprite(String key){
		return itemSprites.get(key);
	}
	public Sprite[] getFeatureSprites(String key){
		return featureSprites.get(key);
	}
	public ArrayMap<String, Sprite[]> getEntitySprites() {
		return entitySprites;
	}
	public ArrayMap<String, Sprite> getItemSprites() {
		return itemSprites;
	}
	public ArrayMap<String, Sprite[]> getWallSprites() {
		return wallSprites;
	}
	public ArrayMap<String, Sprite[]> getFeatureSprites() {
		return featureSprites;
	}
}
