package com.ado.trader.gui.game;

import com.ado.trader.GameMain;
import com.ado.trader.gui.GuiUtils;
import com.ado.trader.gui.ToolTip;
import com.ado.trader.screens.GameScreen;
import com.ado.trader.systems.GameTime;
import com.ado.trader.utils.GameServices;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;

//Main control Gui element
//Has menu buttons, time display and control, map height buttons and quit game button

public class ControlArea extends Group{
	Table bgTable, functionTable;
	int width = 184;
	int height = 120;
	Label daysLabel, timeLabel;
	
	World world;
	InformationWindow infoWindow;
	NewsWindow newsWindow;

	public ControlArea(final GameServices gameRes){
		setVisible(true);
		setName("controlArea");
		world = gameRes.getWorld();
		infoWindow = new InformationWindow(gameRes);
		newsWindow = new NewsWindow(gameRes);
		
		new GameMenu(gameRes);
		
		bgTable = new Table();
		bgTable.setHeight(height);
		bgTable.setWidth(width);
		bgTable.top();
		bgTable.setBackground(gameRes.getSkin().getDrawable("gui/bGround"));
		bgTable.add(new Image(gameRes.getSkin().getDrawable("gui/fGround"))).colspan(2).pad(4).height(24).width(width-10).row();
		bgTable.add(new Image(gameRes.getSkin().getDrawable("gui/fGround"))).padLeft(2).padRight(2).height(24).width(24);
		bgTable.add(new Image(gameRes.getSkin().getDrawable("gui/fGround"))).height(24).width(width-28-8).left().row();
		
		bgTable.add(new Image(gameRes.getSkin().getDrawable("gui/fGround"))).padTop(4).padLeft(2).padRight(2).height(24).width(24);
		bgTable.add(new Image(gameRes.getSkin().getDrawable("gui/fGround"))).padTop(4).height(24).width(width-28-8).left().row();
		
		bgTable.add(new Image(gameRes.getSkin().getDrawable("gui/fGround"))).padTop(4).padLeft(2).padRight(2).height(24).width(24);
		bgTable.add(new Image(gameRes.getSkin().getDrawable("gui/fGround"))).padTop(4).height(24).width(width-28-8).left().row();
		
		functionTable = new Table();
		functionTable.setHeight(height);
		functionTable.setWidth(width);
		functionTable.top().left();
//		functionTable.debug();
		
		//News window button
		final ImageButton newsButton = GuiUtils.createImageButton("gui/infoIcon2", null, "gui/button", null, gameRes.getSkin());
		newsButton.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				ToolTip toolTip = (ToolTip)(getStage().getRoot().findActor("tooltip"));
				toolTip.show("Shows News panel");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				ToolTip toolTip = (ToolTip)(getStage().getRoot().findActor("tooltip"));
				toolTip.hide();
			}
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				Viewport view = gameRes.getStage().getViewport();
				newsWindow.showWindow(view.getScreenX() + 2, view.getScreenY() + 2);
				return true;
			}
		});
		functionTable.add(newsButton).padTop(6).padLeft(2).padRight(2).width(22).height(22);
		
		//build menu button
		ImageButton buildButton = GuiUtils.createImageButton("gui/workIcon", null, "gui/button", null, gameRes.getSkin());
		buildButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
