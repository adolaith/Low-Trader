package com.ado.trader.map;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.entities.components.AiProfile;
import com.ado.trader.entities.components.Animation;
import com.ado.trader.entities.components.Area;
import com.ado.trader.entities.components.AttributeTable;
import com.ado.trader.entities.components.BaseId;
import com.ado.trader.entities.components.Inventory;
import com.ado.trader.entities.components.Money;
import com.ado.trader.entities.components.Movement;
import com.ado.trader.entities.components.Name;
import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.SpriteComp;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class EntityLayer extends IntMapLayer {
	ComponentMapper<BaseId> baseIdMap;
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
	World world;

	public EntityLayer(int w, int h, int c, World world) {
		super(w, h, c);
		this.world = world;
		
		baseIdMap = world.getMapper(BaseId.class);
		nameMap = world.getMapper(Name.class);
		spriteMap = world.getMapper(SpriteComp.class);
		animMap = world.getMapper(Animation.class);
		aiMap = world.getMapper(AiProfile.class);
		areaMap = world.getMapper(Area.class);
		inventoryMap = world.getMapper(Inventory.class);
		movementMap = world.getMapper(Movement.class);
		attributeMap = world.getMapper(AttributeTable.class);
		positionMap = world.getMapper(Position.class);
		moneyMap = world.getMapper(Money.class);
	}
	
	public boolean addToMap(Integer id, int x, int y) {
		for(int i = 0; i < map[x][y].length; i++){
			if(map[x][y][i] == null){
				map[x][y][i] = id;
				return true;
			}
		}
		return false;
	}
	
	public void deleteFromMap(int x, int y) {
		map[x][y] = null;
	}
	
	public void markAreaOccupied(int x, int y, Entity e, IntMapLayer layer){
		if (!areaMap.has(e)) return;
		
		Area a = areaMap.get(e);
		for(Vector2 vec: a.area){
			layer.addToMap(e.getId(), (int)(x+vec.x), (int)(y+vec.y));
		}
	}
	
	//finds neighbours of x,y to a depth of n with the desired tag.
	public Array<Integer> getNeighborEntitys(int x, int y, int n, String tag) {
		Array<Integer> neighbours = new Array<Integer>();
		GroupManager gm = world.getManager(GroupManager.class);
		
		for(int i = x-n; i<x+n+n; i++){
			for(int j = y-n; j<y+n+n; j++){
				if((i==x && j==y) || i < 0 || j < 0 || i >= map.length || j >= map[i].length){
					continue;
				}
				for(int c = 0; c < map[i][j].length; c++){
					if(map[i][j][c]!=null){
						Entity e = world.getEntity(map[i][j][c]);
						
						if(gm.isInGroup(e, tag)){
							neighbours.add(map[i][j][c]);
						}
					}
				}
			}
		}
		return neighbours;		  
	}
	
	public Entity getClosestEntity(int x, int y, int n, String tag){
		GroupManager gm = world.getManager(GroupManager.class);
		for(int d = 1; d <= n; d++){
			int i = x - d;
			int j = y + d;
			
			if(outOfMap(i, j)){
				continue;
			}

			for(int c = 0; c < map[i][j].length; c++){
				if(map[i][j][c]!=null){
					Entity e = world.getEntity(map[i][j][c]);
					if(gm.isInGroup(e, tag)){
						return e;
					}
				}
			}
			
			for(int q = 0; q < d*2; q++){
				i++;
				if(outOfMap(i, j)){
					continue;
				}
				for(int c = 0; c < map[i][j].length; c++){
					if(map[i][j][c]!=null){
						Entity e = world.getEntity(map[i][j][c]);
						if(gm.isInGroup(e, tag)){
							return e;
						}
					}
				}
			}
			for(int q = 0; q < d*2; q++){
				j--;
				if(outOfMap(i, j)){
					continue;
				}
				for(int c = 0; c < map[i][j].length; c++){
					if(map[i][j][c]!=null){
						Entity e = world.getEntity(map[i][j][c]);
						if(gm.isInGroup(e, tag)){
							return e;
						}
					}
				}
			}
			for(int q = 0; q < d*2; q++){
				i--;
				if(outOfMap(i, j)){
					continue;
				}
				for(int c = 0; c < map[i][j].length; c++){
					if(map[i][j][c]!=null){
						Entity e = world.getEntity(map[i][j][c]);
						if(gm.isInGroup(e, tag)){
							return e;
						}
					}
				}
			}
			for(int q = 0; q < d*2-1; q++){
				j++;
				if(outOfMap(i, j)){
					continue;
				}
				for(int c = 0; c < map[i][j].length; c++){
					if(map[i][j][c]!=null){
						Entity e = world.getEntity(map[i][j][c]);
						if(gm.isInGroup(e, tag)){
							return e;
						}
					}
				}
			}
		}
		return null;
	}
	private boolean outOfMap(int x, int y){
		if(x < 0 || x >= map.length ||
				y < 0 || y >= map[x].length){
			return true;
		}
		return false;
	}
	public int getWidth() {
		return map.length;
	}
	
	public int getHeight() {
		return map[0].length;
	}
	
	@Override
	public void saveLayer(Json chunkJson) {
		chunkJson.writeArrayStart("entities");
		
		for(int x=0; x<map.length; x++){
			for(int y=0; y<map[x].length; y++){
				for(int c = 0; c < map[x][y].length; c++){	

					if(map[x][y][c] == null){
						continue;
					}
					Entity e = world.getEntity(map[x][y][c]);

					chunkJson.writeObjectStart();
					
					chunkJson.writeValue("id", baseIdMap.get(e).getId());
					
					chunkJson.writeValue("n", nameMap.get(e).getName());

					Position p = positionMap.get(e);
					chunkJson.writeValue("p", p.getTileX() +","+ p.getTileY() +","+ c);
					chunkJson.writeValue("o", p.getIsoOffset().x +","+ p.getIsoOffset().y);
					
					if(areaMap.has(e)){
						Area a = areaMap.get(e);
						chunkJson.writeArrayStart("area");
						for(Vector2 vec:a.area){
							chunkJson.writeValue(vec.x +","+ vec.y);
						}
						chunkJson.writeArrayEnd();
					}

					if(spriteMap.has(e)){		//sprite
						SpriteComp sC = spriteMap.get(e);
						chunkJson.writeArrayStart("spr");
						chunkJson.writeValue(sC.mainSprite);

						if(sC.secondSprite != null){
							chunkJson.writeValue(sC.secondSprite);
						}

						chunkJson.writeArrayEnd();
					}

					if(animMap.has(e)){		//animation skin
						Animation a = animMap.get(e);
						chunkJson.writeArrayStart("anim");
						chunkJson.writeValue(a.getSkeleton().getSkin().getName());
						chunkJson.writeValue(a.getSkeleton().findSlot("head").getAttachment().getName());
						chunkJson.writeArrayEnd();
					}

					if(moneyMap.has(e)){		
						Money m = moneyMap.get(e);
						chunkJson.writeValue("m", m.value);
					}

					if(inventoryMap.has(e)){		
						Inventory i = world.getMapper(Inventory.class).get(e);
						chunkJson.writeArrayStart("inv");
						for(int itemId: i.getItems()){
							String item = nameMap.get(world.getEntity(itemId)).getName();
							chunkJson.writeValue(item);
						}
						chunkJson.writeArrayEnd();
					}

					chunkJson.writeObjectEnd();
				}
			}
		}
		chunkJson.writeArrayEnd();
	}
	
	public void loadLayer(JsonValue e){
		String[] xy;
		for(JsonValue v = e.child; v != null; v = v.next){
			xy = v.getString("p").split(",");
			
			Entity ent = EntityFactory.createEntity(v.getString("n"));
			
			//place on map
			map[Integer.valueOf(xy[0])][Integer.valueOf(xy[1])][Integer.valueOf(xy[2])] = ent.getId();
			
			//set entity's position
			Position p = positionMap.get(ent);
			p.setPosition(Integer.valueOf(xy[0]), Integer.valueOf(xy[1]));
			
			xy = v.getString("o").split(",");
			p.getIsoOffset().x = Float.valueOf(xy[0]);
			p.getIsoOffset().y = Float.valueOf(xy[1]);
			
			//load area
			if(v.has("area")){
				Area a = areaMap.get(ent);
				for(JsonValue vec = v.get("area").child; vec != null; vec = vec.next){
					xy = vec.asString().split(",");
					a.area.add(new Vector2(Float.valueOf(xy[0]), Float.valueOf(xy[1])));
				}
			}
			
			//load sprite(s)
			if(v.has("spr")){
				SpriteComp sC = spriteMap.get(ent);
				int[] sprIndex = v.get("spr").asIntArray();
				sC.mainSprite = sprIndex[0];
				if(sprIndex.length > 1){
					sC.secondSprite = sprIndex[1];
				}
			}
			
			//load inventory
			if(v.has("inv")){
				Inventory inv = world.getMapper(Inventory.class).get(ent);
				String[] contents = v.get("inv").asStringArray();
				for(String n: contents){
					Entity i = ItemFactory.createItem(n);
					inv.add(i.getId());
				}
			}
		}
	}
}
