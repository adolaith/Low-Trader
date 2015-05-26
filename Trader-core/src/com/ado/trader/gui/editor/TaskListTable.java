package com.ado.trader.gui.editor;

import com.ado.trader.gui.GuiUtils;
import com.ado.trader.gui.ToolTip;
import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.JsonValue;

public class TaskListTable extends ScrollPane {
	ArrayMap<String, Table> tables;
	AiEntry parent;
	Skin skin;
	Table buttonMenu;
	BitmapFont font;
	Array<Button> listBtns;
	
	public TaskListTable(JsonValue aiConfig, GameServices gameRes) {
		super(null);
		this.skin = gameRes.getSkin();
		this.font = gameRes.getFont();
		tables = new ArrayMap<String, Table>();
		
		ScrollPaneStyle spS = new ScrollPaneStyle();
		spS.vScroll = skin.getDrawable("gui/scrollBar");
		spS.vScrollKnob = skin.getDrawable("gui/scrollBar");
		
		setStyle(spS);
		setScrollingDisabled(false, false);
//		setScrollBarPositions(true, true);
		setOverscroll(false, false);
		
		tables.put("p", new Table().top().left());
		tables.put("d", new Table());
		tables.put("l", new Table());
		
		setWidget(tables.get("p"));
		setVisible(false);
		
		final ToolTip toolTip = (ToolTip)(gameRes.getStage().getRoot().findActor("tooltip"));
		
		LabelStyle style = new LabelStyle(font, Color.BLACK);
		style.background = skin.getDrawable("gui/button");
		
		for(JsonValue t = aiConfig.child(); t != null; t = t.next()){
			for(JsonValue v = t.child(); v != null; v = v.next()){
				Label l = createLabel(v, t.name, toolTip, style, gameRes);
				
				switch(t.name){
				case "parents":
					tables.get("p").add(l).left().top().row();
					break;
				case "decorations":
					tables.get("d").add(l).left().top().row();
					break;
				case "leafs":
					tables.get("l").add(l).left().top().row();
					break;
				}
			}
			
		}
		
		buttonMenu = createButtonTable(toolTip, gameRes.getStage(), skin);
		layout();
	}
	
	private Table createButtonTable(final ToolTip toolTip, Stage stage, Skin skin){
		final Table t = new Table();
		final Table btns = new Table();
		int tableW = 165;
		int btnW = 25;
		
		t.setVisible(false);
		t.top().left();
		btns.left().top().defaults().width(btnW).height(30);
		
		listBtns = new Array<Button>();
		
		//delete Task
		Button b = createButton("X", "Delete Task", toolTip, skin);
		listBtns.add(b);
		
		btns.add(b).left();
		
		//Decorate Task
		b = createButton("D", "Decorate Task", toolTip, skin);
		listBtns.add(b);		
		btns.add(b).left();
		
		//create child Parent task
		b = createButton("P", "Create child Parent task", toolTip, skin);
		listBtns.add(b);
		btns.add(b).left();

		//create child leaf task
		b = createButton("L", "Create child Leaf task", toolTip, skin);
		listBtns.add(b);
		btns.add(b).left();

		
		t.add(btns).left();
		t.row();
		t.add(this).width(tableW).height(120).fill().expand();
		
		t.setWidth(tableW);
		
		t.setHeight(150);
		
		stage.addActor(t);
		t.layout();
		
		stage.addListener(new ClickListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				
				if(t.isVisible()){
					if(isVisible()){
						if(x < t.getX() - 4 || x > t.getRight() + 4 || y < t.getY() - 4 || y > t.getTop() + 4){
							hide();
							return true;
						}
					}else{
						Vector2 pos = localToStageCoordinates(new Vector2(btns.getX(), btns.getY()));
						Vector2 size = localToStageCoordinates(new Vector2(btns.getRight(), btns.getTop()));
						if(x < pos.x - 10 || x > size.x + 10 || y < pos.y - 10 || y > size.y + 10){
							t.setVisible(false);
							return true;
						}
					}
				}
				
				return false;
			}
		});
		
		return t;
	}
	
	private Button createButton(final String name, final String tooltipText, final ToolTip toolTip, Skin skin){
		Button b = GuiUtils.createButton("gui/button", null, skin);
		LabelStyle lStyle = new LabelStyle(font, Color.BLACK);
		Label l = new Label(name, lStyle);
		b.add(l);

		b.addListener(new ClickListener(){
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.show(tooltipText);
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.hide();
			}
			public void clicked (InputEvent event, float x, float y) {
				if(name.toLowerCase().matches("x")){
					if(parent.parentNode != null){
						parent.parentNode.removeEntry(parent);
						AiEditorWindow.refreshLayout();
						hide();
					}
					return;
				}
				
				if(tables.getKey((Table)getWidget(), true).matches(name.toLowerCase()) && isVisible()){
					setVisible(false);
				}else{
					setWidget(tables.get(name.toLowerCase()));
					setVisible(true);
					layout();
				}
			}
		});
		
		return b;
	}
	
	public void show(float x, float y, AiEntry parent){
		if(buttonMenu.isVisible()){
			buttonMenu.setVisible(false);
			return;
		}
		
		this.parent = parent;
		
		if(parent instanceof LeafEntry){
			listBtns.get(1).setVisible(true);
			listBtns.get(2).setVisible(false);
			listBtns.get(3).setVisible(false);
			buttonMenu.setPosition(x - listBtns.first().getWidth() * 2, y - buttonMenu.getHeight());
		}else if(parent instanceof ParentEntry){
			listBtns.get(1).setVisible(true);
			listBtns.get(2).setVisible(true);
			listBtns.get(3).setVisible(true);
			buttonMenu.setPosition(x - buttonMenu.getCells().first().getActorWidth(), y - buttonMenu.getHeight());
		}else if(parent instanceof DecorationEntry){
			listBtns.get(1).setVisible(false);
			listBtns.get(2).setVisible(false);
			listBtns.get(3).setVisible(false);
			buttonMenu.setPosition(x - listBtns.first().getWidth(), y - buttonMenu.getHeight());
		}
		
		
		
		buttonMenu.setVisible(true);
	}
	public void hide(){
		buttonMenu.setVisible(false);
		setVisible(false);
	}
	
	private Label createLabel(final JsonValue child, final String type, final ToolTip toolTip, final LabelStyle style, final GameServices gameRes){
		Label l = new Label(child.name, style);
		
		l.addListener(new ClickListener() {
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.show(child.getString("desc"));
			}
			public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				toolTip.hide();
			}
			public void clicked (InputEvent event, float x, float y) {
				if(type.matches("decorations")){
					parent.addDecoration(new DecorationEntry(child, false, parent, gameRes));
				}else if(type.matches("parents")){
					((ParentEntry)parent).addChildTask(new ParentEntry(child, false, parent, gameRes));
				}else if(type.matches("leafs")){
					((ParentEntry)parent).addChildTask(new LeafEntry(child, false, parent, gameRes));
				}
				AiEditorWindow.refreshLayout();
				
				hide();
			}
		});
		
		return l;
	}
}