//				guiMain.buildMenu.buildMenu.get("mainmenu").setPosition(functionTable.getX()-40, functionTable.getY()+200);
//				guiMain.buildMenu.buildMenu.get("mainmenu").setVisible(true);
			}
		});
		functionTable.add(buildButton).padRight(2).padTop(6).width(22).height(22);
		
		//Misc info button
		final ImageButton statsButton = GuiUtils.createImageButton("gui/statsIcon", null, "gui/button", null, gameRes.getSkin());
		statsButton.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				ToolTip toolTip = (ToolTip)(getStage().getRoot().findActor("tooltip"));
				toolTip.show("Shows Misc. Info panel");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				ToolTip toolTip = (ToolTip)(getStage().getRoot().findActor("tooltip"));
				toolTip.hide();
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				Viewport view = gameRes.getStage().getViewport();
				infoWindow.showWindow(view.getScreenX() + view.getScreenWidth() - infoWindow.getWidth() - 2, view.getScreenY() + view.getScreenHeight() - infoWindow.getHeight() - 2);
				return true;
			}
		});
		functionTable.add(statsButton).padRight(2).padTop(6).width(22).height(22);
		
		//Map button
		final ImageButton mapButton = GuiUtils.createImageButton("gui/mapIcon", null, "gui/button", null, gameRes.getSkin());
		mapButton.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				ToolTip toolTip = (ToolTip)(getStage().getRoot().findActor("tooltip"));
				toolTip.show("Test announcement");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				ToolTip toolTip = (ToolTip)(getStage().getRoot().findActor("tooltip"));
				toolTip.hide();
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.log("ControlArea: ", "button clicked");
				newsWindow.newMessage("Over-map goes here");
				return true;
			}
		});
		functionTable.add(mapButton).padRight(2).padTop(6).width(22).height(22).row();
		
		//Layer up
		final ImageButton upButton = GuiUtils.createImageButton("gui/arrowUp", null, "gui/button", null, gameRes.getSkin());
		upButton.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				ToolTip toolTip = (ToolTip)(getStage().getRoot().findActor("tooltip"));
				toolTip.show("Change map height(+)");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				ToolTip toolTip = (ToolTip)(getStage().getRoot().findActor("tooltip"));
				toolTip.hide();
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.log(GameMain.LOG, "+height map button!!");
				return true;
			}
		});
		functionTable.add(upButton).padTop(6).padLeft(6).padRight(6).width(22).height(22);
		
		//Days label
		LabelStyle ls = new LabelStyle(gameRes.getFont(), Color.WHITE);
		daysLabel = new Label("", ls);
		daysLabel.setAlignment(Align.center);
		functionTable.add(daysLabel).padTop(4).colspan(3).width(width-28-8).row();
		
		//Layer down
		final ImageButton downButton = GuiUtils.createImageButton("gui/arrowDown", null, "gui/button", null, gameRes.getSkin());
		downButton.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				ToolTip toolTip = (ToolTip)(getStage().getRoot().findActor("tooltip"));
				toolTip.show("Change map height(-)");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				ToolTip toolTip = (ToolTip)(getStage().getRoot().findActor("tooltip"));
				toolTip.hide();
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.log(GameMain.LOG, "-height map button!!");
				return true;
			}
		});
		functionTable.add(downButton).padTop(6).padLeft(6).padRight(6).width(22).height(22);
		
		//time label
		timeLabel = new Label("", ls);
		timeLabel.setAlignment(Align.center);
		functionTable.add(timeLabel).colspan(3).padTop(6).width(width-28-8).row();
		
		//exit game button
		final ImageButton exitButton = GuiUtils.createImageButton("gui/exitIcon", null, "gui/button", null, gameRes.getSkin());
		exitButton.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				ToolTip toolTip = (ToolTip)(getStage().getRoot().findActor("tooltip"));
				toolTip.show("Exit game");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				ToolTip toolTip = (ToolTip)(getStage().getRoot().findActor("tooltip"));
				toolTip.hide();
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				GameMenu menu = (GameMenu) gameRes.getStage().getRoot().findActor("gameMenu");
				menu.show();
				return true;
			}
		});
		functionTable.add(exitButton).padTop(6).width(22).height(22);
		
		//slow time button
		final ImageButton rwButton = GuiUtils.createImageButton("gui/arrowRW", null, "gui/button", null, gameRes.getSkin());
		rwButton.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				ToolTip toolTip = (ToolTip)(getStage().getRoot().findActor("tooltip"));
				toolTip.show("Slow game time speed");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				ToolTip toolTip = (ToolTip)(getStage().getRoot().findActor("tooltip"));
				toolTip.hide();
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				GameScreen.setSpeed(-1);
				return true;
			}
		});
		functionTable.add(rwButton).padTop(6).padLeft(3).padRight(2).width(22).height(22);
		
		//normal time button
		final ImageButton playButton = GuiUtils.createImageButton("gui/arrowPlay", null, "gui/button", null, gameRes.getSkin());
		playButton.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				ToolTip toolTip = (ToolTip)(getStage().getRoot().findActor("tooltip"));
				toolTip.show("Normal game time speed");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				ToolTip toolTip = (ToolTip)(getStage().getRoot().findActor("tooltip"));
				toolTip.hide();
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				GameScreen.setSpeed(0);
				return true;
			}
		});
		functionTable.add(playButton).padTop(6).padLeft(4).padRight(2).width(22).height(22);
		
		//fast forward time button
		final ImageButton ffButton = GuiUtils.createImageButton("gui/arrowFF", null, "gui/button", null, gameRes.getSkin());
		ffButton.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				ToolTip toolTip = (ToolTip)(getStage().getRoot().findActor("tooltip"));
				toolTip.show("Increase game time speed");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				ToolTip toolTip = (ToolTip)(getStage().getRoot().findActor("tooltip"));
				toolTip.hide();
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				GameScreen.setSpeed(1);
				return true;
			}
		});
		functionTable.add(ffButton).padTop(6).padLeft(2).padRight(2).width(22).height(22);
		
		float x = gameRes.getStage().getViewport().getScreenX() + gameRes.getStage().getViewport().getScreenWidth() - (width + 2);
		float y = gameRes.getStage().getViewport().getScreenY() + 2;
		
		bgTable.setPosition(x, y);
		functionTable.setPosition(x, y);
		
		addActor(bgTable);
		addActor(functionTable);
		
		gameRes.getStage().addActor(this);
	}
	@Override
	public void act(float delta){
		super.act(delta);
		infoWindow.update();
		
		//update time/date
		GameTime time = world.getSystem(GameTime.class);
		daysLabel.setText("Day: "+time.getDays());
		String s = String.format("%02d:%02d %s", time.getTime()/60, time.getTime()%60, time.getTimeOfDay());
		timeLabel.setText(s);
		
//		if(InputHandler.getVelocity().x != 0 || InputHandler.getVelocity().y != 0){
//			bgTable.moveBy(InputHandler.getVelocity().x, InputHandler.getVelocity().y);
//			functionTable.moveBy(InputHandler.getVelocity().x, InputHandler.getVelocity().y);
//		}
	}
}
