package com.ado.trader.gui.editor;

import com.ado.trader.gui.GuiUtils;
import com.ado.trader.gui.ToolTip;
import com.ado.trader.screens.MapEditorScreen;
import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class EditorMenu extends Group {
	Table background, functionTable;
	float width, height, stageWidth;
	OrthographicCamera cam;
	Image shade;

	public EditorMenu(final GameServices gameRes) {
		setName("editorMenu");
		
		new MapFileInterface(gameRes);
		
		cam = gameRes.getCam();
		
		width = gameRes.getStage().getWidth() * 0.25f;
		height = gameRes.getStage().getHeight() * 0.50f;
		stageWidth = gameRes.getStage().getWidth();
		
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
		position();
		size(gameRes.getStage());
		
		functionTable.setVisible(false);
		background.setVisible(false);
		
		//background image
		background.setBackground(gameRes.getSkin().getDrawable("gui/bGround"));
		background.add(new Image(gameRes.getSkin().getDrawable("gui/fGround"))).pad(4).fill().expand();
		
		final ToolTip toolTip = (ToolTip)(gameRes.getStage().getRoot().findActor("tooltip"));
		//quit button
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
				MapFileInterface file = (MapFileInterface) gameRes.getStage().getRoot().findActor("mapFiles");
				file.show(true);
				file.toFront();
				return true;
			}
		});
		functionTable.add(b).row();
		
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
				MapFileInterface file = (MapFileInterface) gameRes.getStage().getRoot().findActor("mapFiles");
				file.show(false);
				file.toFront();
				return true;
			}
		});
		functionTable.add(b).row();
		
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
		
		gameRes.getStage().addActor(this);
		setZIndex(500);
	}

	public void show(){
		if(stageWidth != getStage().getWidth()){
			size(getStage());
			stageWidth = getStage().getWidth();
		}
		position();
		MapEditorScreen.runLogic = false;
		
		for(Actor a: getStage().getActors()){
			if(a.getName() != null){
				if(a == this || a.getName().matches("tooltip") || a.getName().matches("mapFiles") ||
						a.getName().matches("overWrite")){
					continue;
				}
			}
			if(a.isVisible()){
				a.setVisible(false);
			}
		}
		
		background.setVisible(true);
		functionTable.setVisible(true);
		shade.setVisible(true);
	}
	public void hide(){
		functionTable.setVisible(false);
		background.setVisible(false);
		shade.setVisible(false);
		
		for(Actor a: getStage().getActors()){
			if(a.getName() != null){
				if(a == this || a.getName().matches("tooltip") || a.getName().matches("mapFiles")||
						a.getName().matches("overWrite")){
					continue;
				}
				a.setVisible(true);
			}
		}
		
		MapEditorScreen.runLogic = true;
	}
	private void position(){
		shade.setPosition(cam.position.x - cam.viewportWidth / 2, cam.position.y - cam.viewportHeight / 2);
		background.setPosition(cam.position.x - (width / 2), cam.position.y - (height / 2));
		functionTable.setPosition(cam.position.x - (width / 2), cam.position.y - (height / 2));
	}
	private void size(Stage stage){
		shade.setSize(cam.viewportWidth, cam.viewportHeight);
		background.setSize(stage.getWidth() * 0.25f, stage.getHeight() * 0.50f);
		functionTable.setSize(stage.getWidth() * 0.25f, stage.getHeight() * 0.50f);
	}
}
