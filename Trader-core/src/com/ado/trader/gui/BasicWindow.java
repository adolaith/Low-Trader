package com.ado.trader.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

public class BasicWindow {
	Table bgTable, functionTable, root;
	int width, height;
	Label title;
	BitmapFont font;
	Skin skin;
	
	public BasicWindow(String title, int width, int height, BitmapFont font, Skin skin, Stage stage){
		this.width = width;
		this.height = height;
		this.font = font;
		this.skin = skin;
		LabelStyle ls = new LabelStyle(font, Color.WHITE);
		this.title = new Label(title, ls);
		init(skin, stage);
	}
	private void init(Skin skin, Stage stage){
		bgTable = new Table();
		bgTable.top();
		bgTable.setHeight(height);
		bgTable.setWidth(width);
		bgTable.setBackground(skin.getDrawable("gui/bGround"));
		Image i = new Image(skin.getDrawable("gui/fGround"));
		bgTable.add(i).pad(4).height(24).width(width-10).row();
		i = new Image(skin.getDrawable("gui/fGround"));
		bgTable.add(i).width(width-10).height(height-38);
		
		functionTable = new Table();
		functionTable.top().left();
		functionTable.setHeight(height);
		functionTable.setWidth(width);
		
		title.addListener(new DragListener() {
			public void touchDragged(InputEvent event, float x, float y, int pointer){
				float lenX = x-getTouchDownX();
				float lenY = y-getTouchDownY();
				updatePosition(lenX, lenY);
			}
		});
		functionTable.add(title).padLeft(6).padTop(6).padBottom(6).width(width-36).fill();
		
		ImageButton closeButton = new ImageButton(GameGui.setImgButtonStyle(skin.getDrawable("gui/delete"), null, skin.getDrawable("gui/button"), null));
		closeButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				hideWindow();
			}
		});
		functionTable.add(closeButton).left().padRight(6).padTop(6).padBottom(6).width(22).height(22).row();
		
		root = new Table();
		root.pad(2).padTop(2);
		functionTable.add(root).top().left().fill().expand().colspan(2).padLeft(4).padBottom(4).padRight(4);
		
		bgTable.setVisible(false);
		functionTable.setVisible(false);
		
		stage.addActor(bgTable);
		stage.addActor(functionTable);
	}
	protected void addLabelPair(String key, String value, BitmapFont font){
		LabelStyle ls = new LabelStyle(font, Color.WHITE);
		Label l = new Label(key, ls);
		float w = (root.getWidth() - 12) / 2;
		root.add(l).width(w);
		l = new Label(value, ls);
		l.setAlignment(Align.right);
		root.add(l).width(w).row();
	}
	protected void updateSize(int width, int height){
		bgTable.setWidth(width);
		bgTable.setHeight(height);
		Cell<?> c = bgTable.getCells().get(1);
		c.size(width-10, height-38);
		functionTable.setWidth(width);
		functionTable.setHeight(height);
	}
	public void showWindow(float x, float y){
		bgTable.setPosition(x, y);
		functionTable.setPosition(x, y);
		bgTable.setVisible(true);
		functionTable.setVisible(true);
	}
	public void hideWindow(){
		bgTable.setVisible(false);
		functionTable.setVisible(false);
	}
	public void updatePosition(float x, float y){
		bgTable.moveBy(x, y);
		functionTable.moveBy(x, y);
	}
}
