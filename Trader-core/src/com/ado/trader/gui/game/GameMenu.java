package com.ado.trader.gui.game;

import com.ado.trader.gui.GuiUtils;
import com.ado.trader.gui.MenuGroup;
import com.ado.trader.gui.ToolTip;
import com.ado.trader.screens.GameScreen;
import com.ado.trader.screens.MainMenu;
import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class GameMenu extends MenuGroup {

	public GameMenu(final GameServices gameRes) {
		super(gameRes);
		setName("gameMenu");
		new SaveLoadGame(gameRes);
		
		final ToolTip toolTip = (ToolTip)(gameRes.getStage().getRoot().findActor("tooltip"));
		
		//Load button
		LabelStyle lStyle = new LabelStyle(gameRes.getFont(), Color.WHITE);
		Button b = GuiUtils.createButton("gui/button", null, gameRes.getSkin());
		b.add(new Label("Load game",lStyle));
		b.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.show("Load saved map");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.hide();
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				SaveLoadGame file = (SaveLoadGame) gameRes.getStage().getRoot().findActor("saveMenu");
				file.show(true);
				file.toFront();

				return true;
			}
		});
		functionTable.add(b).row();
		
		//Save button
		b = GuiUtils.createButton("gui/button", null, gameRes.getSkin());
		b.add(new Label("Save map",lStyle));
		b.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.show("Save map");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.hide();
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				SaveLoadGame file = (SaveLoadGame) gameRes.getStage().getRoot().findActor("saveMenu");
				file.show(false);
				file.toFront();

				return true;
			}
		});
		functionTable.add(b).row();
		
		//Back to main menu
		b = GuiUtils.createButton("gui/button", null, gameRes.getSkin());
		b.add(new Label("Quit to Main menu",lStyle));
		b.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.show("Main Menu");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.hide();
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				GameScreen.getGame().setScreen(new MainMenu(GameScreen.getGame()));
				return true;
			}
		});
		functionTable.add(b).row();
		
		//Quit to desktop
		b = GuiUtils.createButton("gui/button", null, gameRes.getSkin());
		b.add(new Label("Quit to Desktop",lStyle));
		b.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.show("Exit game");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.hide();
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.exit();
				return true;
			}
		});
		functionTable.add(b).row();
		
		//back to game
		b = GuiUtils.createButton("gui/button", null, gameRes.getSkin());
		b.add(new Label("Back",lStyle));
		b.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.show("Back to game");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.hide();
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				hide();
				return true;
			}
		});
		functionTable.add(b);
	}

	@Override
	public void show(){
		super.show();
		ControlArea control = (ControlArea) getStage().getRoot().findActor("controlArea");
		control.toBack();
		control.setTouchable(Touchable.disabled);
		GameScreen.logicRunning = false;
	}
	
	@Override
	public void hide(){
		super.hide();
		ControlArea control = (ControlArea) getStage().getRoot().findActor("controlArea");
		control.toFront();
		control.setTouchable(Touchable.enabled);
		GameScreen.logicRunning = true;
	}
}
