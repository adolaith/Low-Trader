package com.ado.trader.entities.AiComponents.base;

import com.ado.trader.GameMain;
import com.ado.trader.systems.AiSystem;
import com.ado.trader.utils.InputHandler;
import com.badlogic.gdx.Gdx;

/*
 * Original code by @author Ying
 * Modified
 */

public abstract class Task {
	protected String name;
	protected AiSystem aiSys;
	
	public Task(AiSystem aiSys){
		this.aiSys = aiSys;
	}
	public Task(AiSystem aiSys, String name){
		this.aiSys = aiSys;
		this.name = name;
	}
	public void LogTask(String text){
		if(InputHandler.DEBUG){
			Gdx.app.log(GameMain.LOG, "Task: "+ name +"; Entity: "+ aiSys.currentEntity.getId() +"; "+ text);
		}
	}
	
	public void start(){
		LogTask("Starting");
	}
	
	public abstract void end();
	
	public abstract void doTask();
	
	public abstract TaskController getControl();
	
	public abstract boolean checkConditions();

}
