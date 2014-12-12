package com.ado.trader.gui;

import com.ado.trader.screens.GameScreen;
import com.ado.trader.systems.GameTime;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class InformationWindow extends BasicWindow {
	Table buttonTable, devInfo;
	Cell<?> bodyCell;
	
	public InformationWindow(final Gui gui) {
		super("Information Overview", 320, 200, gui);
		
		buttonTable = new Table();
		buttonTable.setWidth(28);
		buttonTable.setHeight(root.getHeight());
		buttonTable.top().defaults().padBottom(2).padRight(2);
		
		ImageButton firstButton = new ImageButton(GuiUtils.setImgButtonStyle(gui.skin.getDrawable("gui/statsIcon"), null, gui.skin.getDrawable("gui/button"), null));
		firstButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				bodyCell.setActor(devInfo);
				title.setText("Dev Info");
			}
		});
		buttonTable.add(firstButton).width(22).height(22).row();
		
		root.add(buttonTable).top();
		
		initDevTable(gui);
		
		bodyCell = root.add(devInfo).fill().expand();
	}
	
	private void initDevTable(Gui gui){
		devInfo = new Table();
		devInfo.top();
		addLabelPair("Fps: ","fps", gui.font);
		addLabelPair("Delta: ","delta", gui.font);
		addLabelPair("Game time: ","time", gui.font);
		addLabelPair("Time mod: ","timeMod", gui.font);
		addLabelPair("Active entities: ","active", gui.font);
		addLabelPair("Logic time: ", "logic", gui.font);
		addLabelPair("Render time: ", "render", gui.font);
	}
	public void update(GameScreen game){
		if(!functionTable.isVisible())return;
		((Label)devInfo.findActor("fps")).setText(""+Gdx.graphics.getFramesPerSecond());
		((Label)devInfo.findActor("delta")).setText(""+Gdx.graphics.getRawDeltaTime());
		((Label)devInfo.findActor("time")).setText(""+game.getWorld().getSystem(GameTime.class).getTime());
		((Label)devInfo.findActor("timeMod")).setText(""+game.speed);
		((Label)devInfo.findActor("active")).setText(""+game.getWorld().getEntityManager().getActiveEntityCount());
		((Label)devInfo.findActor("logic")).setText(""+game.updateTime);
		((Label)devInfo.findActor("render")).setText(""+game.getRenderer().renderTime);
	}
	protected void addLabelPair(String text,String valueName, BitmapFont font){
		LabelStyle ls = new LabelStyle(font, Color.WHITE);
		Label l = new Label(text, ls);
		float w = (root.getWidth() / 2) - 12;
		devInfo.add(l).width(w);
		ls = new LabelStyle(font, Color.BLUE);
		l = new Label("", ls);
		l.setAlignment(Align.right);
		l.setName(valueName);
		devInfo.add(l).width(w).right().row();
	}
}
