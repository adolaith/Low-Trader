package com.ado.trader.gui;

import com.ado.trader.GameMain;
import com.ado.trader.screens.GameScreen;
import com.ado.trader.systems.GameTime;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
	InformationWindow infoWindow;
	NewsWindow newsWindow;

	public ControlArea(final GameServices gameRes){
		infoWindow = new InformationWindow(gameRes);
		newsWindow = new NewsWindow(gameRes);
		
		bgTable = new Table();
		bgTable.setHeight(height);
		bgTable.setWidth(width);
		bgTable.top();
		bgTable.setBackground(gameRes.skin.getDrawable("gui/bGround"));
		bgTable.add(new Image(gameRes.skin.getDrawable("gui/fGround"))).colspan(2).pad(4).height(24).width(width-10).row();
		bgTable.add(new Image(gameRes.skin.getDrawable("gui/fGround"))).padLeft(2).padRight(2).height(24).width(24);
		bgTable.add(new Image(gameRes.skin.getDrawable("gui/fGround"))).height(24).width(width-28-8).left().row();
		
		bgTable.add(new Image(gameRes.skin.getDrawable("gui/fGround"))).padTop(4).padLeft(2).padRight(2).height(24).width(24);
		bgTable.add(new Image(gameRes.skin.getDrawable("gui/fGround"))).padTop(4).height(24).width(width-28-8).left().row();
		
		bgTable.add(new Image(gameRes.skin.getDrawable("gui/fGround"))).padTop(4).padLeft(2).padRight(2).height(24).width(24);
		bgTable.add(new Image(gameRes.skin.getDrawable("gui/fGround"))).padTop(4).height(24).width(width-28-8).left().row();
		
		functionTable = new Table();
		functionTable.setHeight(height);
		functionTable.setWidth(width);
		functionTable.top().left();
//		functionTable.debug();
		
		//News window button
		ImageButton newsButton = createButton("gui/infoIcon2", "gui/button", gameRes.skin);
		newsButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				newsWindow.showWindow(gameRes.getCam().position.x, gameRes.getCam().position.y);
			}
		});
		functionTable.add(newsButton).padTop(6).padLeft(2).padRight(2).width(22).height(22);
		
		//build menu button
		ImageButton buildButton = createButton("gui/workIcon", "gui/button", gameRes.skin);
		buildButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
//				guiMain.buildMenu.buildMenu.get("mainmenu").setPosition(functionTable.getX()-40, functionTable.getY()+200);
//				guiMain.buildMenu.buildMenu.get("mainmenu").setVisible(true);
			}
		});
		functionTable.add(buildButton).padRight(2).padTop(6).width(22).height(22);
		
		//Misc info button
		ImageButton statsButton = createButton("gui/statsIcon", "gui/button", gameRes.skin);
		statsButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				infoWindow.showWindow(gameRes.getCam().position.x, gameRes.getCam().position.y);
			}
		});
		functionTable.add(statsButton).padRight(2).padTop(6).width(22).height(22);
		
		//Map button
		ImageButton mapButton = createButton("gui/mapIcon", "gui/button", gameRes.skin);
		mapButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				newsWindow.newMessage("Over-map goes here");
			}
		});
		functionTable.add(mapButton).padRight(2).padTop(6).width(22).height(22).row();
		
		//Layer up
		ImageButton upButton = createButton("gui/arrowUp", "gui/button", gameRes.skin);
		upButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.log(GameMain.LOG, "+height map button!!");
			}
		});
		functionTable.add(upButton).padTop(6).padLeft(6).padRight(6).width(22).height(22);
		
		//Days label
		LabelStyle ls = new LabelStyle(gameRes.font, Color.WHITE);
		daysLabel = new Label("", ls);
		daysLabel.setAlignment(Align.center);
		functionTable.add(daysLabel).padTop(4).colspan(3).width(width-28-8).row();
		
		//Layer down
		ImageButton downButton = createButton("gui/arrowDown", "gui/button", gameRes.skin);
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
		ImageButton exitButton = createButton("gui/exitIcon", "gui/button", gameRes.skin);
		exitButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.exit();
			}
		});
		functionTable.add(exitButton).padTop(6).width(22).height(22);
		
		//slow time button
		ImageButton rwButton = createButton("gui/arrowRW", "gui/button", gameRes.skin);
		rwButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				GameScreen.setSpeed(-1);
			}
		});
		functionTable.add(rwButton).padTop(6).padLeft(3).padRight(2).width(22).height(22);
		
		//normal time button
		ImageButton playButton = createButton("gui/arrowPlay", "gui/button", gameRes.skin);
		playButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				GameScreen.setSpeed(0);
			}
		});
		functionTable.add(playButton).padTop(6).padLeft(4).padRight(2).width(22).height(22);
		
		//fast forward time button
		ImageButton ffButton = createButton("gui/arrowFF", "gui/button", gameRes.skin);
		ffButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				GameScreen.setSpeed(1);
			}
		});
		functionTable.add(ffButton).padTop(6).padLeft(2).padRight(2).width(22).height(22);
		
		gameRes.stage.addActor(bgTable);
		gameRes.stage.addActor(functionTable);
	}
	private ImageButton createButton(String img, String bg, Skin skin){
		return new ImageButton(GameGui.setImgButtonStyle(skin.getDrawable(img), null, 
				skin.getDrawable(bg), null));
	}
	public void update(float x, float y, World world){
		infoWindow.update(world);
		newsWindow.update();
		
		//update time/date
		GameTime time = world.getSystem(GameTime.class);
		daysLabel.setText("Day: "+time.getDays());
		String s = String.format("%02d:%02d %s", time.getTime()/60, time.getTime()%60, time.getTimeOfDay());
		timeLabel.setText(s);
		
		//position gui element in bottom right corner
		x += (Gdx.graphics.getWidth() / 2) - width - 2;
		y -= (Gdx.graphics.getHeight() / 2) - 2;
		
		bgTable.setPosition(x, y);
		functionTable.setPosition(x, y);
	}
}
