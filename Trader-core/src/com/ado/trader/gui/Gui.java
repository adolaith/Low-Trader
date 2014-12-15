package com.ado.trader.gui;

import com.ado.trader.screens.GameScreen;
import com.ado.trader.systems.GameTime;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

//Gui is anchored to the camera
public class Gui {
	Stage stage;
	
	Skin skin;
	BitmapFont font;

	GameScreen game;
	ControlArea controlArea;
	BuildMenu buildMenu;
	ToolTip toolTip;
	ZoneDialog zoneDialog;
	CreateNpcWindow entityCustom;
	RightClickMenu rightClickMenu;
	
	InformationWindow infoWindow;
	NewsWindow newsWindow;
	FarmWindow farmWindow;
	HomeWindow homeWindow;
	ItemWindow itemWindow;
	NpcInfoWindow npcWindow;
	ContainerWindow containerWindow;
	WorkZoneWindow workWindow;
	
	public Gui(GameScreen game) {
		this.game = game;
		stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),game.getRenderer().getCamera()));
		font = game.getRenderer().getFont();
		skin = new Skin();
		skin.addRegions(game.getAtlas());
		
		buildMenu = new BuildMenu(this);
		controlArea = new ControlArea(this);

		zoneDialog = new ZoneDialog(this);
		entityCustom = new CreateNpcWindow(this);
		toolTip = new ToolTip(this);
		
		rightClickMenu = new RightClickMenu(this);
		infoWindow = new InformationWindow(this);
		newsWindow = new NewsWindow(this);
		farmWindow = new FarmWindow(this);
		homeWindow = new HomeWindow(this);
		itemWindow = new ItemWindow(this);
		npcWindow = new NpcInfoWindow(this);
		containerWindow = new ContainerWindow(this);
		workWindow = new WorkZoneWindow(this);
	}
	public void update(float x, float y){
		toolTip.updateToolTip();
		controlArea.update(game.getWorld().getSystem(GameTime.class), game.getRenderer().getCamera());
		infoWindow.update(game);
		infoWindow.updatePosition(x, y);
		newsWindow.updatePosition(x, y);
		rightClickMenu.checkBounds(game.getInput());
	}
	public boolean rightClickAction(){
		if(buildMenu.getBuildMenu().get("mainmenu").isVisible()){buildMenu.rightClickVis(false);}	//hides build menu
		rightClickMenu.showMenu(game.getInput().getIsoClicked().x, game.getInput().getIsoClicked().y, game.getInput().getMapClicked(), game.getMap().getCurrentLayerGroup());
		return false;
	}
	public void resize(int width, int height){
		stage.getViewport().update(width, height);
	}
	public ZoneDialog getZoneDialog() {
		return zoneDialog;
	}
	public Stage getStage() {
		return stage;
	}
	public ControlArea getNoticeArea() {
		return controlArea;
	}
	public BuildMenu getrClickMenu() {
		return buildMenu;
	}
	public CreateNpcWindow getEntityCustom() {
		return entityCustom;
	}
	public NewsWindow getNewsWindow() {
		return newsWindow;
	}
	public ContainerWindow getContainerWindow() {
		return containerWindow;
	}
	public WorkZoneWindow getWorkWindow() {
		return workWindow;
	}
	public Skin getSkin(){
		return skin;
	}
	public void dispose(){
		skin.dispose();
		font.dispose();
	}
}
