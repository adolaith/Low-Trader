package com.ado.trader;

import com.ado.trader.screens.MainMenu;
import com.badlogic.gdx.Game;

public class GameMain extends Game {
	public static final String LOG = "Debug log: ";
	public static final String VERSION = "0.0.0.01";
	
	@Override
	public void create() {		
		setScreen(new MainMenu(this));
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	
	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}
}
