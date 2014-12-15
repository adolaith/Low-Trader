package com.ado.trader.systems;

import com.ado.trader.entities.components.Animation;
import com.ado.trader.entities.components.Area;
import com.ado.trader.entities.components.Feature;
import com.ado.trader.entities.components.Mask;
import com.ado.trader.entities.components.Position;
import com.ado.trader.entities.components.SpriteComp;
import com.ado.trader.entities.components.Status;
import com.ado.trader.entities.components.Wall;
import com.ado.trader.items.Item;
import com.ado.trader.items.ItemSprite;
import com.ado.trader.screens.GameScreen;
import com.ado.trader.systems.StatusIconSystem.StatusIcon;
import com.ado.trader.utils.IsoUtils;
import com.ado.trader.utils.MaskingSystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonBounds;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.SkeletonRendererDebug;

//Handles rendering of all active Entities. 
//Renders next entity selected for placement at the mouses location.

public class EntityRenderSystem{
	ComponentMapper<Position> pm;
	ComponentMapper<SpriteComp> sm;
	ComponentMapper<Wall> wm;
	ComponentMapper<Mask> maskm;
	ComponentMapper<Animation> animMapper;
	
	GameScreen game;
	World world;
	SkeletonRenderer skeletonRenderer;
	SkeletonRendererDebug debugRenderer;
	ArrayMap<Integer, Sprite> staticSprites;
	MaskingSystem masks;

