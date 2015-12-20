package com.ado.trader.systems;

import com.ado.trader.entities.components.Animation;
import com.ado.trader.utils.GameServices;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.brashmonkey.spriter.Data;
import com.brashmonkey.spriter.LibGdxAtlasLoader;
import com.brashmonkey.spriter.LibGdxDrawer;
import com.brashmonkey.spriter.Player;
import com.brashmonkey.spriter.SCMLReader;

public class AnimationSystem extends EntityProcessingSystem {
	
	LibGdxAtlasLoader atlasLoader;
	Data animationData;
	
	LibGdxDrawer drawer;

	public AnimationSystem() {
		super(Aspect.all(Animation.class));
		
		String scmlPath = "data/anim/";
		FileHandle scmlFile = Gdx.files.internal(scmlPath + "animations.scml");
		
		SCMLReader reader = new SCMLReader(scmlFile.read());
		animationData = reader.getData();
		
		atlasLoader = new LibGdxAtlasLoader(animationData, Gdx.files.internal(scmlPath + "anims.atlas"));
		atlasLoader.load(scmlFile.file());
	}
	
	@Override
	protected void process(Entity e) {
		ComponentMapper<Animation> animMapper = GameServices.getWorld().getMapper(Animation.class);
		
		if(animMapper.has(e)){
			Player p = animMapper.get(e).getPlayer();
			p.update();
//			animMapper.get(e).setPosition(world.getMapper(Position.class).get(e).getIsoOffset());
		}
	}
	public Player createPlayer(String name){
		return new Player(animationData.getEntity(name));
	}
	public void loadDrawer(SpriteBatch batch, ShapeRenderer renderer){
		try{
			drawer = new LibGdxDrawer(atlasLoader, batch, renderer);
		}catch(Exception ex){
			System.out.println("AnimationSys: Error loading LibGdxDrawer");
			ex.printStackTrace();
		}
	}
	public LibGdxDrawer getDrawer(){
		return drawer;
	}
	public LibGdxAtlasLoader getAtlasLoader() {
		return atlasLoader;
	}
	public Data getAnimationData() {
		return animationData;
	}
}
