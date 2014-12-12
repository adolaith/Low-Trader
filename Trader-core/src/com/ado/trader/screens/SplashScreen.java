package com.ado.trader.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

import com.ado.rts.Rts;
import com.ado.rts.tweenAccessors.SpriteTween;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

//old code from a tutorial. TO be replaced.
public class SplashScreen implements Screen{
	
	Texture splashTexture;
	Sprite splashSprite;
	SpriteBatch batch;
	Rts game;
	TweenManager manager;
	

	public SplashScreen(Rts game) {
		this.game = game;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(255, 255, 255, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		manager.update(delta);
		
		batch.begin();
		splashSprite.draw(batch);
		batch.end();
		
		//System.out.println("");
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		//load an img(texture)
		splashTexture = new Texture("img/logo.png"); 
		splashTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		//creates a sprite from a texture
		splashSprite = new Sprite(splashTexture);
		splashSprite.setColor(1, 1, 1, 0);
		splashSprite.setX(Gdx.graphics.getWidth() / 2 - (splashSprite.getWidth()/2));
		splashSprite.setY(Gdx.graphics.getHeight() / 2 - (splashSprite.getHeight()/2));
		
		batch = new SpriteBatch();
		
		Tween.registerAccessor(Sprite.class, new SpriteTween());
		
		manager = new TweenManager();
		
		//when the tween finishes, call tweenCompleted
		TweenCallback cb = new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				tweenCompleted();
			}
		};
		
		//the tween animation and run settings
		Tween.to(splashSprite, SpriteTween.ALPHA, 1.3f).target(1).ease(TweenEquations.easeInQuad).repeatYoyo(1, 2f).setCallback(cb).setCallbackTriggers(TweenCallback.COMPLETE).start(manager);
	}
	
	//on tween fin, switch to main menu screen
	public void tweenCompleted(){
		game.setScreen(new MainMenu(game));
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

}
