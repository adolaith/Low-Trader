package com.ado.trader.rendering;

import com.ado.trader.input.InputHandler;
import com.ado.trader.map.Map;
import com.ado.trader.map.Tile;
import com.ado.trader.placement.PlacementManager;
import com.ado.trader.utils.IsoUtils;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.TimeUtils;

public class WorldRenderer{
	Map map;
	PlacementManager plManager;
	Stage stage;
	InputHandler input;
	
	SpriteBatch batch;
	OrthographicCamera cam;
	float width, height;
	ShapeRenderer sr;
	BitmapFont font;
	EntityRenderSystem renderEntity;
	Sprite highlight;

	public WorldRenderer(TextureAtlas atlas, Map map, World world, InputHandler input, Stage stage, PlacementManager plManager){
		width = 1280;	//1280		960		16:
		height = 720;	//720 		540		 9
		this.map = map;
		this.input = input;
		this.stage = stage;
		this.plManager = plManager;
		
		cam = new OrthographicCamera(width, height);
		highlight = atlas.createSprite("gui/highlightTile");
		highlight.setScale(2);
		
		batch = new SpriteBatch();
		batch.setProjectionMatrix(cam.combined);
		
		sr = new ShapeRenderer();
		font = new BitmapFont(Gdx.files.internal("font/white.fnt"), false);
		font.setScale(2f);
		
		MaskingSystem masks = new MaskingSystem(atlas, map, input);
		renderEntity = new EntityRenderSystem(map, world, masks);
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
		map.draw(batch);
		renderTileHighlight(batch);
		
		plManager.render(batch);
		
		//Render entities
		renderEntity.renderEntities(batch, cam);
		
		//shows debug overlay
		if(InputHandler.DEBUG){
			//highlights zones
			map.drawDebug(sr);
		}		
		
		//draws scene2d stage
		stage.draw();
		
		renderTime = TimeUtils.nanosToMillis(TimeUtils.timeSinceNanos(start));
	}
	public void renderTileHighlight(SpriteBatch batch){
		Vector2 mapPos = IsoUtils.getColRow((int)input.getMousePos().x, (int)input.getMousePos().y, map.getTileWidth(), map.getTileHeight());
		if(mapPos.x<0||mapPos.y<0||mapPos.x>=map.getWidthInTiles()||mapPos.y>=map.getHeightInTiles()){return;}
		
		Tile t = map.getTileLayer().map[(int)mapPos.x][(int)mapPos.y][map.currentLayer];
		mapPos = IsoUtils.getIsoXY((int)t.getX(), (int)t.getY(), map.getTileWidth(), map.getTileHeight());
		batch.begin();
		batch.draw(highlight, (int)mapPos.x, (int)mapPos.y,highlight.getWidth()*highlight.getScaleX(),highlight.getHeight()*highlight.getScaleY());	
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
