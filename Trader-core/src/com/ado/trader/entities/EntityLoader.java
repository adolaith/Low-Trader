package com.ado.trader.entities;

import com.ado.trader.GameMain;
import com.ado.trader.entities.components.AiProfile;
import com.ado.trader.entities.components.Animation;
import com.ado.trader.entities.components.Area;
import com.ado.trader.entities.components.AttributeTable;
import com.ado.trader.entities.components.Feature;
import com.ado.trader.entities.components.Inventory;
import com.ado.trader.entities.components.Locations;
import com.ado.trader.entities.components.Mask;
import com.ado.trader.entities.components.Money;
import com.ado.trader.entities.components.Movement;
import com.ado.trader.entities.components.Name;
import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.SpriteComp;
import com.ado.trader.entities.components.Target;
import com.ado.trader.entities.components.Wall;
import com.ado.trader.items.ItemFactory;
import com.ado.trader.map.EntityLayer;
import com.ado.trader.rendering.EntityRenderSystem;
import com.ado.trader.utils.FileLogger;
import com.ado.trader.utils.GameServices;
import com.ado.trader.utils.IsoUtils;
import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.ComponentMapper;
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
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;

public class EntityLoader {
	ComponentMapper<Name> nameMap;
	ComponentMapper<SpriteComp> spriteMap;
	ComponentMapper<Animation> animMap;
	ComponentMapper<AiProfile> aiMap;
	ComponentMapper<Area> areaMap;
	ComponentMapper<Inventory> inventoryMap;
	ComponentMapper<Movement> movementMap;
	ComponentMapper<AttributeTable> attributeMap;
	ComponentMapper<Position> positionMap;
	ComponentMapper<Money> moneyMap;
	ComponentMapper<Wall> wallMap;
	ComponentMapper<Mask> maskMap;
	ComponentMapper<Feature> featureMap;
	GroupManager groupManager;
	
	World world;

	public EntityLoader(World world){
		this.world = world;
		
		nameMap = world.getMapper(Name.class);
		spriteMap = world.getMapper(SpriteComp.class);
		animMap = world.getMapper(Animation.class);
		aiMap = world.getMapper(AiProfile.class);
		areaMap = world.getMapper(Area.class);
		inventoryMap = world.getMapper(Inventory.class);
		movementMap = world.getMapper(Movement.class);
		attributeMap = world.getMapper(AttributeTable.class);
		positionMap = world.getMapper(Position.class);
		wallMap = world.getMapper(Wall.class);
		maskMap = world.getMapper(Mask.class);
		featureMap = world.getMapper(Feature.class);
		moneyMap = world.getMapper(Money.class);
		
		groupManager = world.getManager(GroupManager.class);
	}

	//use after loading entity profiles. Loads level data
	public void loadSavedEntities(String fileName, GameServices gameRes){
		Json json = new Json();
		JsonValue file = null;
		
		if(fileName.startsWith("data")){
			file = json.fromJson(null, Gdx.files.internal(fileName + "/entities"));
		}else{
			file = json.fromJson(null, Gdx.files.external(fileName + "/entities"));
		}

		if(file == null){
			Gdx.app.log(GameMain.LOG, "Save file is empty"); 
			return;
		}
		
		file = file.child;
		for(JsonValue e = file.child; e != null; e = e.next){
			loadEntity(gameRes, e);
		}
	}

	private void loadEntity(GameServices gameRes, JsonValue entityData){
		Entity e = EntityFactory.createEntity(entityData.getString("name"));
		
		for(JsonValue d = entityData.child; d != null; d = d.next){
			switch(d.name){
			case "area":
				Array<Vector2> vecs = new Array<Vector2>();
				for(JsonValue v = d.child; v != null; v = v.next){
					float[] xy = v.asFloatArray();
					vecs.add(new Vector2(xy[0], xy[1]));
				}
				Area area = areaMap.get(e);
				area.area = vecs;
				break;
			
			case "pos":
				int[] pos = d.asIntArray();
				Vector2 mapXY = IsoUtils.getColRow(pos[0], pos[1], 
						gameRes.getMap().getTileWidth(), gameRes.getMap().getTileHeight());
				mapXY.x += 0.5;
				
				positionMap.get(e).setPosition((int)mapXY.x, (int)mapXY.y, pos[2]);
				
				if(groupManager.isInGroup(e, "wall")){
					gameRes.getMap().getWallLayer().addToMap(e.getId(), (int)mapXY.x, (int)mapXY.y, pos[2]);
				}else{
					EntityLayer eLayer = gameRes.getMap().getEntityLayer();
					eLayer.addToMap(e.getId(), (int)mapXY.x, (int)mapXY.y, pos[2]);
					eLayer.markAreaOccupied((int)mapXY.x, (int)mapXY.y, pos[2], 
							e, gameRes.getMap().getEntityLayer());
				}
				break;
				
			case "sprite":
				int[] spriteIndexes = d.asIntArray();
				SpriteComp sC = spriteMap.get(e);
				sC.mainSprite = spriteIndexes[0];
				if(spriteIndexes.length > 1){
					sC.secondSprite = spriteIndexes[1];
				}
				break;
				
			case "animation":
				String[] skel = d.asStringArray();
				Animation a = animMap.get(e);
				a.getSkeleton().setSkin(skel[0]);
				a.getSkeleton().setAttachment("head", skel[1]);
				break;
				
			case "inventory":
				String[] inventory = d.asStringArray();
				Inventory i = inventoryMap.get(e);
				
				i.max = Integer.valueOf(inventory[0]);
				
				for(int x = 1; x<inventory.length; x++){
					Entity item = ItemFactory.createItem(inventory[x]);
					i.add(item.id);
				}
				break;
				
			case "wall":
				String[] wallDir = d.asStringArray();
				Wall w = wallMap.get(e);
				w.firstSprite = Direction.valueOf(wallDir[0]);
				if(wallDir.length > 1){
					w.secondSprite = Direction.valueOf(wallDir[1]);
				}
				break;
				
			case "mask":
				String[] maskData = d.asStringArray();
				Mask m = new Mask();
				m.maskName = maskData[0];
				m.maskIndex = Integer.valueOf(maskData[1]);
				e.edit().add(m);
				break;
				
			case "feature":
				String[] feature = d.asStringArray();
				Feature f = new Feature(feature[0], Integer.valueOf(feature[1]));
				e.edit().add(f);
				break;
				
			case "money":
				int value = d.asInt();
				moneyMap.get(e).value = value;
				break;
			}
			
		}
	}
}
