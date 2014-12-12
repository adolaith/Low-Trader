package com.ado.trader.gui;

import com.ado.trader.GameMain;
import com.ado.trader.systems.GameTime;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

//Main control Gui element
//Has menu buttons, time display and control, map height buttons and quit game button

public class ControlArea{
	Table bgTable, functionTable;
	int width = 184;
	int height = 120;
	Label daysLabel, timeLabel;

	public ControlArea(final Gui guiMain){
		Skin skin = guiMain.skin;
		bgTable = new Table();
		bgTable.setHeight(height);
		bgTable.setWidth(width);
		bgTable.top();
		bgTable.setBackground(skin.getDrawable("gui/bGround"));
		bgTable.add(new Image(skin.getDrawable("gui/fGround"))).colspan(2).pad(4).height(24).width(width-10).row();
		bgTable.add(new Image(skin.getDrawable("gui/fGround"))).padLeft(2).padRight(2).height(24).width(24);
		bgTable.add(new Image(skin.getDrawable("gui/fGround"))).height(24).width(width-28-8).left().row();
		
		bgTable.add(new Image(skin.getDrawable("gui/fGround"))).padTop(4).padLeft(2).padRight(2).height(24).width(24);
		bgTable.add(new Image(skin.getDrawable("gui/fGround"))).padTop(4).height(24).width(width-28-8).left().row();
		
		bgTable.add(new Image(skin.getDrawable("gui/fGround"))).padTop(4).padLeft(2).padRight(2).height(24).width(24);
		bgTable.add(new Image(skin.getDrawable("gui/fGround"))).padTop(4).height(24).width(width-28-8).left().row();
		
		functionTable = new Table();
		functionTable.setHeight(height);
		functionTable.setWidth(width);
		functionTable.top().left();
//		functionTable.debug();
		final OrthographicCamera cam = guiMain.game.getRenderer().getCamera();
		
		//News window button
		ImageButton newsButton = createButton("gui/infoIcon2", "gui/button", skin);
		newsButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				guiMain.newsWindow.showWindow(cam.position.x-guiMain.infoWindow.width/2, cam.position.y-guiMain.infoWindow.height/2);
			}
		});
		functionTable.add(newsButton).padTop(6).padLeft(2).padRight(2).width(22).height(22);
		
		//build menu button
		ImageButton buildButton = createButton("gui/workIcon", "gui/button", skin);
		buildButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				guiMain.buildMenu.buildMenu.get("mainmenu").setPosition(functionTable.getX()-40, functionTable.getY()+200);
				guiMain.buildMenu.buildMenu.get("mainmenu").setVisible(true);
			}
		});
		functionTable.add(buildButton).padRight(2).padTop(6).width(22).height(22);
		
		//Misc info button
		ImageButton statsButton = createButton("gui/statsIcon", "gui/button", skin);
		statsButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				guiMain.infoWindow.showWindow(cam.position.x-guiMain.infoWindow.width/2, cam.position.y-guiMain.infoWindow.height/2);
			}
		});
		functionTable.add(statsButton).padRight(2).padTop(6).width(22).height(22);
		
		//Map button
		ImageButton mapButton = createButton("gui/mapIcon", "gui/button", skin);
		mapButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				guiMain.newsWindow.newMessage("Over-map goes here");
			}
		});
		functionTable.add(mapButton).padRight(2).padTop(6).width(22).height(22).row();
		
		//Layer up
		ImageButton upButton = createButton("gui/arrowUp", "gui/button", skin);
		upButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.log(GameMain.LOG, "+height map button!!");
			}
		});
		functionTable.add(upButton).padTop(6).padLeft(6).padRight(6).width(22).height(22);
		
		//Days label
		LabelStyle ls = new LabelStyle(guiMain.font, Color.WHITE);
		daysLabel = new Label("", ls);
		daysLabel.setAlignment(Align.center);
		functionTable.add(daysLabel).padTop(4).colspan(3).width(width-28-8).row();
		
		//Layer down
		ImageButton downButton = createButton("gui/arrowDown", "gui/button", skin);
		downButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.log(GameMain.LOG, "-height map button!!");
			}
		});
		functionTable.add(downButton).padTop(6).padLeft(6).padRight(6).width(22).height(22);
		
		//time label
		timeLabel = new Label("", ls);
		timeLabel.setAlignment(Align.center);
		functionTable.add(timeLabel).colspan(3).padTop(6).width(width-28-8).row();
		
		//exit game button
		ImageButton exitButton = createButton("gui/exitIcon", "gui/button", skin);
		exitButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.exit();
			}
		});
		functionTable.add(exitButton).padTop(6).width(22).height(22);
		
		//slow time button
		ImageButton rwButton = createButton("gui/arrowRW", "gui/button", skin);
		rwButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				guiMain.game.setSpeed(-1);
			}
		});
		functionTable.add(rwButton).padTop(6).padLeft(3).padRight(2).width(22).height(22);
		
		//normal time button
		ImageButton playButton = createButton("gui/arrowPlay", "gui/button", skin);
		playButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				guiMain.game.setSpeed(0);
			}
		});
		functionTable.add(playButton).padTop(6).padLeft(4).padRight(2).width(22).height(22);
		
		//fast forward time button
		ImageButton ffButton = createButton("gui/arrowFF", "gui/button", skin);
		ffButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				guiMain.game.setSpeed(1);
			}
		});
		functionTable.add(ffButton).padTop(6).padLeft(2).padRight(2).width(22).height(22);
		
		guiMain.stage.addActor(bgTable);
		guiMain.stage.addActor(functionTable);
	}
	private ImageButton createButton(String img, String bg, Skin skin){
		return new ImageButton(GuiUtils.setImgButtonStyle(skin.getDrawable(img), null, 
				skin.getDrawable(bg), null));
	}
	public void update(GameTime time, OrthographicCamera cam){
		daysLabel.setText("Day: "+time.getDays());
		String s = String.format("%02d:%02d %s", time.getTime()/60, time.getTime()%60, time.getTimeOfDay());
		timeLabel.setText(s);		//use string format here
		updatePosition(cam);
	}
	private void updatePosition(OrthographicCamera cam){
		float x = cam.position.x+(cam.viewportWidth/2)-width-2;
		float y =cam.position.y-cam.viewportHeight/2+2;
		bgTable.setPosition(x, y);
		functionTable.setPosition(x, y);
	}
}
