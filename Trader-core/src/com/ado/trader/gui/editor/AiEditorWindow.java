package com.ado.trader.gui.editor;

import com.ado.trader.gui.BasicWindow;
import com.ado.trader.gui.GuiUtils;
import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

//uses ai component descriptions from the file 'aiConfig' to generate an entity ai profile file 
public class AiEditorWindow extends BasicWindow {
	JsonValue aiConfig;
	static TaskListTable tasksTable;
	static ParentEntry rootNode;
	static Table bodyTable;
	AiProfileLoader loader;

	public AiEditorWindow(GameServices gameRes) {
		super("Ai Profile Editor", 500, 400, gameRes.getFont(), gameRes.getSkin(), gameRes.getStage());
		
		Json json = new Json();
		aiConfig = json.fromJson(null, Gdx.files.internal("data/ai/aiConfig"));
		
		loader = new AiProfileLoader(gameRes, this);
		
		tasksTable = new TaskListTable(aiConfig, gameRes);
		
		//menu bar
		Table menu = createMenu(gameRes);
		 
		body.add(menu).width(480).height(25).row();
		
		//body
		ScrollPane pane = GuiUtils.createScrollTable(gameRes.getSkin());
		bodyTable = new Table();
		bodyTable.top().left();
		bodyTable.defaults().top().left();
		pane.setWidget(bodyTable);
		pane.setOverscroll(false, false);
		pane.setFadeScrollBars(false);
		pane.setScrollBarPositions(true, true);
		pane.setScrollingDisabled(false, false);
		body.add(pane).fill().expand();
		
	}
	
	@Override
	public void act(float delta){
		super.act(delta);
		
	}
	
	private Table createMenu(final GameServices gameRes){
		Table t = new Table();
		t.left();
		
		Button b = GuiUtils.createButton("gui/button", null, gameRes.getSkin());
		
		LabelStyle lStyle = new LabelStyle(gameRes.getFont(), Color.WHITE);
		Label l = new Label("New Profile", lStyle);
		b.add(l);
		b.addListener(new ClickListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				bodyTable.clearChildren();
				
				//initial node
				rootNode = new ParentEntry(aiConfig.get("parents").get("Selector"), false, null, gameRes);
				rootNode.paramField.setText("root");
				rootNode.paramField.setDisabled(true);
				
				rootNode.addDecoration(new DecorationEntry(aiConfig.get("decorations").get("ResetDecorator"), false, rootNode, gameRes));
				
				bodyTable.add(rootNode).row();
				
				refreshLayout();
				return true;
			}
		});
		
		t.add(b);
		
		b = new Button(b.getStyle());
		l = new Label("Load Profile", lStyle);
		b.add(l);
		b.addListener(new ClickListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				loader.showWindow(getX() + loader.getWidth() / 8, getY(), true);
				return true;
			}
		});

		t.add(b).padLeft(2);

		b = new Button(b.getStyle());
		l = new Label("Save Profile", lStyle);
		b.add(l);
		b.addListener(new ClickListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				loader.showWindow(getX() + loader.getWidth() / 8, getY(), false);
				return true;
			}
		});
		
		t.add(b).padLeft(2);
		
		return t;
	}
	public void hideWindow(){
		super.hideWindow();
		tasksTable.hide();
	}

	//call when task is expanded, minimized, added or removed 
	public static void refreshLayout(){
		bodyTable.clearChildren();
		
		int indentCount = 0;
		
		layoutEntry(indentCount, rootNode);
	}
	private static void layoutEntry(int indentCount, AiEntry entry){
		int leftPad = indentCount * 25;
		
		for(int x = 0; x < entry.decorations.size; x++){
			AiEntry d = entry.decorations.get(x);
			if(d == null){
				break;
			}else{
				bodyTable.add(d).padLeft(leftPad).row();
			}
		}
		bodyTable.add(entry).padLeft(leftPad).row();
		
		indentCount++;
		
		if(entry instanceof ParentEntry){
			ParentEntry p = (ParentEntry)entry;
			if(p.isExpanded){
				if(p.childTasks == null){
					return;
				}
				for(int x = 0; x < p.childTasks.size; x++){
					AiEntry c = p.childTasks.get(x);
					if(c == null){
						break;
					}else{
						layoutEntry(indentCount, c);
					}
				}
			}
		}
	}
	
	
}
