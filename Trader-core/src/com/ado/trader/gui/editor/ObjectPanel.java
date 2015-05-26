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

public class ObjectPanel extends Table {
	ObjectMenu objectMenus;

	public ObjectPanel(final GameServices gameRes, final MapEditorPanel panel) {
		setWidth(38);
		setHeight(6 * 36);
		defaults().center().width(30).height(30).pad(2);
		
		objectMenus = new ObjectMenu(gameRes);
		
		createButton("zoneTile", "Tile menu", "tileMenu", gameRes);
		createButton("wallIcon", "Wall menu", "wallMenu", gameRes);
		createButton("entityIcon", "Entity menu", "entityMenu", gameRes);
		createButton("workIcon", "Item menu", "itemsMenu", gameRes);
		
		final ToolTip toolTip = (ToolTip)(gameRes.getStage().getRoot().findActor("tooltip"));
		//delete button
		ImageButton deleteButton = GuiUtils.createImageButton("gui/trashcanIcon", null, "gui/button", null, gameRes.getSkin());
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
		
		ImageButton backButton = GuiUtils.createImageButton("gui/arrowPlay", null, "gui/button", null, gameRes.getSkin());
		
		backButton.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.show("Back to main panel");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.hide();
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				panel.setChildTable("main");
				
				return true;
			}
		});
		add(backButton).row();
	}

	private void createButton(String icon, final String tooltip, final String menuName, final GameServices gameRes){
		ImageButton b = GuiUtils.createImageButton("gui/" +icon, null, "gui/button", null, gameRes.getSkin());
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
					objectMenus.setCurrentTable(t);
					objectMenus.getTitle().setText(tooltip);
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