	public EntityRenderSystem(GameScreen game) {
		this.game = game;
		this.world = game.getWorld();
		skeletonRenderer = new SkeletonRenderer();
		debugRenderer = new SkeletonRendererDebug();
		skeletonRenderer.setPremultipliedAlpha(true);
		sm = world.getMapper(SpriteComp.class);
		wm = world.getMapper(Wall.class);
		pm = world.getMapper(Position.class);
		animMapper = world.getMapper(Animation.class);
		maskm = world.getMapper(Mask.class);
		
		masks = new MaskingSystem(game);
	}
	int x, y;
	SkeletonBounds bounds = new SkeletonBounds();
	//renders entities from the rear of the map to front, avoiding sprite overlap caused by isometric view
	public void renderEntities(SpriteBatch batch){
		debugRenderer.getShapeRenderer().setProjectionMatrix(game.getRenderer().getCamera().combined);
		
		int sum = game.getMap().getWidthInTiles()+game.getMap().getHeightInTiles()-2;
		if(batch.isDrawing()){
			batch.end();
		}
		batch.begin();
		for(int count=sum; count>=0;count--){		//DEPTH COUNTER
			for(y=game.getMap().getHeightInTiles()-1;y>=0;y--){
				for(x=game.getMap().getWidthInTiles()-1;x>=0;x--){		//DIAGONAL MAP READ
					if(x+y-count==0){
						if(drawWideEntity(batch, game))continue;
						
						renderNorthernWall(x, y, batch);
						
						if(game.getMap().getCurrentLayerGroup().itemLayer.isOccupied(x, y)){
							Item i = game.getMap().getCurrentLayerGroup().itemLayer.map[x][y];
							Vector2 itemPos = IsoUtils.getIsoXY(x, y, game.getMap().getTileWidth(), game.getMap().getTileHeight());
							itemPos.add(game.getMap().getTileWidth()/2, game.getMap().getTileHeight()/3);
							ItemSprite item = i.getData(ItemSprite.class);
							batch.draw(item.sprite, itemPos.x-item.sprite.getWidth(), itemPos.y, 
									item.sprite.getWidth()*item.sprite.getScaleX(),item.sprite.getHeight()*item.sprite.getScaleY());
						}
						
						if(game.getMap().getCurrentLayerGroup().entityLayer.map[x][y]!=null){
							Entity e = world.getEntity(game.getMap().getCurrentLayerGroup().entityLayer.map[x][y]);

							if(sm.has(e)){		//RENDER STATIC ENTITY
								drawSprite(e,batch);
							}else if(animMapper.has(e)){		//RENDER ANIMATED ENTITY
								Skeleton skel = animMapper.get(e).getSkeleton();
								skeletonRenderer.draw(batch,skel);
								
								//render status icons
								ComponentMapper<Status> statusMapper = world.getMapper(Status.class);
								if(statusMapper.has(e)){
									StatusIcon icon = statusMapper.get(e).getStatusIcon();
									Vector2 iconPos = new Vector2(skel.getX(), skel.getY());
									bounds.update(skel, true);
									iconPos.y += bounds.getHeight() + 4;
									icon.drawIcon(batch, iconPos);
								}
							}
						}
						renderSouthernWall(x, y, batch);
					}
				}
			}
		}
		batch.end();
	}
	private boolean drawWideEntity(SpriteBatch batch, GameScreen game){
		if(game.getMap().getCurrentLayerGroup().entityLayer.map[x][y]!=null){
			Entity e = world.getEntity(game.getMap().getCurrentLayerGroup().entityLayer.map[x][y]);
			ComponentMapper<Area> areaM = game.getWorld().getMapper(Area.class);
			Position p = pm.get(e);
			if(areaM.has(e)){
				if(x==p.getX()&&y==p.getY()){
					Area a = areaM.get(e);
					SpriteComp s = sm.get(e);
					Sprite tmp = s.mainSprite;
					if(tmp.isFlipX()){		//render next entity
						if(game.getMap().getCurrentLayerGroup().entityLayer.isOccupied(x+1, y-1)){
							Entity next = world.getEntity(game.getMap().getCurrentLayerGroup().entityLayer.map[x+1][y-1]);
							drawSprite(next, batch);
							this.x--;
							this.y--;
						}
					}
					renderNorthernWall(p.getX(), p.getY(), batch);
					for(Vector2 vec:a.area){			//north walls
						int aX = (int)(vec.x+p.getX());
						int aY = (int)(vec.y+p.getY());
						renderNorthernWall(aX, aY, batch);
					}
					if(tmp.isFlipX()){		//render next entity
						batch.draw(tmp , (int)p.getIsoPosition().x-4, (int)p.getIsoPosition().y-32,
								tmp.getWidth()*tmp.getScaleX(),tmp.getHeight()*tmp.getScaleY());
					}else{
						batch.draw(tmp , (int)p.getIsoPosition().x-68, (int)p.getIsoPosition().y-32,
								tmp.getWidth()*tmp.getScaleX(),tmp.getHeight()*tmp.getScaleY());
					}
					renderSouthernWall(p.getX(), p.getY(), batch);
					for(Vector2 vec:a.area){			//south walls
						int aX = (int)(vec.x+p.getX());
						int aY = (int)(vec.y+p.getY());

						renderSouthernWall(aX, aY, batch);
					}
				}
				return true;
			}
		}
		return false;
	}
	private void drawFeature(Entity e,float x, float y, SpriteBatch batch){
		if(!game.getWorld().getMapper(Feature.class).has(e))return;
		Feature f = game.getWorld().getMapper(Feature.class).get(e);
		batch.draw(f.sprite, x, y, f.sprite.getWidth()*f.sprite.getScaleX(), f.sprite.getHeight()*f.sprite.getScaleY());
	}

