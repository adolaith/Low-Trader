package com.ado.trader.entities.AiComponents.base;

import com.ado.trader.GameMain;
import com.ado.trader.systems.AiSystem;
import com.badlogic.gdx.Gdx;

/**
 * This parent task selects one of it's children to update.
 * 
 * To select a child, it starts from the beginning of it's children vector
 * and goes one by one until it finds one that passes the CheckCondition test.
 * It then updates that child until it finished.
 * If the child finishes with failure, it continues down the list looking another
 * candidate to update, and if it doesn't find it, it finishes with failure. 
 * If the child finishes with success, the Selector considers it's task done and 
 * bails with success. 
 * Original code by @author Ying
 * Modified
 */
public class Selector extends ParentTask{
	/**
	 * Creates a new instance of the Selector class
	 * @param blackboard Reference to the AI Blackboard data
	 */
	public Selector(AiSystem aiSys){
		super(aiSys);              
	}

	public Selector(AiSystem aiSys, String name){
		super(aiSys, name);              
	}
	/**
	 * Chooses the new task to update.
	 * @return The new task, or null if none was found
	 */
	public Task chooseNewTask(){
		Task task = null;
		boolean found = false;
		int curPos = control.subtasks.indexOf(control.curTask, false);

		while(!found){
			if(curPos == (control.subtasks.size - 1)){
				found = true;
				task = null;
				break;
			}

			curPos++;
			
			task = control.subtasks.get(curPos);
			if(task.checkConditions()){
				task.getControl().safeStart();
				return task;
			}
		}
		return task;
	}

	/**
	 * In case of child finishing with failure we find a new one to update,
	 * or fail if none is to be found
	 */
	@Override
	public void childFailed(){
		LogTask("child failed");
		control.curTask = chooseNewTask();
		if(control.curTask == null){
			control.finishWithFailure();
		}               
	}

	/**
	 * In case of child finishing with sucess, our job here is done, finish with sucess
	 * as well
	 */
	@Override
	public void childSucceeded(){
		LogTask("child succeeded");
		control.finishWithSuccess();            
	}
}