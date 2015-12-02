package com.ado.trader.gui;

import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class SaveLoadMenu extends BasicWindow {
	protected List<String> folderList;
	protected OrthographicCamera cam;
	protected LabelStyle labelStyle;
	protected TextField field;
	protected String externalPath, internalPath;
	protected Button save, load, delete;
	protected Cell<Button> activeButton;

	public SaveLoadMenu(final GameServices gameRes, String externalPath, int width, int height) {
		super("Menu", width, height, gameRes.getFont(), gameRes.getSkin(), gameRes.getStage());
		this.externalPath = externalPath;
		
		init(gameRes);
	}
	public SaveLoadMenu(final GameServices gameRes, String externalPath, String internalPath, int width, int height) {
		super("Menu", width, height, gameRes.getFont(), gameRes.getSkin(), gameRes.getStage());
		
		this.externalPath = externalPath;
		this.internalPath = internalPath;
		
		init(gameRes);
	}
	private void init(final GameServices gameRes){
		cam = gameRes.getCam();
		
		new OverwriteDialog(gameRes, width, height * 0.2f);
		
		//empty list 
		ListStyle listStyle = new ListStyle();
		listStyle.font = gameRes.getFont();
		listStyle.fontColorSelected = Color.BLACK;
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
		
		//scrollpane with list
		ScrollPaneStyle spS = new ScrollPaneStyle();
		spS.vScroll = gameRes.getSkin().getDrawable("gui/scrollBar");
		spS.vScrollKnob = gameRes.getSkin().getDrawable("gui/scrollBar");
		
		ScrollPane sP = new ScrollPane(folderList, spS);
		sP.setScrollingDisabled(true, false);
		sP.setScrollBarPositions(false, true);
		
		body.add(sP).colspan(3).pad(4).width(width * 0.95f).height(height * 0.70f).row();
		
		//save name text field
		TextFieldStyle textStyle = new TextFieldStyle();
		textStyle.font = gameRes.getFont();
		textStyle.fontColor = Color.BLACK;
		textStyle.background = gameRes.getSkin().getDrawable("gui/tooltip");
		field = new TextField("", textStyle);
		field.setWidth(width * 0.95f);
		
		body.add(field).padTop(-6).colspan(3).width(width * 0.95f).height(height * 0.1f).row();
		
		//save button
		labelStyle = new LabelStyle(gameRes.getFont(), Color.WHITE);
		save = GuiUtils.createButton("gui/button", null, gameRes.getSkin());
		save.add(new Label("Save",labelStyle));
		
		//cell contains either save or load button
		activeButton = body.add(save).bottom().width(width * 0.2f).height(height * 0.1f);
		
		//delete save file
		delete = GuiUtils.createButton("gui/button", null, gameRes.getSkin());
		delete.add(new Label("Delete",labelStyle));
		delete.addListener(new ClickListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if(folderList.getSelected() != null){
					if(!folderList.getSelected().startsWith("*")){
						FileHandle file = Gdx.files.external(externalPath + folderList.getSelected());
						file.delete();
						
						if(activeButton.getActor() == save){
							populateList(false);	
						}else{
							populateList(true);
						}
					}
				}
				return true;
			}
		});

		body.add(delete).bottom().width(width * 0.2f).height(height * 0.1f);
		
		//load button
		load = GuiUtils.createButton("gui/button", null, gameRes.getSkin());
		load.add(new Label("Load",labelStyle));
		
	}
	protected void populateList(boolean loading){}
	
	public void showWindow(float x, float y, boolean loading){
		super.showWindow(x, y);
		
		activeButton.clearActor();
		
		if(loading){
			activeButton.setActor(load);
		}else{
			activeButton.setActor(save);
		}
	}
	@Override
	public void hideWindow(){
		super.hideWindow();
		
		field.setText("");
		folderList.clearItems();
	}
	//splashes 'game saved' label across screen before fading
	protected void saveAlert(String text){
		Label l = new Label(text, labelStyle);
		l.setFontScale(2.4f);
		l.setWidth(200);
		l.setHeight(40);
		getStage().addActor(l);
		l.toFront();
		l.setPosition((Gdx.graphics.getWidth() / 3) - (l.getWidth()), Gdx.graphics.getHeight() / 2);
		l.addAction(Actions.sequence(Actions.alpha(0, 3), Actions.removeActor()));
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
//					save(externalPath+field.getText());
					hideWindow();
					return true;
				}
			});
			body.add(b).padRight(3);
			
			b = GuiUtils.createButton("gui/button", null, gameRes.getSkin());
			b.add(new Label("Cancel",labelStyle));
			b.addListener(new ClickListener() {
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					hideWindow();
					return true;
				}
			});
			body.add(b);
		}
		
	}
}
