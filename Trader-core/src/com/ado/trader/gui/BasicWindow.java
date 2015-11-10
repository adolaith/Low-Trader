package com.ado.trader.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

public class BasicWindow extends Table{
	protected Table body;
	float width, height;
	Label titleLabel;
	
	public BasicWindow(String title, float width, float height, BitmapFont font, Skin skin, Stage stage){
		this.width = width;
		this.height = height;
		
		setVisible(false);
		setSize(width, height);
		setBackground(skin.getDrawable("gui/bGround"));
		top();
		
		Table titleBar = new Table();
		titleBar.setBackground(skin.newDrawable("gui/fGround"));
		titleBar.addListener(new DragListener() {
			public void touchDragged(InputEvent event, float x, float y, int pointer){
				float lenX = x-getTouchDownX();
				float lenY = y-getTouchDownY();
				updatePosition(lenX, lenY);
			}
		});
		
		LabelStyle ls = new LabelStyle(font, Color.WHITE);
		titleLabel = new Label(title, ls);
		
		titleBar.add(titleLabel).left().fill().expandX();
		
		ImageButton closeButton = GuiUtils.createImageButton("gui/exitIcon", null, "gui/button", null, skin);
		closeButton.addListener(new InputListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				ToolTip toolTip = (ToolTip)(getStage().getRoot().findActor("tooltip"));
				if(toolTip != null){
					toolTip.show("Close window");
				}
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				ToolTip toolTip = (ToolTip)(getStage().getRoot().findActor("tooltip"));
				if(toolTip != null){
					toolTip.hide();
				}
			}
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				hideWindow();
				return true;
			}
		});
		
		titleBar.add(closeButton).size(20).right().pad(2);
		
		add(titleBar).height(24).fillX().expandX().padTop(2).padLeft(2).padRight(2).row();
		
		body = new Table();
		body.top().padTop(6);
		body.setBackground(skin.newDrawable("gui/fGround"));
		
		
		add(body).pad(2).top().fill().expand();
		
		Group layer = stage.getRoot().findActor("guiLayer");
		if(layer != null){
//			stage.addActor(this);
			layer.addActor(this);
		}else{
//			stage.addActor(this);
			layer = stage.getRoot().findActor("mainMenu");
			layer.addActor(this);
		}
		
//		debugAll();
	}
	public void showWindow(float x, float y){
		setPosition(x, y);
		setVisible(true);
	}
	public void hideWindow(){
		setVisible(false);
	}
	public void updatePosition(float x, float y){
		moveBy(x, y);
	}
	public float getWidth(){
		return width;
	}
	public float getHeight(){
		return height;
	}
	public Label getTitle() {
		return titleLabel;
	}
	public void setTitle(Label title) {
		this.titleLabel = title;
	}
	public Table getBody(){
		return body;
	}
}
