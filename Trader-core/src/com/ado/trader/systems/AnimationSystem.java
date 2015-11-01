package com.ado.trader.systems;

import com.ado.trader.entities.components.Animation;
import com.ado.trader.entities.components.Position;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;

@Wire
public class AnimationSystem extends EntityProcessingSystem {
	ComponentMapper<Animation> animMapper;

	@SuppressWarnings("unchecked")
	public AnimationSystem() {
		super(Aspect.getAspectForAll(Animation.class));
	}

	@Override
	protected void process(Entity e) {
		if(animMapper.has(e)){
			animMapper.get(e).setPosition(world.getMapper(Position.class).get(e).getIsoPosition());
			animMapper.get(e).getMainState().update(world.getDelta());
			animMapper.get(e).getMainState().apply(animMapper.get(e).getSkeleton());
			animMapper.get(e).getSkeleton().update(world.getDelta());
			animMapper.get(e).getSkeleton().updateWorldTransform();
		}
	}
}