	private void drawNorthSprite(Direction dir, Sprite s, Position p, Mask m, SpriteBatch batch){
		if(dir==null)return;
		Vector2 vec = null;
		switch(dir){
		case NE:
			vec = new Vector2((int)p.getIsoPosition().x+(30*s.getScaleX()), (int)p.getIsoPosition().y+(16*s.getScaleY()));
			masks.drawMask(batch, "wallMask_sw", vec, s.getHeight(), p, m, game);
			batch.draw(s , vec.x, vec.y,s.getWidth()*s.getScaleX(),s.getHeight()*s.getScaleY());
			break;
		case NW:
			vec = new Vector2((int)p.getIsoPosition().x-4, (int)p.getIsoPosition().y+(16*s.getScaleY()));
			masks.drawMask(batch, "wallMask_se", vec, s.getHeight(), p, m, game);
			batch.draw(s , vec.x, vec.y,s.getWidth()*s.getScaleX(),s.getHeight()*s.getScaleY());
			break;
		}
		batch.flush();
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
	private void renderNorthernWall(int x, int y, SpriteBatch batch){
		if(!game.getMap().getCurrentLayerGroup().wallLayer.isOccupied(x, y)) return;
		Entity e = world.getEntity(game.getMap().getCurrentLayerGroup().wallLayer.map[x][y]);
		Wall w = wm.get(e);
		if(!(w.firstSprite==Direction.NE||w.firstSprite==Direction.NW)&&!
				(w.secondSprite==Direction.NE||w.secondSprite==Direction.NW))return;
		SpriteComp sC = sm.get(e);
		Position p = pm.get(e);
		Mask m = null;
		if(maskm.has(e)){
			m = maskm.get(e);
		}
		drawNorthSprite(w.firstSprite, sC.mainSprite, p, m, batch);
		drawNorthSprite(w.secondSprite, sC.secondarySprite, p, m, batch);
		drawFeature(e, p.getIsoPosition().x, p.getIsoPosition().y, batch);
	}
	private void renderSouthernWall(int x, int y, SpriteBatch batch){
		if(!game.getMap().getCurrentLayerGroup().wallLayer.isOccupied(x, y)) return;
		Entity e = world.getEntity(game.getMap().getCurrentLayerGroup().wallLayer.map[x][y]);
		Wall w = wm.get(e);
		if(!(w.firstSprite==Direction.SE||w.firstSprite==Direction.SW)&&!
				(w.secondSprite==Direction.SE||w.secondSprite==Direction.SW))return;
		SpriteComp sC = sm.get(e);
		Position p = pm.get(e);
		Mask m = null;
		if(maskm.has(e)){
			m = maskm.get(e);
		}
		drawSouthSprite(w.firstSprite, sC.mainSprite, p, m, batch);
		drawSouthSprite(w.secondSprite, sC.secondarySprite, p, m, batch);
		drawFeature(e, p.getIsoPosition().x, p.getIsoPosition().y, batch);
	}
	private void drawSouthSprite(Direction dir, Sprite s, Position p, Mask m, SpriteBatch batch){
		if(dir==null)return;
		Vector2 vec = null;
		switch(dir){
		case SE:
			vec = new Vector2((int)p.getIsoPosition().x+(28*s.getScaleX()), (int)p.getIsoPosition().y);
			masks.drawMask(batch, "wallMask_se", vec, s.getHeight(), p, m, game);
			batch.draw(s , vec.x, vec.y,s.getWidth()*s.getScaleX(),s.getHeight()*s.getScaleY());
			break;
		case SW:
			vec = new Vector2((int)p.getIsoPosition().x, (int)p.getIsoPosition().y);
			masks.drawMask(batch, "wallMask_sw", vec, s.getHeight(), p, m, game);
			batch.draw(s , vec.x, vec.y,s.getWidth()*s.getScaleX(),s.getHeight()*s.getScaleY());
			break;
		}
		batch.flush();
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
	private void drawSprite(Entity e, SpriteBatch batch){
		//draws entities
		Position p = pm.get(e);
		SpriteComp s = sm.get(e);
		Sprite tmp = s.mainSprite;
		batch.draw(tmp , (int)p.getIsoPosition().x, (int)p.getIsoPosition().y,tmp.getWidth()*tmp.getScaleX(),tmp.getHeight()*tmp.getScaleY());		//static entities
	}
	public ArrayMap<Integer, Sprite> getStaticSprites() {
		return staticSprites;
	}
	public SkeletonRenderer getSkeletonRenderer() {
		return skeletonRenderer;
	}
	public SkeletonRendererDebug getDebugRenderer() {
		return debugRenderer;
	}
	public void loadSprites(ArrayMap<Integer, Sprite> entitySprites){
		staticSprites = entitySprites;
	}
	public MaskingSystem getMasks() {
		return masks;
	}
	public enum Direction{
		NW,NE,SW,SE,NORTH,SOUTH
	}
}
