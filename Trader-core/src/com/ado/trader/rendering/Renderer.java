package com.ado.trader.rendering;

import com.ado.trader.GameMain;
import com.ado.trader.input.InputHandler;
import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.TimeUtils;

public class Renderer{
	GameServices gameRes;
	
	SpriteBatch batch;
	OrthographicCamera cam;
	float width, height;
	ShapeRenderer sr;
	EntityRenderSystem renderEntity;
	

	public Renderer(GameServices gameRes){
		width = 1280;	//1280		960		16:
		height = 720;	//720 		540		 9
		this.gameRes = gameRes;
		
		cam = gameRes.getCam();
		
		batch = new SpriteBatch(1000, GameMain.createSpriteShader());
		batch.setProjectionMatrix(cam.combined);
		
		sr = new ShapeRenderer();
		
		MaskingSystem masks = new MaskingSystem(gameRes.getAtlas(), gameRes.getMap());
		renderEntity = new EntityRenderSystem(gameRes.getAtlas(), gameRes.getMap(), masks);
	}
	
	public static long renderTime;
	public void render(float delta){
		long start = TimeUtils.nanoTime();
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		//draws the world
		sr.setProjectionMatrix(cam.combined);
		batch.setProjectionMatrix(cam.combined);
		
		//draws tile backgrounds
		gameRes.getMap().draw(batch);
		
		gameRes.getInput().render(batch);
		
		//Render entities
		renderEntity.renderEntities(batch, cam);
		
		//shows debug overlay
		if(InputHandler.DEBUG){
			//highlights zones
			gameRes.getMap().drawDebug(batch);
		}		
		//draws scene2d stage
		if(batch.isDrawing()){
			batch.end();
		}
		
		renderTime = TimeUtils.nanosToMillis(TimeUtils.timeSinceNanos(start));
	}

	public OrthographicCamera getCamera() {
		return cam;
	}
	public SpriteBatch getBatch() {
		return batch;
	}
	public EntityRenderSystem getRenderEntitySystem() {
		return renderEntity;
	}
	public ShapeRenderer getShapeRenderer(){
		return sr;
	}
	public void dispose(){
		batch.dispose();
		sr.dispose();
	}
}
