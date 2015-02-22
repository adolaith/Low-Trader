package com.ado.trader.gui.editor;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.gui.ContextMenu;
import com.ado.trader.gui.CustomCursor;
import com.ado.trader.gui.GuiUtils;
import com.ado.trader.input.InputHandler;
import com.ado.trader.map.Map;
import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ArrayMap;

public class DeleteMenu extends ContextMenu {
	ArrayMap<String, Button> buttons;
	Vector2 mapClicked;
	Map map;
	public boolean listening;

	public DeleteMenu(GameServices gameRes) {
		super(gameRes);
		setName("deleteMenu");
		listening = false;
		map = gameRes.getMap();
		mapClicked = new Vector2();
		
		buttons = new ArrayMap<String, Button>();
		buttons.put("item", createButton("item", gameRes));
		buttons.put("entity", createButton("entity", gameRes));
		buttons.put("wall", createButton("wall", gameRes));
		
		gameRes.getStage().addActor(this);
	}
	
	private Button createButton(final String key, final GameServices gameRes){
		LabelStyle lStyle = new LabelStyle(gameRes.getFont(), Color.WHITE);
		Button b = GuiUtils.createButton("gui/button", null, gameRes.getSkin());
		b.add(new Label("Delete "+key,lStyle));
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				switch(key){
				case "item":
					gameRes.getMap().getItemLayer().deleteFromMap((int) mapClicked.x, (int) mapClicked.y, gameRes.getMap().currentLayer);
					break;
				case "entity":
					EntityFactory.deleteEntity((int) mapClicked.x, (int) mapClicked.y, gameRes.getMap().currentLayer, gameRes.getMap().getEntityLayer());
					break;
				case "wall":
					EntityFactory.deleteEntity((int) mapClicked.x, (int) mapClicked.y, gameRes.getMap().currentLayer, gameRes.getMap().getWallLayer());
					break;
				}
				if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)){
					CustomCursor icon = (CustomCursor)(getStage().getRoot().findActor("customCursor"));
					icon.show("trashcanIcon");
					listening = true;
					InputHandler.getMapClicked().setZero();
				}
				hide();
			}
		});
		return b;
	}
	@Override
	public void act(float delta){
		if(Gdx.input.isButtonPressed(Buttons.RIGHT)){
			if(isVisible()){
				hide();
			}else{
				listening = false;				
			}
		}
		if(listening && !InputHandler.getMapClicked().isZero()){
			mapClicked.set(InputHandler.getMapClicked());
			if(map.getEntityLayer().isOccupied((int) mapClicked.x, (int) mapClicked.y, map.currentLayer)){
				add(buttons.get("entity")).row();
			}
			if(map.getItemLayer().isOccupied((int) mapClicked.x, (int) mapClicked.y, map.currentLayer)){
				add(buttons.get("item")).row();
			}
			if(map.getWallLayer().isOccupied((int) mapClicked.x, (int) mapClicked.y, map.currentLayer)){
				add(buttons.get("wall")).row();
			}
			if(getRows() == 0){
				return;
			}
			int heightMargin = 8;
			setHeight(height * getRows() +heightMargin);
			super.show();
			
			listening = false;
			CustomCursor icon = (CustomCursor)(getStage().getRoot().findActor("customCursor"));
			icon.hide();
		}
		super.act(delta);
	}
	
}
