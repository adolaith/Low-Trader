package com.ado.trader.entities;

import com.ado.trader.GameMain;
import com.ado.trader.entities.components.Feature;
import com.ado.trader.entities.components.Mask;
import com.ado.trader.entities.components.Wall;
import com.ado.trader.screens.GameScreen;
import com.ado.trader.systems.EntityRenderSystem.Direction;
import com.ado.trader.utils.FileParser;
import com.ado.trader.utils.MaskingSystem;
import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

public class EntityFeatures {
	ArrayMap<String, ArrayMap<String, String>> featuresList;
	MaskingSystem maskSys;
	
	public EntityFeatures(GameScreen game){
		featuresList = loadEntityProfiles("data/Features", game);
		maskSys = game.getRenderer().getRenderEntitySystem().getMasks();
	}
	public void applyFeature(Entity e, String featureName, int spriteId, Sprite sprite){
		ArrayMap<String, String> feature = featuresList.get(featureName);
		for(String key: feature.keys()){
			switch(key){
			case "sprite":
				e.addComponent(new Feature(sprite, spriteId));
				break;
			case "mask":
				applyMask(e, feature.get(key));
				break;
			}
		}
	}
	private void applyMask(Entity e, String maskString){
		Wall wC = e.getComponent(Wall.class);
		Mask m = new Mask();
		String[] list = maskString.split(",");
		if(wC.firstSprite==Direction.NE||wC.firstSprite==Direction.SW){
			m.mask = maskSys.getMask(list[1]);
		}else if(wC.firstSprite==Direction.SE||wC.firstSprite==Direction.NW){
			m.mask = maskSys.getMask(list[0]);
		}
		e.addComponent(m);
	}
	private ArrayMap<String, ArrayMap<String,String>> loadEntityProfiles(String fileName, GameScreen game) {
		ArrayMap<String, ArrayMap<String,String>> entities = new ArrayMap<String, ArrayMap<String,String>>();
		FileParser p = game.getParser(); 
		p.initParser(fileName, false, false);
		ArrayMap<Integer, Sprite> sprites = new ArrayMap<Integer, Sprite>();

		Array<ArrayMap<String, String>> data = p.readFile();
		for(ArrayMap<String, String> template: data){
			ArrayMap<String,String> entity = new ArrayMap<String, String>();
			for(String key: template.keys()){
				switch(key){
				case "sprite":
					String[] tmp = template.get(key).split(",");
					String idList = "";
					for(String s:tmp){
						if(s.isEmpty()){continue;}
						String[] element = s.split("'");
						idList+=element[0]+",";
					}
					createSprite(sprites, template.get(key), game);
					entity.put(key, idList);
					break;
				case "mask":
					String[] lst = template.get(key).split(",");
					for(String s:lst){
						if(s.isEmpty()){continue;}
						Sprite msk = game.getAtlas().createSprite(s);
						msk.scale(1f);
						game.getRenderer().getRenderEntitySystem().getMasks().loadMask(s, msk);
					}
					entity.put(key, template.get(key));
					break;
				default:
					entity.put(key, template.get(key));
					break;
				}
			}
			entities.put(template.get("id"), entity);
		}
		game.getRenderer().getRenderEntitySystem().getStaticSprites().putAll(sprites);
		return entities;
	}
	private void createSprite(ArrayMap<Integer, Sprite> sprites, String list, GameScreen game){
		String[] tmp = list.split(",");
		try{
			for(String s:tmp){
				if(s.isEmpty()){continue;}
				String[] element = s.split("'");
				Sprite sprite = game.getAtlas().createSprite(element[1]);
				sprite.scale(1f);
				sprites.put(Integer.valueOf(element[0]), sprite);
			}
		}catch(Exception e){Gdx.app.log(GameMain.LOG, "Error loading feature sprites...:"+e);}
	}
	public ArrayMap<String, String> getFeature(String name){
		return featuresList.get(name);
	}
	public ArrayMap<String, ArrayMap<String, String>> getFeaturesList() {
		return featuresList;
	}
}
