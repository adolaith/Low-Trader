 package com.ado.trader.entities;

import com.ado.trader.GameMain;
import com.ado.trader.entities.components.Animation;
import com.ado.trader.entities.components.Area;
import com.ado.trader.entities.components.Feature;
import com.ado.trader.entities.components.Health;
import com.ado.trader.entities.components.Hunger;
import com.ado.trader.entities.components.Inventory;
import com.ado.trader.entities.components.Mask;
import com.ado.trader.entities.components.Money;
import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.SpriteComp;
import com.ado.trader.entities.components.Wall;
import com.ado.trader.items.Item;
import com.ado.trader.items.ItemFactory;
import com.ado.trader.map.EntityLayer;
import com.ado.trader.rendering.EntityRenderSystem;
import com.ado.trader.rendering.EntityRenderSystem.Direction;
import com.ado.trader.utils.FileParser;
import com.ado.trader.utils.GameServices;
import com.ado.trader.utils.IsoUtils;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;

public class EntityLoader {
	World world;

	public EntityLoader(World world){
		this.world = world;
	}
	public ArrayMap<String, SkeletonData> loadSpineData(TextureAtlas atlas, EntityFactory entities){
		ArrayMap<String, SkeletonData> skeletons = new ArrayMap<String, SkeletonData>();
		FileHandle[] files = Gdx.files.internal("./bin/data/anim").list();
		
		for(FileHandle file: files){
			if(file.extension().matches("json")){
				
				SkeletonJson json = new SkeletonJson(atlas);
				json.setScale(2f);
				
				SkeletonData skelData = json.readSkeletonData(file);
				skeletons.put(skelData.getName(), skelData);
				
				EntityFactory.animationPool.put(skelData.getName(), new AnimationStateData(skelData));
			}
		}
		return skeletons;
	}

	//use after loading entity profiles. Loads level data
	public void loadSavedEntities(String fileName, GameServices gameRes){
		FileParser p = gameRes.getParser();
		boolean external = !fileName.contains("bin");
		
		p.initParser(fileName+"/entities", false, external);
		if(!p.getFile().exists()){Gdx.app.log(GameMain.LOG, "Save file is empty"); return;}
		Array<ArrayMap<String, String>> data = p.readFile();
		for(ArrayMap<String, String> entityData: data){
			loadEntity(gameRes, entityData);
		}
	}

