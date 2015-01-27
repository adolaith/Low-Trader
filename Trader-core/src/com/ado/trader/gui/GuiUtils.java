package com.ado.trader.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class GuiUtils {

	public static ImageButton createImageButton(String imgUp, String imgDown, String backgroundUp, String backgroundDown, Skin skin){
		if(imgDown == null && backgroundDown == null){
			return new ImageButton(setImgButtonStyle(skin.getDrawable(imgUp), null, 
					skin.getDrawable(backgroundUp), null));
		}
		return new ImageButton(setImgButtonStyle(skin.getDrawable(imgUp), skin.getDrawable(imgDown), 
				skin.getDrawable(backgroundUp), skin.getDrawable(backgroundDown)));
	}
	public static Button createButton(String styleUp, String styleDown, Skin skin){
		return new Button(setButtonStyle(styleUp, styleDown, skin));
	}
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
