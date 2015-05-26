package com.ado.trader.gui.editor;

import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.JsonValue;

public class DecorationEntry extends AiEntry {

	public DecorationEntry(JsonValue data, boolean isLoaded, AiEntry parent, final GameServices gameRes) {
		super(data, isLoaded, parent, gameRes);
		init(gameRes);
	}
	private void init(final GameServices gameRes){
		NinePatch p = new NinePatch(gameRes.getSkin().getPatch("gui/tooltip"));
		p.setColor(new Color(0, 0.3f, 0, 0.5f));
		NinePatchDrawable bg = new NinePatchDrawable(p);
		setBackground(bg);
		
		final AiEntry e = this;
		button.addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				Vector2 v = gameRes.getStage().screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
				AiEditorWindow.tasksTable.show(v.x , v.y, e);
				
				return false;
			}
		});
	}

}
