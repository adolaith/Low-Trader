package com.ado.trader.gui.editor;

import com.ado.trader.gui.CustomCursor;
import com.ado.trader.gui.GuiUtils;
import com.ado.trader.gui.ToolTip;
import com.ado.trader.input.InputHandler;
import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MapEditorPanel extends Table{
	ObjectMenu objectMenus;
	int stageWidth;

	public MapEditorPanel(final GameServices gameRes) {
		setName("editorPanel");
		objectMenus = new ObjectMenu(gameRes);
		
		setVisible(true);
		setWidth(38);
		setHeight(6 * 36);
		defaults().center().width(30).height(30).pad(2);
		setBackground(gameRes.getSkin().getDrawable("gui/bGround"));
		
		new DeleteMenu(gameRes);
		
		new EditorMenu(gameRes);
		
		createButton("zoneTile", "Tile menu", "tileMenu", gameRes);
		createButton("wallIcon", "Wall menu", "wallMenu", gameRes);
		createButton("entityIcon", "Entity menu", "entityMenu", gameRes);
		createButton("workIcon", "Item menu", "itemsMenu", gameRes);
		
		final ToolTip toolTip = (ToolTip)(gameRes.getStage().getRoot().findActor("tooltip"));
		//delete button
		final ImageButton deleteButton = GuiUtils.createImageButton("gui/trashcanIcon", null, "gui/button", null, gameRes.getSkin());
		deleteButton.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.show("Delete object");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.hide();
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				CustomCursor icon = (CustomCursor)(gameRes.getStage().getRoot().findActor("customCursor"));
				DeleteMenu menu = (DeleteMenu)(gameRes.getStage().getRoot().findActor("deleteMenu"));
				InputHandler.getMapClicked().setZero();
				if(icon.isVisible()){
					icon.hide();
					menu.listening = false;
				}else{
					icon.show("trashcanIcon");
					menu.listening = true;
				}
				
				return true;
			}
		});
		add(deleteButton).row();
		
		//exit game button
		final ImageButton exitButton = GuiUtils.createImageButton("gui/exitIcon", null, "gui/button", null, gameRes.getSkin());
		exitButton.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.show("Exit menu");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.hide();
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				EditorMenu menu = (EditorMenu) gameRes.getStage().getRoot().findActor("editorMenu");
				menu.show();
				return true;
			}
		});
		add(exitButton);
		
		Viewport view = gameRes.getStage().getViewport();
		stageWidth = (int) view.getScreenWidth();
		float x = view.getScreenX() + view.getScreenWidth() - getWidth() - 2;
		float y = view.getScreenY() + 2;
		
		setPosition(x, y);
		gameRes.getStage().addActor(this);
	}
	
	//UPDATE
	public void act(float delta){
		//screen has been resized
		if(getStage().getWidth() != stageWidth){
			stageWidth = (int) getStage().getWidth();

			float x = getStage().getCamera().position.x + (stageWidth / 2) - getWidth() - 2;
			
			setPosition(x, getY());
		}
		super.act(delta);
	}

	private void createButton(String icon, final String tooltip, final String menuName, final GameServices gameRes){
		final ImageButton b = GuiUtils.createImageButton("gui/" +icon, null, "gui/button", null, gameRes.getSkin());
		final ToolTip toolTip = (ToolTip)(gameRes.getStage().getRoot().findActor("tooltip"));
		
		b.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.show(tooltip);
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.hide();
			}
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				Table t = objectMenus.getTable(menuName);
				if(objectMenus.isVisible()){
					if(objectMenus.getCurrentTable() == t){
						objectMenus.hideWindow();
					}else{
						objectMenus.setCurrentTable(t);
						objectMenus.getTitle().setText(tooltip);
					}
				}else{
					objectMenus.setCurrentTable(t);
					objectMenus.getTitle().setText(tooltip);
					Viewport view = gameRes.getStage().getViewport();
					objectMenus.showWindow(view.getScreenX() + view.getScreenWidth() - getWidth() - objectMenus.getWidth() - 2, getY());
				}
				return true;
			}
		});
		add(b).row();
	}
	
}
