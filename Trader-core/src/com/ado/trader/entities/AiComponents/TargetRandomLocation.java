package com.ado.trader.entities.AiComponents;

import java.util.Random;

import com.ado.trader.entities.AiComponents.base.LeafTask;
import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.Target;
import com.ado.trader.map.EntityLayer;
import com.ado.trader.systems.AiSystem;

public class TargetRandomLocation extends LeafTask {
	int searchRadius, width, height;
	Random random;

	public TargetRandomLocation(AiSystem aiSys, String searchRadius) {
		super(aiSys);
		this.searchRadius = Integer.valueOf(searchRadius);
		random = new Random();
		width = aiSys.game.getMap().getWidthInTiles();
		height = aiSys.game.getMap().getHeightInTiles();
	}

	public TargetRandomLocation(AiSystem aiSys, String name, String radius) {
		super(aiSys, name);
		this.searchRadius = Integer.valueOf(searchRadius);
		random = new Random();
	}

	@Override
	public void end() {

	}

	@Override
	public void doTask() {
		Position pos = aiSys.getPm().get(aiSys.currentEntity);
		EntityLayer elayer = aiSys.game.getMap().getCurrentLayerGroup().entityLayer;
		
		int minX = pos.getX() - searchRadius;
		int minY = pos.getY() - searchRadius;
		int maxX = pos.getX() + searchRadius;
		int maxY = pos.getY() + searchRadius;

		for(int i = 0; i < 5; i++){
			int x = random.nextInt(maxX - minX + 1) + minX;
			int y = random.nextInt(maxY - minY + 1) + minY;
			
			if(x < 0 || x >= width ||
					y < 0 || y >= height){
				continue;
			}
			
			if(!elayer.isOccupied(x, y)){
				Target t = aiSys.currentEntity.getComponent(Target.class);
				t.setTargetVector(x, y);

				control.finishWithSuccess();
				return;
			}
		}
		control.finishWithFailure();
	}

	@Override
	public boolean checkConditions() {
		return true;
	}

}
