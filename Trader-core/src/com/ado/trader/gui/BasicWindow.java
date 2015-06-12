package com.ado.trader.gui;

import com.badlogic.gdx.Input.Keys;
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
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

public class BasicWindow extends Table{
	protected Table body;
	float width, height;
	private Label title;
	BitmapFont font;
	Skin skin;
	
	public BasicWindow(String title, float width, float height, BitmapFont font, Skin skin, Stage stage){
		this.width = width;
		this.height = height;
		this.font = font;
		this.skin = skin;
		
		setSize(width, height);
		setBackground(skin.getDrawable("gui/bGround"));
		top();
		
		Table titleBar = new Table();
		titleBar.setBackground(skin.getDrawable("gui/fGround"));
		titleBar.addListener(new DragListener() {
			public void touchDragged(InputEvent event, float x, float y, int pointer){
				float lenX = x-getTouchDownX();
				float lenY = y-getTouchDownY();
				updatePosition(lenX, lenY);
			}
		});
		
		LabelStyle ls = new LabelStyle(font, Color.WHITE);
		this.title = new Label(title, ls);
		
		titleBar.add(this.title).left().fill().expand().padLeft(2);
		
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
		
		titleBar.add(closeButton).size(20).right().pad(2).fill();
		
		add(titleBar).top().height(24).padTop(2).padLeft(2).padRight(2).fill().row();
		
		body = new Table();
		body.top();
		body.setBackground(skin.getDrawable("gui/fGround"));
		
		add(body).pad(2).top().fill().expand();
		
		setVisible(false);
		
		Group layer = stage.getRoot().findActor("guiLayer");
		if(layer != null){
			layer.addActor(this);	
		}else{
			stage.addActor(this);
		}
		
//		debugAll();
	}
	protected void addLabelPair(String key, String value, BitmapFont font){
		LabelStyle ls = new LabelStyle(font, Color.WHITE);
		Label l = new Label(key, ls);
		float w = (body.getWidth() - 12) / 2;
		body.add(l).width(w);
		l = new Label(value, ls);
		l.setAlignment(Align.right);
		body.add(l).width(w).row();
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
		return title;
	}
	public void setTitle(Label title) {
		this.title = title;
	}
}
