package com.ado.trader.gui;

import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class MenuGroup extends Group {
	protected Table background, functionTable;
	protected float width, height, stageWidth;
	protected OrthographicCamera cam;
	protected Image shade;

	public MenuGroup(final GameServices gameRes) {
		cam = gameRes.getCam();
		
		width = gameRes.getStage().getViewport().getScreenWidth() * 0.25f;
		height = gameRes.getStage().getViewport().getScreenHeight() * 0.50f;
		stageWidth = gameRes.getStage().getViewport().getScreenWidth();
		
		Pixmap pix = new Pixmap(2, 2, Format.RGBA8888);
		pix.setColor(0, 0, 0, 0.5f);
		pix.fill();
		Texture tex = new Texture(pix);
		pix.dispose();
		shade = new Image(tex);
		shade.setVisible(false);
		addActor(shade);
		 
		background = new Table();
		functionTable = new Table();
		addActor(background);
		addActor(functionTable);
		
		functionTable.center().defaults().width(width * 0.75f).height(40);
		position(gameRes.getStage());
		size(gameRes.getStage());
		
		functionTable.setVisible(false);
		background.setVisible(false);
		
		//background image
		background.setBackground(gameRes.getSkin().getDrawable("gui/bGround"));
		background.add(new Image(gameRes.getSkin().getDrawable("gui/fGround"))).pad(4).fill().expand();
		
		gameRes.getStage().addActor(this);
	}
	
	public void show(){
		if(stageWidth != getStage().getWidth()){
			size(getStage());
			stageWidth = getStage().getViewport().getScreenWidth();
		}
		position(getStage());
		
		background.setVisible(true);
		functionTable.setVisible(true);
		shade.setVisible(true);
	}
	public void hide(){
		functionTable.setVisible(false);
		background.setVisible(false);
		shade.setVisible(false);
	}
	private void position(Stage stage){
		shade.setPosition(stage.getViewport().getScreenX() - 50, stage.getViewport().getScreenY());
		background.setPosition(stage.getViewport().getScreenX() + (width * 1.6f), stage.getViewport().getScreenY() + (height / 2));
		functionTable.setPosition(stage.getViewport().getScreenX() + (width * 1.6f), stage.getViewport().getScreenY() + (height / 2));
	}
	private void size(Stage stage){
		shade.setSize((int)(stage.getViewport().getScreenWidth() * 1.5f), stage.getViewport().getScreenHeight());
		background.setSize(stage.getViewport().getScreenWidth() * 0.25f, stage.getViewport().getScreenHeight() * 0.50f);
		functionTable.setSize(stage.getViewport().getScreenWidth() * 0.25f, stage.getViewport().getScreenHeight() * 0.50f);
	}
	
}
