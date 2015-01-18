package com.ado.trader.gui;

import com.ado.trader.input.InputHandler;
import com.artemis.World;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

//Gui is anchored to the camera
public class GameGui {
	GameServices gameRes;

	ControlArea controlArea;
//	BuildMenu buildMenu;
	ToolTip toolTip;
	RightClickMenu rightClickMenu;
	
	public GameGui(GameServices gameRes) {
		this.gameRes = gameRes;
//		buildMenu = new BuildMenu(this);
		controlArea = new ControlArea(gameRes);

		toolTip = new ToolTip(gameRes);
		
		rightClickMenu = new RightClickMenu(gameRes);
	}
	
	public void update(){
		toolTip.updateToolTip();
		controlArea.update(gameRes.getCam().position.x, gameRes.getCam().position.y, gameRes.getWorld());
		rightClickMenu.update();
	}
	
	public boolean rightClickAction(){
//		if(buildMenu.getBuildMenu().get("mainmenu").isVisible()){buildMenu.rightClickVis(false);}	//hides build menu
		
		rightClickMenu.showMenu(InputHandler.getIsoClicked().x, InputHandler.getIsoClicked().y, InputHandler.getMapClicked(), gameRes.getMap());
		return false;
	}
	
	public void resize(int width, int height){
		gameRes.getStage().getViewport().update(width, height);
	}
	public ControlArea getNoticeArea() {
		return controlArea;
	}
//	public BuildMenu getrClickMenu() {
//		return buildMenu;
//	}
	
	//move all gui code elsewhere
		public static ButtonStyle setButtonStyle(String styleUp, String styleDown, Skin skin){
			ButtonStyle style = new ButtonStyle();
			style.up = skin.getDrawable(styleUp);
			if(styleDown!=null){
				style.down = skin.getDrawable(styleDown);
			}
			style.unpressedOffsetX = 2f;
			style.pressedOffsetX = style.unpressedOffsetX + 1f;
			style.pressedOffsetY = -1f;
			return style;
		}
		public static ButtonStyle setButtonStyle(Drawable styleUp, Drawable styleDown){
			ButtonStyle style = new ButtonStyle();
			style.up = styleUp;
			if(styleDown!=null){
				style.down = styleDown;
			}
			style.unpressedOffsetX = 2f;
			style.pressedOffsetX = style.unpressedOffsetX + 1f;
			style.pressedOffsetY = -1f;
			return style;
		}
		public static ImageButtonStyle setImgButtonStyle(String imgUp, String imgDown, String styleUp, String styleDown, Skin skin){
			ImageButtonStyle imgStyle = new ImageButtonStyle();
			imgStyle.imageUp = skin.getDrawable(imgUp);
			imgStyle.up = skin.getDrawable(styleUp);
			if(styleDown!=null)	imgStyle.down = skin.getDrawable(imgDown);
			if(styleDown!=null)	imgStyle.down = skin.getDrawable(styleDown);
			imgStyle.unpressedOffsetX = 0f;
			imgStyle.pressedOffsetX = imgStyle.unpressedOffsetX + 1f;
			imgStyle.pressedOffsetY = -1f;
			return imgStyle;
		}
		public static ImageButtonStyle setImgButtonStyle(Drawable imgUp, Drawable imgDown, Drawable styleUp, Drawable styleDown){
			ImageButtonStyle imgStyle = new ImageButtonStyle();
			imgStyle.imageUp = imgUp;
			imgStyle.up = styleUp;
			if(styleDown!=null)	imgStyle.down = imgDown;
			if(styleDown!=null)	imgStyle.down = styleDown;
			imgStyle.unpressedOffsetX = 0f;
			imgStyle.pressedOffsetX = imgStyle.unpressedOffsetX + 1f;
			imgStyle.pressedOffsetY = -1f;
			return imgStyle;
		}
}
