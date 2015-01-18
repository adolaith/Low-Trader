package com.ado.trader.systems;

import com.ado.trader.entities.components.Animation;
import com.ado.trader.entities.components.Health;
import com.ado.trader.entities.components.Movement;
import com.ado.trader.entities.components.Position;
import com.ado.trader.gui.GameServices;
import com.ado.trader.map.Map;
import com.ado.trader.map.Tile;
import com.ado.trader.pathfinding.Path.Step;
import com.ado.trader.utils.IsoUtils;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

//If entities with the movement component are given a path, this steadily moves them and cycles through the steps in the path.
@Wire
public class MovementSystem  extends EntityProcessingSystem {
	ComponentMapper<Position> pm;
	ComponentMapper<Movement> mm;
	ComponentMapper<Animation> am;

	Map map;

	@SuppressWarnings("unchecked")
	public MovementSystem(GameServices gameRes) {
		super(Aspect.getAspectForAll(Movement.class));
		map = gameRes.getMap();
	}

	@Override
	protected void process(Entity e) {
		if(mm.has(e)){
			if(mm.get(e).getPath()!=null){
				Position p = pm.get(e);
				Movement m = mm.get(e);
				Vector2 tmp = new Vector2();
				Vector2 currentStep = IsoUtils.getIsoXY((int)m.getPath().getX(m.getStep()), (int)m.getPath().getY(m.getStep()), map.getTileWidth(), map.getTileHeight());
				
				//move entity +x
				if(currentStep.x>p.getIsoPosition().x){
					tmp.x = (float) (p.getIsoPosition().x + m.getVelocity() * Math.ceil(Gdx.graphics.getRawDeltaTime() * 2) / 2);
					if(tmp.x > currentStep.x) {tmp.x = currentStep.x; }
					p.getIsoPosition().x = tmp.x;
				//move entity -x
				}else if(currentStep.x < p.getIsoPosition().x){
					tmp.x = (float) (p.getIsoPosition().x - m.getVelocity() * Math.ceil(Gdx.graphics.getRawDeltaTime() * 2) / 2);
					if(tmp.x < currentStep.x) {tmp.x = currentStep.x; }
					p.getIsoPosition().x = tmp.x;
				}
				//move entity +y
				if(currentStep.y > p.getIsoPosition().y){
					tmp.y = (float) (p.getIsoPosition().y + (m.getVelocity() / 2f) * Math.ceil(Gdx.graphics.getRawDeltaTime() * 2) / 2);
					if(tmp.y > currentStep.y) {tmp.y = currentStep.y; }
					p.getIsoPosition().y = tmp.y;
				//move entity -y
				}else if(currentStep.y < p.getIsoPosition().y){
					tmp.y = (float) (p.getIsoPosition().y - (m.getVelocity() / 2f) * Math.ceil(Gdx.graphics.getRawDeltaTime() * 2) / 2);
					if(tmp.y < currentStep.y) {tmp.y = currentStep.y; }
					p.getIsoPosition().y = tmp.y;
				}

				Vector2 newTile = IsoUtils.getColRow((int)p.getIsoPosition().x, (int)p.getIsoPosition().y, map.getTileWidth(), map.getTileHeight());

				//next step in the path
				if(tmp.x==0&&tmp.y==0){
					//moves entity in collision grid
					map.getEntityLayer().deleteFromMap(p.getX(), p.getY(), p.getHeightLayer());
					map.getEntityLayer().addToMap(e.getId(), (int)(newTile.x + 1),(int)newTile.y, map.currentLayer);		
					p.setPosition((int)m.getPath().getX(m.getStep()), (int)m.getPath().getY(m.getStep()), map.currentLayer);
					
					//check for damage from tile
					Tile t = map.getTileLayer().map[(int)(newTile.x+1)][(int)newTile.y][map.currentLayer];
					if(t.dmg > 0){
						StatusIconSystem iconSys = world.getSystem(StatusIconSystem.class);
						iconSys.newIconAnimation("iconImportant", e);

						Health hp = e.getComponent(Health.class);
						hp.value -= t.dmg;
					}
					
					//next step
					m.setStep(m.getStep()+1);
					
					//ends path and animation
					if(m.getStep()==m.getPath().getLength()){
						m.setPath(null);
						am.get(e).resetAnimation();
						return;
					}
					findDirection(am.get(e), p, m);
				}
			}
		}
	}
	public void findDirection(Animation a,Position p,  Movement m){
		int srcX,srcY;
		if(m.getStep()==0){
			srcX = p.getX();
			srcY = p.getY();
		}else{
			srcX = (int)m.getPath().getX(m.getStep()-1);
			srcY = (int)m.getPath().getY(m.getStep()-1);
		}
		Step current = m.getPath().getStep(m.getStep());
		int directionX = (int)(current.getX()-srcX);
		int directionY = (int)(current.getY()-srcY);
		
		String skeleName = a.getSkeleton().getData().getName();
		
		if(skeleName.matches("human")){
			//humans, nw,ne,sw,se animations
			if(m.getDirection().x!=directionX || m.getDirection().y!=directionY){
				String headName = a.getSkeleton().findSlot("head").getAttachment().getName();
				String bodyName = a.getSkeleton().getSkin().getName().substring(1, a.getSkeleton().getSkin().getName().indexOf("_"));
				switch(directionX){
				case 1:
					a.getSkeleton().setSkin("m"+bodyName+"_Rear");
					a.getSkeleton().setAttachment("head", "human/guyR_head"+Integer.valueOf(headName.substring(headName.length()-1)));
					a.getSkeleton().setFlipX(false);
					a.getMainState().setAnimation(0, "walkRear", true);
					break;
				case -1:
					a.getSkeleton().setSkin("m"+bodyName+"_Front");
					a.getSkeleton().setAttachment("head", "human/guyF_head"+Integer.valueOf(headName.substring(headName.length()-1)));
					a.getSkeleton().setFlipX(false);
					a.getMainState().setAnimation(0, "walkFront", true);
					break;
				}
				switch(directionY){
				case 1:
					a.getSkeleton().setSkin("m"+bodyName+"_Rear");
					a.getSkeleton().setAttachment("head", "human/guyR_head"+Integer.valueOf(headName.substring(headName.length()-1)));
					a.getSkeleton().setFlipX(true);
					a.getMainState().setAnimation(0, "walkRear", true);
					break;
				case -1:
					a.getSkeleton().setSkin("m"+bodyName+"_Front");
					a.getSkeleton().setAttachment("head", "human/guyF_head"+Integer.valueOf(headName.substring(headName.length()-1)));
					a.getSkeleton().setFlipX(true);
					a.getMainState().setAnimation(0, "walkFront", true);
					break;
				}
				m.setDirection(new Vector2(directionX, directionY));
			}
		}else{
			//non human, left-right animations
			if(m.getDirection().x!=directionX || m.getDirection().y!=directionY){
				switch(directionX){
				case 1:
					a.getSkeleton().setFlipX(true);
					a.getMainState().setAnimation(0, "walk", true);
					break;
				case -1:
					a.getSkeleton().setFlipX(false);
					a.getMainState().setAnimation(0, "walk", true);
					break;
				}
				switch(directionY){
				case 1:
					a.getSkeleton().setFlipX(false);
					a.getMainState().setAnimation(0, "walk", true);
					break;
				case -1:
					a.getSkeleton().setFlipX(true);
					a.getMainState().setAnimation(0, "walk", true);
					break;
				}
				m.setDirection(new Vector2(directionX, directionY));
			}
		}
	}
}