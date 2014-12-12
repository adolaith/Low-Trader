package com.ado.trader.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ado.trader.GameMain;

public class DesktopLauncher {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "rts - BURP!";
//		cfg.useGL30 = true;
		cfg.width = 1280;
		cfg.height = 720;
		cfg.vSyncEnabled =false;

		new LwjglApplication(new GameMain(), cfg);
	}
}