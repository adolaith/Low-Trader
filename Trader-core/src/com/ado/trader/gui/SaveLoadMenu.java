package com.ado.trader.gui;

import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class SaveLoadMenu extends Group {
	protected Table background, functionTable;
	protected List<String> folderList;
	protected OrthographicCamera cam;
	protected LabelStyle labelStyle;
	protected TextField field;
	protected float width, height;
	protected String filePath;
	protected Button save, load, delete;
	protected Cell<Button> activeButton;

	public SaveLoadMenu(final GameServices gameRes) {
		width = gameRes.getStage().getWidth() * 0.25f;
		height = gameRes.getStage().getHeight() * 0.50f;
		cam = gameRes.getCam();
		
		background = new Table();
		background.top();
		functionTable = new Table();
		
		background.setSize(width, height);
		functionTable.setSize(width, height);
		
		new OverwriteDialog(gameRes, width, height * 0.3f);
		
		position(gameRes.getStage());
		
		background.setBackground(gameRes.getSkin().getDrawable("gui/bGround"));
		background.add(new Image(gameRes.getSkin().getDrawable("gui/fGround"))).pad(4).width(width * 0.95f).height(height * 0.75f).row();
		
		ListStyle listStyle = new ListStyle();
		listStyle.font = gameRes.getFont();
		listStyle.selection = gameRes.getSkin().getDrawable("gui/tooltip");
		folderList = new List<String>(listStyle);
		folderList.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(folderList.getSelected() != null){
					field.setText(folderList.getSelected());
				}
			}
		});
		
		ScrollPaneStyle spS = new ScrollPaneStyle();
		spS.vScroll = gameRes.getSkin().getDrawable("gui/scrollBar");
		spS.vScrollKnob = gameRes.getSkin().getDrawable("gui/scrollBar");
		
		ScrollPane sP = new ScrollPane(folderList, spS);
		sP.setScrollingDisabled(true, false);
		sP.setScrollBarPositions(false, true);
		sP.setSize(width * 0.95f, height * 0.85f);
		
		functionTable.add(sP).colspan(3).pad(4).width(width * 0.95f).height(height * 0.75f).row();
		
		TextFieldStyle textStyle = new TextFieldStyle();
		textStyle.font = gameRes.getFont();
		textStyle.fontColor = Color.BLACK;
		textStyle.background = gameRes.getSkin().getDrawable("gui/tooltip");
		field = new TextField("", textStyle);
		field.setWidth(width * 0.95f);
		
		functionTable.add(field).padTop(-6).colspan(3).width(width * 0.95f).height(height * 0.1f).row();
		
		final ToolTip toolTip = (ToolTip)(gameRes.getStage().getRoot().findActor("tooltip"));
		//save button
		labelStyle = new LabelStyle(gameRes.getFont(), Color.WHITE);
		save = GuiUtils.createButton("gui/button", null, gameRes.getSkin());
		save.add(new Label("Save",labelStyle));
		
		activeButton = functionTable.add(save).bottom().width(width * 0.2f).height(height * 0.1f);
		
		delete = GuiUtils.createButton("gui/button", null, gameRes.getSkin());
		delete.add(new Label("Delete",labelStyle));

		functionTable.add(delete).bottom().width(width * 0.2f).height(height * 0.1f);
		
		Button b = GuiUtils.createButton("gui/button", null, gameRes.getSkin());
		b.add(new Label("Back",labelStyle));
		b.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.show("Previous menu");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.hide();
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				hide();
				return true;
			}
		});
		functionTable.add(b).bottom().width(width * 0.2f).height(height * 0.1f);
		
		load = GuiUtils.createButton("gui/button", null, gameRes.getSkin());
		load.add(new Label("Load",labelStyle));
		
		background.setVisible(false);
		functionTable.setVisible(false);
		addActor(background);
		addActor(functionTable);
		
		gameRes.getStage().addActor(this);
	}
	public void show(boolean loading){
		activeButton.clearActor();
		
		if(loading){
			activeButton.setActor(load);
		}else{
			activeButton.setActor(save);
		}
		
		position(getStage());
		background.setVisible(true);
		functionTable.setVisible(true);
	}
	public void  hide(){
		field.setText("");
		folderList.clearItems();
		background.setVisible(false);
		functionTable.setVisible(false);
	}
	private void position(Stage stage){
		background.setPosition(stage.getViewport().getScreenX() + (stage.getViewport().getScreenWidth() / 2) - (background.getWidth() * 1.5f), 
				stage.getViewport().getScreenY() + (stage.getViewport().getScreenHeight() / 2) - (background.getHeight() / 2));
		functionTable.setPosition(stage.getViewport().getScreenX() + (stage.getViewport().getScreenWidth() / 2) - (functionTable.getWidth() * 1.5f), 
				stage.getViewport().getScreenY() + (stage.getViewport().getScreenHeight() / 2) - (functionTable.getHeight() / 2));
	}
	protected class OverwriteDialog extends BasicWindow{

		public OverwriteDialog(final GameServices gameRes, float width, float height) {
			super("Overwrite existing save?", (int)width, (int)height, gameRes.getFont(), gameRes.getSkin(), gameRes.getStage());
			setName("overWrite");
			
			getTitle().clearListeners();
			
			LabelStyle labelStyle = new LabelStyle(gameRes.getFont(), Color.WHITE);
			Button b = GuiUtils.createButton("gui/button", null, gameRes.getSkin());
			b.add(new Label("Save",labelStyle));
			b.addListener(new ClickListener() {
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					gameRes.getMap().saveGameState(filePath+field.getText());
					hideWindow();
					return true;
				}
			});
			root.add(b);
			
			b = GuiUtils.createButton("gui/button", null, gameRes.getSkin());
			b.add(new Label("Cancel",labelStyle));
			b.addListener(new ClickListener() {
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					hideWindow();
					return true;
				}
			});
			root.add(b);
		}
	}
	
}
