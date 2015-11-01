package com.ado.trader.gui.editor;

import com.ado.trader.gui.GuiUtils;
import com.ado.trader.gui.ToolTip;
import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;

public class AiEntry extends Table {
	Array<DecorationEntry> decorations;
	
	Label label;
	TextField paramField;
	
	ImageButton button, up, down;
	
	AiEntry parentNode;

	public AiEntry(JsonValue data, boolean isLoaded, AiEntry parent, final GameServices gameRes) {
//		setDebug(true);
		parentNode = parent;
		
		//Name label
		LabelStyle lStyle = new LabelStyle(gameRes.getFont(), Color.BLACK);
		label = new Label(data.name, lStyle);

		add(label).width(180);

		final ToolTip toolTip = (ToolTip)(gameRes.getStage().getRoot().findActor("tooltip"));
		
		if(data.has("param")){
			//Param entry text field
			TextFieldStyle fStyle = new TextFieldStyle();
			fStyle.background = gameRes.getSkin().getDrawable("gui/bGround");
			fStyle.font = gameRes.getFont();
			fStyle.fontColor = Color.WHITE;
			fStyle.focusedFontColor = Color.BLUE;

			paramField = new TextField("", fStyle);
			paramField.setBlinkTime(1f);
			paramField.setWidth(180);
			
			if(isLoaded){
				Gdx.app.log("AiEntry: ", "isLoaded: "+data.getString("param"));
				paramField.setText(data.getString("param"));
			}else{
				paramField.setMessageText(data.getString("param"));
			}
			
			paramField.addListener(new ClickListener(){
				public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
					toolTip.show("Enter required params seperated\n by a ','");
				}
				public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
					toolTip.hide();
				}
			});

			add(paramField).width(180);
		}
		
		Table t = new Table();
		
		up = GuiUtils.createImageButton("gui/arrowUp", null, "gui/button", null, gameRes.getSkin());
		t.add(up).height(up.getHeight() / 2).row();
		
		down = GuiUtils.createImageButton("gui/arrowDown", null, "gui/button", null, gameRes.getSkin());
		t.add(down).height(down.getHeight() / 2);
		
		add(t);
		t.setVisible(false);

		//click to show button menu
		Sprite s = new Sprite(gameRes.getSkin().getSprite("gui/arrowPlay"));
		s.flip(true, false);
		ImageButtonStyle imgStyle = GuiUtils.setImgButtonStyle(new SpriteDrawable(s), null, gameRes.getSkin().getDrawable("gui/button"), null);
		button = new ImageButton(imgStyle);

		add(button);
	}
	
	public void addDecoration(DecorationEntry deco){
		decorations.add(deco);
	}
	public void removeEntry(AiEntry child){
	}
	public String getName(){
		return label.getText().toString();
	}
}
