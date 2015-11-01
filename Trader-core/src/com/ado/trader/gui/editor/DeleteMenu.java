package com.ado.trader.gui.editor;

import com.ado.trader.entities.EntityFactory;
import com.ado.trader.entities.components.Name;
import com.ado.trader.gui.ContextMenu;
import com.ado.trader.gui.CustomCursor;
import com.ado.trader.gui.GuiUtils;
import com.ado.trader.input.InputHandler;
import com.ado.trader.map.Chunk;
import com.ado.trader.map.Map;
import com.ado.trader.utils.GameServices;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

@Wire
public class DeleteMenu extends ContextMenu {
	ComponentMapper<Name> nameMap;
	Vector2 tileClicked;
	Chunk chunkClicked;
	Map map;
	public boolean listening;
	BitmapFont font;
	Skin skin;

	public DeleteMenu(GameServices gameRes) {
		super(gameRes);
		setName("deleteMenu");
		listening = false;
		map = gameRes.getMap();
		font = gameRes.getFont();
		skin = gameRes.getSkin();
		
		tileClicked = new Vector2();
		
		nameMap = gameRes.getWorld().getMapper(Name.class);
		
		Group layer = gameRes.getStage().getRoot().findActor("guiLayer");
		layer.addActor(this);
	}
	
	private Button createButton(final String key, final String name){
		LabelStyle lStyle = new LabelStyle(font, Color.WHITE);
		Button b = GuiUtils.createButton("gui/button", null, skin);
		b.add(new Label("Delete "+key,lStyle));
		b.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				switch(key){
				case "item":
					ItemFactory.deleteItem(chunkClicked, (int) tileClicked.x, (int) tileClicked.y, name);
					break;
				case "entity":
					EntityFactory.deleteEntity(chunkClicked, (int) tileClicked.x, (int) tileClicked.y, name);
					break;
				case "wall":
					EntityFactory.deleteWall(chunkClicked, (int) tileClicked.x, (int) tileClicked.y);
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
			chunkClicked = map.getChunk((int) InputHandler.getMapClicked().x, (int) InputHandler.getMapClicked().y);
			tileClicked = map.worldVecToTile((int) InputHandler.getMapClicked().x, (int) InputHandler.getMapClicked().y);

			//entity buttons
			for(int c = 0; c < chunkClicked.getEntities().map[(int) tileClicked.x][(int) tileClicked.y].length; c++){
				if(chunkClicked.getEntities().map[(int) tileClicked.x][(int) tileClicked.y][c] == null){
					continue;
				}
				Entity e = map.getWorld().getEntity(chunkClicked.getEntities().map[(int) tileClicked.x][(int) tileClicked.y][c]);
				add(createButton("entity", nameMap.get(e).getName())).row();
			}
			
			//item buttons
			for(int c = 0; c < chunkClicked.getEntities().map[(int) tileClicked.x][(int) tileClicked.y].length; c++){
				if(chunkClicked.getItems().map[(int) tileClicked.x][(int) tileClicked.y][c] == null){
					continue;
				}
				Entity e = map.getWorld().getEntity(chunkClicked.getItems().map[(int) tileClicked.x][(int) tileClicked.y][c]);
				add(createButton("item", nameMap.get(e).getName())).row();
			}
			
			//wall buttons
			if(chunkClicked.getWalls().map[(int) tileClicked.x][(int) tileClicked.y] != null){
				Entity e = map.getWorld().getEntity(chunkClicked.getWalls().map[(int) tileClicked.x][(int) tileClicked.y]);
				add(createButton("wall", nameMap.get(e).getName())).row();
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
