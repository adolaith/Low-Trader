package com.ado.trader.map;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.entities.components.Feature;
import com.ado.trader.entities.components.Mask;
import com.ado.trader.entities.components.Name;
import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.SpriteComp;
import com.ado.trader.entities.components.Wall;
import com.ado.trader.rendering.EntityRenderSystem.Direction;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class WallLayer implements Layer {
	ComponentMapper<Name> nameMap;
	ComponentMapper<Wall> wallMap;
	ComponentMapper<Mask> maskMap;
	ComponentMapper<Feature> featureMap;
	ComponentMapper<SpriteComp> spriteMap;
	ComponentMapper<Position> posMap;
	public Integer[][] map;
	World world;

	public WallLayer(int w, int h, World world) {
		map = new Integer[w][h];
		this.world = world;
		nameMap = world.getMapper(Name.class);
		wallMap = world.getMapper(Wall.class);
		maskMap = world.getMapper(Mask.class);
		featureMap = world.getMapper(Feature.class);
		spriteMap = world.getMapper(SpriteComp.class);
		posMap = world.getMapper(Position.class);
	}
	public void addToMap(Integer id, int x, int y) {
		map[x][y] = id;
	}
	public void deleteFromMap(int x, int y) {
		map[x][y] = null;
	}
	public int getWidth() {
		return map.length;
	}
	public int getHeight() {
		return map[0].length;
	}
	@Override
	public boolean isOccupied(int x, int y) {
		return map[x][y]!=null;
	}
	public void loadLayer(JsonValue walls, int rX, int rY, int cX, int cY){
		String[] xy;
		for(JsonValue w = walls.child; w != null; w = w.next){
			xy = w.getString("p").split(",");
			
			Entity e = EntityFactory.createEntity(w.getString("n"));
			
			Vector2 worldVec = Map.tileToWorld(Integer.valueOf(xy[0]), Integer.valueOf(xy[1]), cX, cY, rX, rY);
			posMap.get(e).setPosition((int)worldVec.x, (int)worldVec.y);
			
			addToMap(e.getId(), Integer.valueOf(xy[0]), Integer.valueOf(xy[1]));
			
			String[] dirs = w.get("w").asStringArray();
			
			wallMap.get(e).firstSprite = Direction.valueOf(dirs[0]);
			
			if(dirs.length > 1){
				wallMap.get(e).secondSprite = Direction.valueOf(dirs[1]);
			}
			
			int[] sprites = w.get("s").asIntArray();
			spriteMap.get(e).mainSprite = sprites[0];
			if(sprites.length > 1){
				spriteMap.get(e).secondSprite = sprites[1];	
			}
		}
	}
	@Override
	public void saveLayer(Json chunkJson) {
		chunkJson.writeArrayStart("walls");

		for(int x=0; x<map.length; x++){
			for(int y=0; y<map[x].length; y++){

				if(map[x][y] == null){
					continue;
				}
				Entity e = world.getEntity(map[x][y]);

				chunkJson.writeObjectStart();
				
				chunkJson.writeValue("p", x +","+ y); 
				
				chunkJson.writeValue("n", nameMap.get(e).getName());

				Wall w = wallMap.get(e);
				chunkJson.writeArrayStart("w");
				chunkJson.writeValue(w.firstSprite.name());
				if(w.secondSprite != null){
					chunkJson.writeValue(w.secondSprite.name());
				}
				chunkJson.writeArrayEnd();
				
				SpriteComp sc = spriteMap.get(e);
				chunkJson.writeArrayStart("s");
				chunkJson.writeValue(sc.mainSprite);
				if(sc.secondSprite != null){
					chunkJson.writeValue(sc.secondSprite);
				}
				chunkJson.writeArrayEnd();

				if(maskMap.has(e)){
					Mask m = maskMap.get(e);
					chunkJson.writeArrayStart("m");
					chunkJson.writeValue(m.maskName);
					chunkJson.writeValue(m.maskIndex);
					chunkJson.writeArrayEnd();
				}

				if(featureMap.has(e)){
					Feature f = featureMap.get(e);
					chunkJson.writeArrayStart("f");
					chunkJson.writeValue(f.featureName);
					chunkJson.writeValue(f.spriteIndex);
					chunkJson.writeArrayEnd();
				}
				
				chunkJson.writeObjectEnd();
			}
		}
		chunkJson.writeArrayEnd();
	}
}
