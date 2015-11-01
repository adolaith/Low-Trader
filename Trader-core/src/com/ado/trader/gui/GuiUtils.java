package com.ado.trader.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class GuiUtils {

	public static ImageButton createImageButton(String imgUp, String imgDown, String backgroundUp, String backgroundDown, Skin skin){
		if(imgDown == null && backgroundDown == null){
			return new ImageButton(setImgButtonStyle(skin.newDrawable(imgUp), null, 
					skin.newDrawable(backgroundUp), null));
		}
		return new ImageButton(setImgButtonStyle(skin.newDrawable(imgUp), skin.newDrawable(imgDown), 
				skin.newDrawable(backgroundUp), skin.newDrawable(backgroundDown)));
	}
	public static Button createButton(String styleUp, String styleDown, Skin skin){
		return new Button(setButtonStyle(styleUp, styleDown, skin));
	}
	public static ButtonStyle setButtonStyle(String styleUp, String styleDown, Skin skin){
		ButtonStyle style = new ButtonStyle();
		style.up = skin.newDrawable(styleUp);
		if(styleDown!=null){
			style.down = skin.newDrawable(styleDown);
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
		imgStyle.imageUp = skin.newDrawable(imgUp);
		imgStyle.up = skin.newDrawable(styleUp);
		if(styleDown!=null)	imgStyle.down = skin.newDrawable(imgDown);
		if(styleDown!=null)	imgStyle.down = skin.newDrawable(styleDown);
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
	public static ScrollPane createScrollTable(Skin skin){
		ScrollPaneStyle spS = new ScrollPaneStyle();
		spS.vScroll = skin.newDrawable("gui/scrollBar");
		spS.vScrollKnob = skin.newDrawable("gui/scrollBar");
		spS.hScroll = skin.newDrawable("gui/scrollBar");
		spS.hScrollKnob = skin.newDrawable("gui/scrollBar");
		
		ScrollPane pane = new ScrollPane(null, spS);
		pane.setScrollingDisabled(true, false);
		pane.setScrollBarPositions(false, true);
		
		return pane;
	}
	public static SelectBox<String> createSelectBox(Skin skin, BitmapFont font){
		ScrollPaneStyle styleScroll = new ScrollPaneStyle();
		styleScroll.vScroll = skin.getDrawable("gui/scrollBar");
		styleScroll.vScrollKnob = skin.getDrawable("gui/scrollBar");
		
		ListStyle styleList = new ListStyle(font, Color.BLUE, Color.GRAY, 
				skin.getDrawable("gui/tooltip"));
		styleList.background = skin.getDrawable("gui/tooltip");
		
		SelectBoxStyle styleSelect = new SelectBoxStyle(font, Color.BLACK,
				skin.getDrawable("gui/tooltip"), styleScroll, styleList);
		
		NinePatch n = new NinePatch(skin.getPatch("gui/tooltip"));
		n.setColor(new Color(n.getColor().r, n.getColor().g, n.getColor().b, 0.6f));
		styleSelect.backgroundDisabled = new NinePatchDrawable(n);
		
		SelectBox<String> box = new SelectBox<String>(styleSelect);
		box.setMaxListCount(6);
		return box;
	}
}
