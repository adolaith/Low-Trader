package com.ado.trader.gui.game;

import com.ado.trader.gui.BasicWindow;
import com.ado.trader.gui.GuiUtils;
import com.ado.trader.input.InputHandler;
import com.ado.trader.rendering.WorldRenderer;
import com.ado.trader.screens.GameScreen;
import com.ado.trader.systems.GameTime;
import com.ado.trader.utils.GameServices;
import com.artemis.World;
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
	World world;
	
	public InformationWindow(GameServices gameRes) {
		super("Information Overview", 320, 200, gameRes.getFont(), gameRes.getSkin(), gameRes.getStage());
		this.world = gameRes.getWorld();
		
		buttonTable = new Table();
		buttonTable.setWidth(28);
		buttonTable.setHeight(root.getHeight());
		buttonTable.top().defaults().padBottom(2).padRight(2);
		
		ImageButton firstButton = GuiUtils.createImageButton("gui/statsIcon", null, "gui/button", null, gameRes.getSkin());
		firstButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				bodyCell.setActor(devInfo);
				getTitle().setText("Dev Info");
			}
		});
		buttonTable.add(firstButton).width(22).height(22).row();
		
		root.add(buttonTable).top();
		
		initDevTable(gameRes.getFont());
		
		bodyCell = root.add(devInfo).fill().expand();
	}
	
	private void initDevTable(BitmapFont font){
		devInfo = new Table();
		devInfo.top().defaults().fill().expand();
		addLabelPair("Fps: ","fps", font);
		addLabelPair("Delta: ","delta", font);
		addLabelPair("Game time: ","time", font);
		addLabelPair("Time mod: ","timeMod", font);
		addLabelPair("Active entities: ","active", font);
		addLabelPair("Logic time: ", "logic", font);
		addLabelPair("Render time: ", "render", font);
	}
	public void update(){
		if(!InputHandler.getVelocity().isZero()){
			updatePosition(InputHandler.getVelocity().x, InputHandler.getVelocity().y);
		}
		if(!functionTable.isVisible())return;
		((Label)devInfo.findActor("fps")).setText(""+Gdx.graphics.getFramesPerSecond());
		((Label)devInfo.findActor("delta")).setText(""+Gdx.graphics.getRawDeltaTime());
		((Label)devInfo.findActor("time")).setText(""+world.getSystem(GameTime.class).getTime());
		((Label)devInfo.findActor("timeMod")).setText(""+GameScreen.speed);
		((Label)devInfo.findActor("active")).setText(""+world.getEntityManager().getActiveEntityCount());
		((Label)devInfo.findActor("logic")).setText(""+GameScreen.updateTime);
		((Label)devInfo.findActor("render")).setText(""+ WorldRenderer.renderTime);
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
