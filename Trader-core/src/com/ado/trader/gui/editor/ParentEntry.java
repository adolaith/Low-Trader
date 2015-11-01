package com.ado.trader.gui.editor;

import com.ado.trader.gui.ToolTip;
import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;

public class ParentEntry extends AiEntry {
	Array<AiEntry> childTasks;
	
	boolean isExpanded;

	public ParentEntry(JsonValue data, boolean isLoaded, AiEntry parent, final GameServices gameRes) {
		super(data, isLoaded, parent, gameRes);
		init(gameRes);
	}
	private void init(final GameServices gameRes){
		decorations = new Array<DecorationEntry>();
		childTasks = new Array<AiEntry>();
		isExpanded = true;
		
		final ToolTip toolTip = (ToolTip)(gameRes.getStage().getRoot().findActor("tooltip"));
		
		//Click label to expand children
		label.addListener(new ClickListener(){
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.show("Expand children");
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.hide();
			}
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				isExpanded = !isExpanded;
				AiEditorWindow.refreshLayout();
				return true;
			}
		});
		
		NinePatch p = new NinePatch(gameRes.getSkin().getPatch("gui/tooltip"));
		NinePatchDrawable bg = new NinePatchDrawable(p);
		setBackground(bg);
		
		//button menu
		final AiEntry e = this;
		button.addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				Vector2 v = gameRes.getStage().screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
				AiEditorWindow.tasksTable.show(v.x , v.y, e);
				
				return false;
			}
		});
		
		//adjustment buttons
		if(parentNode != null){
			up.getParent().setVisible(true);

			up.addListener(new ClickListener(){
				public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
					toolTip.show("Move up");
				}
				public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
					toolTip.hide();
				}
				public void clicked (InputEvent event, float x, float y) {
					ParentEntry p = (ParentEntry) parentNode;
					int i = p.childTasks.indexOf(e, false); 

					if(i > 0){
						p.childTasks.swap(i--, i);
					}
					AiEditorWindow.refreshLayout();
				}
			});

			down.addListener(new ClickListener(){
				public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
					toolTip.show("Move down");
				}
				public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
					toolTip.hide();
				}
				public void clicked (InputEvent event, float x, float y) {
					ParentEntry p = (ParentEntry) parentNode;
					int i = p.childTasks.indexOf(e, false); 

					Gdx.app.log("ParentEntry:" , "i: "+i+". i++: "+i+1+"size: "+p.childTasks.size);
					if(i+1 < p.childTasks.size){
						p.childTasks.swap(i++, i);
					}
					AiEditorWindow.refreshLayout();
				}
			});
		}
	}
	public void addChildTask(AiEntry child){
		childTasks.add(child);
	}
	public void removeEntry(AiEntry child){
		if(child instanceof DecorationEntry){
			decorations.removeValue((DecorationEntry) child, false);
			return;
		}else{
			childTasks.removeValue(child, false);
		}
	}
}
