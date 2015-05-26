package com.ado.trader.gui.editor;

import com.ado.trader.gui.GuiUtils;
import com.ado.trader.gui.ToolTip;
import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MapEditorPanel extends Table{
	int stageWidth;
	ArrayMap<String, Table> tables;

	public MapEditorPanel(final GameServices gameRes) {
		setName("editorPanel");
		setVisible(true);
		setWidth(38);
		setHeight(6 * 36);
		setBackground(gameRes.getSkin().getDrawable("gui/bGround"));
		
		tables = new ArrayMap<String, Table>();
		
		new DeleteMenu(gameRes);
		new EditorMenu(gameRes);
		
		tables.put("main", createMainPanel(gameRes));
		tables.put("objects", new ObjectPanel(gameRes, this));

		add(tables.get("main"));
		
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
	
	//default table
	private Table createMainPanel(final GameServices gameRes){
		Table t = new Table();
		t.setWidth(38);
		t.setHeight(6 * 36);
		t.defaults().center().width(30).height(30).pad(2);
		
		final AiEditorWindow aiWin = new AiEditorWindow(gameRes);
		final ToolTip toolTip = (ToolTip)(gameRes.getStage().getRoot().findActor("tooltip"));
		
		ImageButton aiButton = GuiUtils.createImageButton("gui/iconImportant", null, "gui/button", null, gameRes.getSkin());
		aiButton.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.show("Ai editor");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.hide();
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				Viewport view = gameRes.getStage().getViewport();
				aiWin.showWindow((view.getScreenX() + view.getScreenWidth() / 2) - aiWin.getWidth() / 2,
						(view.getScreenY() + view.getScreenHeight() / 2) - aiWin.getHeight() / 2);
				Gdx.app.log("editorPanel: ", "w: "+ getWidth());
				return true;
			}
		});
		t.add(aiButton).row();
		
		ImageButton objectsButton = GuiUtils.createImageButton("gui/toolbox", null, "gui/button", null, gameRes.getSkin());
		objectsButton.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.show("Objects menu");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.hide();
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				setChildTable("objects");
				return true;
			}
		});
		t.add(objectsButton).row();
		
		//exit game button
		ImageButton exitButton = GuiUtils.createImageButton("gui/exitIcon", null, "gui/button", null, gameRes.getSkin());
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
		t.add(exitButton);
		
		return t;
	}
	
	public void setChildTable(String tableName){
		clearChildren();
		add(tables.get(tableName));
	}
}
