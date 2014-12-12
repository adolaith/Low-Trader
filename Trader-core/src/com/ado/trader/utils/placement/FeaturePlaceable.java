package com.ado.trader.utils.placement;

import com.ado.trader.screens.GameScreen;
import com.ado.trader.utils.IsoUtils;
import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class FeaturePlaceable extends Placeable {
	public String featureId;
	public int spriteId;
	Sprite sprite;

	protected FeaturePlaceable(GameScreen game) {
		super(game);
	}
	
	@Override
	void place(int x, int y) {
		switch(game.getEntities().getFeatures().getFeature(featureId).get("deco")){
		case "wall":
			if(!game.getMap().getCurrentLayerGroup().wallLayer.isOccupied(x, y)){
				game.getGui().getNewsWindow().newMessage("Invalid placement. Wall decoration only");
				return;
			}
			Entity w = game.getWorld().getEntity(game.getMap().getCurrentLayerGroup().wallLayer.map[x][y]);
			game.getEntities().getFeatures().applyFeature(w, featureId, spriteId, new Sprite(sprite));
			break;
		}
		if(sprite.isFlipX()){
			sprite.flip(true, false);
		}
	}

	@Override
	void remove(int x, int y) {
		if(!delete){return;}
		if(game.getMap().getCurrentLayerGroup().entityLayer.isOccupied(x, y)){
			game.getEntities().deleteEntity(x,y, game.getMap().getCurrentLayerGroup().entityLayer);
		}
		if(!Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)){
			delete^=delete;
		}
	}
	public void rotateSelection(){
		String[] tmp = game.getEntities().getFeatures().getFeature(featureId).get("sprite").split(",");
		if(tmp.length==1){
			sprite.flip(true, false);
			return;
		}
		if(sprite.isFlipX()){
			sprite.flip(true, false);
			for(String s:tmp){
				int i = Integer.valueOf(s);
				if(i==spriteId)continue;
				spriteId = i;
				sprite = game.getRenderer().getRenderEntitySystem().getStaticSprites().get(i);
				break;
			}
		}else{
			sprite.flip(true, false);
		}
	}
	public void renderPreview(SpriteBatch batch){
		if(delete)return;
		Vector2 mousePos = IsoUtils.getColRow((int)game.getInput().getMousePos().x, (int)game.getInput().getMousePos().y, 
				game.getMap().getTileWidth(), game.getMap().getTileHeight());
		mousePos = IsoUtils.getIsoXY((int)mousePos.x, (int)mousePos.y, 
				game.getMap().getTileWidth(), game.getMap().getTileHeight());
		batch.begin();
		
		batch.draw(sprite, mousePos.x, mousePos.y, 
				sprite.getWidth()*sprite.getScaleX(), sprite.getHeight()*sprite.getScaleY());
		batch.end();
	}
	@Override
	void dragPlace(Vector2 start, Vector2 widthHeight) {
	}
}
