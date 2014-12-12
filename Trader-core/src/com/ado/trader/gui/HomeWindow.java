package com.ado.trader.gui;

import com.ado.trader.entities.components.Type;
import com.ado.trader.map.HomeZone;
import com.ado.trader.screens.GameScreen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class HomeWindow extends BasicWindow {

	public HomeWindow(Gui gui) {
		super("Home", 265, 132, gui);
		root.defaults().left();
	}
	public void setupWindow(final HomeZone z, final GameScreen game){
		root.clearChildren();
		BitmapFont font = game.getGui().font;
		Skin skin = game.getGui().skin;
		
		//Max number label
		LabelStyle ls = new LabelStyle(font, Color.WHITE);
		Label l = new Label("Max occupants: ", ls);
		root.add(l).width(l.getText().length*8+3);
		
		Table container = new Table();
		l = new Label(""+z.maxOccupants, ls);
		container.add(l).width(32);
		
		//increase max occupants buttons
		TextButtonStyle bStyle = new TextButtonStyle();
		bStyle.font = font;
		bStyle.up = skin.getDrawable("gui/button");
		TextButton b = new TextButton("-", bStyle);
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				z.maxOccupants--;
				if(z.maxOccupants < 0){
					z.maxOccupants = 0;
				}
				setupWindow(z, game);
			}
		});
		container.add(b).width(18);
		b = new TextButton("+", bStyle);
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				z.maxOccupants++;
				setupWindow(z, game);
			}
		});
		container.add(b).width(18);
		root.add(container).right().row();
		
		//Lists occupants(type listed)
		getOccupants(font, skin, z, game);
		
		//home's farm zone label
		addLabelPair("Garden: ", ""+z.garden, font);
		root.add();
		
		b = new TextButton("Set garden", bStyle);
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				//add garden to home list
			}
		});
		root.add(b).width(b.getText().length()*12).left();
	}
	private void getOccupants(BitmapFont font, Skin skin, final HomeZone z, final GameScreen game){
		LabelStyle ls = new LabelStyle(font, Color.WHITE);
		Label l = new Label("Occupants: ", ls);
		root.add(l);
		
		//select box for zone type goes here
		ScrollPaneStyle styleScroll = new ScrollPaneStyle();
		styleScroll.vScroll = skin.getDrawable("gui/scrollBar");
		styleScroll.vScrollKnob = skin.getDrawable("gui/scrollBar");
		
		ListStyle styleList = new ListStyle(font, Color.YELLOW, Color.GRAY, 
				skin.getDrawable("gui/guiBG"));
		styleList.background = skin.getDrawable("gui/guiBG");
		
		SelectBoxStyle styleSelect = new SelectBoxStyle(font, Color.BLACK,
				skin.getDrawable("gui/guiBG"), styleScroll, styleList);
		
		final SelectBox<Integer> box = new SelectBox<Integer>(styleSelect);
		Array<Integer> list = new Array<Integer>();
		for(int i: z.occupants){
			Type t = game.getWorld().getEntity(i).getComponent(Type.class);
			list.add(t.getTypeID());
		}
		box.setItems(list);
		
		root.add(box).width(root.getWidth()/2).height(24).padBottom(2).left().row();
		root.layout();
	}
	public void showWindow(float x, float y, HomeZone z, GameScreen game){
		super.showWindow(x, y);
		root.clearChildren();
		setupWindow(z, game);
	}
}