	private void loadEntity(GameServices gameRes, ArrayMap<String, String> entityData){
		Entity e = EntityFactory.createEntity(Integer.valueOf(entityData.get("id")));
		for(String key:entityData.keys()){
			if(key=="id"){
				continue;
			}
			switch(key){
			case "area":
				String[] areaList=entityData.get(key).split("'");
				Array<Vector2> vecs = new Array<Vector2>();
				for(String s: areaList){
					String[] xy = s.split(",");
					vecs.add(new Vector2(Float.valueOf(xy[0]), Float.valueOf(xy[1])));
				}
				Area area = gameRes.getWorld().getMapper(Area.class).get(e);
				area.area = vecs;
				break;
			case "pos":
				String[] pos=entityData.get(key).split(",");
				Vector2 mapXY=IsoUtils.getColRow(Integer.valueOf(pos[0]), Integer.valueOf(pos[1]), gameRes.getMap().getTileWidth(), gameRes.getMap().getTileHeight());
				mapXY.x+=0.5;
				world.getMapper(Position.class).get(e).setPosition((int)mapXY.x, (int)mapXY.y, Integer.valueOf(pos[2]));
				if(e.getWorld().getManager(GroupManager.class).isInGroup(e, "wall")){
					gameRes.getMap().getWallLayer().addToMap(e.getId(), (int)mapXY.x, (int)mapXY.y, Integer.valueOf(pos[2]));
				}else{
					EntityLayer eLayer = gameRes.getMap().getEntityLayer();
					eLayer.addToMap(e.getId(), (int)mapXY.x, (int)mapXY.y, Integer.valueOf(pos[2]));
					eLayer.markAreaOccupied((int)mapXY.x, (int)mapXY.y, Integer.valueOf(pos[2]), e, gameRes.getMap().getEntityLayer());
				}
				break;
			case "sprite":
				String[] s = entityData.get(key).split(",");
				SpriteComp sC = world.getMapper(SpriteComp.class).get(e);
				sC.mainId = Integer.valueOf(s[0]);
				sC.mainSprite = new Sprite(gameRes.getRenderer().getRenderEntitySystem().getStaticSprites().get(sC.mainId));
				sC.mainSprite.flip(Boolean.valueOf(s[1]), false);
				if(s.length>2){
					sC.secondId = Integer.valueOf(s[2]);
					sC.secondarySprite = new Sprite(gameRes.getRenderer().getRenderEntitySystem().getStaticSprites().get(Integer.valueOf(s[2])));
					sC.secondarySprite.flip(Boolean.getBoolean(s[3]), false);
				}
				break;
			case "animation":
				String[] skel = entityData.get(key).split(",");
				e.getComponent(Animation.class).getSkeleton().setSkin(skel[0]);
				e.getComponent(Animation.class).getSkeleton().setAttachment("head", skel[1]);
				break;
			case "health":
				String[] health = entityData.get(key).split(",");
				world.getMapper(Health.class).get(e).loadValues(Integer.valueOf(health[0]), Integer.valueOf(health[1]));
				break;
			case "hunger":
				String[] hunger = entityData.get(key).split(",");
				world.getMapper(Hunger.class).get(e).loadValues(Integer.valueOf(hunger[0]), Integer.valueOf(hunger[1]));
				break;
			case "money":
				String[] money = entityData.get(key).split(",");
				world.getMapper(Money.class).get(e).value = Integer.valueOf(money[0]);
				break;
			case "inventory":
				String[] inventory = entityData.get(key).split(",");
				Inventory i = world.getMapper(Inventory.class).get(e);
				i.max = Integer.valueOf(inventory[0]);
				for(int x = 1; x<inventory.length; x++){
					Item item = ItemFactory.createItem(inventory[x]);
					i.add(item);
				}
				break;
//			case "workZone":
//				String[] workData = entityData.get(key).split(",");
//				int workId = Integer.valueOf(workData[1]);
//				Building workBuilding = gameRes.getBuildingCollection().getBuilding(workId);
//
//				for(WorkArea a: workBuilding.getWorkAreas()){
//					if(a.getId() == Integer.valueOf(workData[0])){
//						if(a.getVec() != null){
//							a.setEntityId(e.getId());
//						}else{
//							a.getAllWorkers().add(e.getId());
//						}
//						world.getMapper(Locations.class).get(e).setWork(a);
//					}
//				}
//
//				break;
//			case "homeZone":
//				int homeId = Integer.valueOf(entityData.get(key));
//				Building homeBuilding = gameRes.getBuildingCollection().getBuilding(homeId);
//				world.getMapper(Locations.class).get(e).setHome(homeBuilding);
//				
//				break;
			case "wall":
				String[] wallDir = entityData.get(key).split(",");
				world.getMapper(Wall.class).get(e).firstSprite = Direction.valueOf(wallDir[0]);
				if(wallDir.length>1){
					world.getMapper(Wall.class).get(e).secondSprite = Direction.valueOf(wallDir[1]);
				}
				break;
			case "mask":
				Mask m = new Mask();
				m.mask = gameRes.getRenderer().getRenderEntitySystem().getMasks().getMask(entityData.get(key));
				e.edit().add(m);
				break;
			case "feature":
				String[] fData = entityData.get(key).split(",");
				Sprite sprite = new Sprite(gameRes.getRenderer().getRenderEntitySystem().getStaticSprites().get(Integer.valueOf(fData[0])));
				sprite.flip(Boolean.parseBoolean(fData[1]), false);
				Feature f = new Feature(sprite, Integer.valueOf(fData[0]));
				e.edit().add(f);
				break;
			}
			
		}
//		world.changedEntity(e);
	}
	//loads entity profile templates
	public void loadEntityProfiles(String fileName, TextureAtlas atlas, EntityFactory collection, EntityRenderSystem entityRenderer, FileParser p) {
		ArrayMap<Integer, ArrayMap<String,String>> entities = collection.getEntities();
		ArrayMap<Integer, ArrayMap<String,String>> npcs = collection.getNpcs();
		p.initParser(fileName, false, false);
		ArrayMap<Integer, Sprite> sprites = new ArrayMap<Integer, Sprite>();

		Array<ArrayMap<String, String>> data = p.readFile();
		for(ArrayMap<String, String> template: data){
			if(template.containsKey("animation")){
				loadNpcEntity(template, npcs);
			}else{
				loadStaticEntity(template, entities, sprites, atlas, entityRenderer);
			}
		}
		entityRenderer.loadSprites(sprites);
	}
	private void loadNpcEntity(ArrayMap<String, String> template, ArrayMap<Integer, ArrayMap<String,String>> npcs){
		ArrayMap<String,String> entity = new ArrayMap<String, String>();
		for(String key: template.keys()){
			entity.put(key, template.get(key));
		}
		npcs.put(Integer.valueOf(entity.get("id")), entity);
	}
	private void loadStaticEntity(ArrayMap<String, String> template, ArrayMap<Integer, ArrayMap<String,String>> entities, ArrayMap<Integer, Sprite> sprites, TextureAtlas atlas, EntityRenderSystem entityRenderer){
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
				createSprite(sprites, template.get(key), atlas);
				entity.put(key, idList);
				break;
			case "mask":
				String[] lst = template.get(key).split(",");
				for(String s:lst){
					if(s.isEmpty()){continue;}
					Sprite msk = atlas.createSprite(s);
					msk.scale(1f);
					entityRenderer.getMasks().loadMask(s, msk);
				}
				entity.put(key, template.get(key));
				break;
			default:
				entity.put(key, template.get(key));
			}
		}
		entities.put(Integer.valueOf(entity.get("id")), entity);
	}
	private void createSprite(ArrayMap<Integer, Sprite> sprites, String list, TextureAtlas atlas){
		String[] tmp = list.split(",");
		try{
			for(String s:tmp){
				if(s.isEmpty()){continue;}
				String[] element = s.split("'");
				Sprite sprite = atlas.createSprite(element[1]);
				sprite.scale(1f);
				sprites.put(Integer.valueOf(element[0]), sprite);
			}
		}catch(Exception e){Gdx.app.log(GameMain.LOG, "Error loading entity sprites...:"+e);}
	}
}
