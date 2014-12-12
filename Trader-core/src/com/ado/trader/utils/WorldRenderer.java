package com.ado.trader.utils;

import com.ado.trader.map.Tile;
import com.ado.trader.screens.GameScreen;
import com.ado.trader.systems.EntityRenderSystem;
import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

public class WorldRenderer{
	GameScreen game;
	SpriteBatch batch;
	OrthographicCamera cam;
	float width, height;
	ShapeRenderer sr;
	BitmapFont font;
	Entity entity;
	EntityRenderSystem renderEntity;
	Sprite highlight;

	public WorldRenderer(GameScreen game){
		this.game = game;
		width = 1280;	//1280		960		16:
		height = 720;	//720 		540		 9
		
		cam = new OrthographicCamera(width, height);
//		cam.zoom = .8f;
		highlight = game.getAtlas().createSprite("gui/highlightTile");
		highlight.setScale(2);
		
		batch = new SpriteBatch();
		batch.setProjectionMatrix(cam.combined);
		
		sr = new ShapeRenderer();
		font = new BitmapFont(Gdx.files.internal("font/white.fnt"), false);
		font.setScale(2f);
		renderEntity = new EntityRenderSystem(game);
	}
	
	public long renderTime;
	public void render(float delta){
		long start = TimeUtils.nanoTime();
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		cam.update();
		
		//draws the world
		sr.setProjectionMatrix(cam.combined);
		batch.setProjectionMatrix(cam.combined);
		
		//draws tile backgrounds
		game.getMap().draw(batch);
		renderTileHighlight(batch);
		
		game.getPlaceManager().render(game, batch);
		
		//Render entities
		renderEntity.renderEntities(batch);
		
		//shows debug overlay
		if(InputHandler.DEBUG){
			//highlights zones
			game.getMap().getCurrentLayerGroup().zoneLayer.renderAllZones(batch);
			game.getMap().drawDebug(sr);
		}		
		
		//draws scene2d stage
		game.getGui().getStage().draw();
		
		renderTime = TimeUtils.nanosToMillis(TimeUtils.timeSinceNanos(start));
	}
	public void renderTileHighlight(SpriteBatch batch){
		Vector2 map = IsoUtils.getColRow((int)game.getInput().getMousePos().x, (int)game.getInput().getMousePos().y, game.getMap().getTileWidth(), game.getMap().getTileHeight());
		if(map.x<0||map.y<0||map.x>=game.getMap().getWidthInTiles()||map.y>=game.getMap().getHeightInTiles()){return;}
		
		Tile t = game.getMap().getCurrentLayerGroup().tileLayer.map[(int)map.x][(int)map.y];
		map = IsoUtils.getIsoXY((int)t.getX(), (int)t.getY(), game.getMap().getTileWidth(), game.getMap().getTileHeight());
		batch.begin();
		batch.draw(highlight, (int)map.x, (int)map.y,highlight.getWidth()*highlight.getScaleX(),highlight.getHeight()*highlight.getScaleY());	
		batch.end();
	}
	public OrthographicCamera getCamera() {
		return cam;
	}
	public BitmapFont getFont() {
		return font;
	}
	public SpriteBatch getBatch() {
		return batch;
	}
	public EntityRenderSystem getRenderEntitySystem() {
		return renderEntity;
	}
	public void dispose(){
		batch.dispose();
		sr.dispose();
		font.dispose();
	}
}
