package com.ado.trader.placement;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.entities.components.Position;
import com.ado.trader.input.InputHandler;
import com.ado.trader.map.Chunk;
import com.ado.trader.map.Map;
import com.ado.trader.utils.FileLogger;
import com.ado.trader.utils.GameServices;
import com.ado.trader.utils.IsoUtils;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;

public class NpcPlaceable extends Placeable {
	String baseId;
	
	JsonValue profile;
	
	ComponentMapper<Position> positionMapper;

	public NpcPlaceable(GameServices gameRes) {
		super(gameRes.getMap(), gameRes.getRenderer().getRenderEntitySystem());

		positionMapper = map.getWorld().getMapper(Position.class);
	}

	@Override
	public void place(int mapX, int mapY) {
		Entity e = EntityFactory.createEntity(baseId);
		
		Chunk c = map.getChunk(mapX, mapY);
		Vector2 tile = Map.worldVecToTile(mapX, mapY);
		
		positionMapper.get(e).setPosition((int) tile.x, (int) tile.y);

		c.getEntities().addToMap(e.getId(), (int) tile.x, (int) tile.y);
	}

	@Override
	public void rotateSelection() {

	}

	@Override
	public void renderPreview(SpriteBatch batch) {

	}
	
	public void setSelection(String baseid){
		this.baseId = baseid;
		
		String[] idSplit = baseid.split("\\.");
		
		profile = EntityFactory.getEntityData().get(idSplit[0]).get(idSplit[1]);
	}

	@Override
	public void clearSettings() {
		baseId = null;
		profile = null;
	}
	
	@Override
	public void dragPlace(Vector2 start, Vector2 widthHeight) {}

}
