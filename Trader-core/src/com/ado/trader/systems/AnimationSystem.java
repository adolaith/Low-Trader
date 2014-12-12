package com.ado.trader.systems;

import com.ado.trader.entities.components.Animation;
import com.ado.trader.entities.components.Position;
import com.ado.trader.screens.GameScreen;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.utils.ArrayMap;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.SkeletonData;

@Wire
public class AnimationSystem extends EntityProcessingSystem {
	ComponentMapper<Animation> animMapper;
	
	GameScreen game;
	ArrayMap<String, AnimationStateData> animationPool;

	@SuppressWarnings("unchecked")
	public AnimationSystem(GameScreen game) {
		super(Aspect.getAspectForAll(Animation.class));
		this.game = game;
		animationPool = new ArrayMap<String, AnimationStateData>();
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
	public void loadAnimation(SkeletonData skelData){
		animationPool.put(skelData.getName(), new AnimationStateData(skelData));
	}
	public ArrayMap<String, AnimationStateData> getAnimationPool() {
		return animationPool;
	}
}
