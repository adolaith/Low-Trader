package com.ado.trader.entities.AiComponents.base;

import com.ado.trader.systems.AiSystem;

/**
 * This ParentTask executes each of it's children in turn until he has finished all of them.
 * 
 * It always starts by the first child, updating each one.
 * If any child finishes with failure, the Sequence fails, and we finish with failure.
 * When a child finishes with success, we select the next child as the update victim.
 * If we have finished updating the last child, the Sequence returns with success.
 * 
 * Original code by @author Ying
 * Modified
 */
public class Sequence extends ParentTask{
	/**
	 * Creates a new instance of the Sequence class
	 * @param blackboard Reference to the AI Blackboard data
	 */
	public Sequence(AiSystem aiSys){
		super(aiSys);
	}
	public Sequence(AiSystem aiSys, String name){
		super(aiSys, name);
	}

	/**
	 * A child finished with failure.
	 * We failed to update the whole sequence. Bail with failure.
	 */
	@Override
	public void childFailed(){
		control.finishWithFailure();
	}

	/**
	 * A child has finished with success
	 * Select the next one to update. If it's the last, we have finished with success.
	 */
	@Override
	public void childSucceeded(){
		int curPos = control.subtasks.indexOf(control.curTask, false);
		if( curPos == (control.subtasks.size - 1)){
			control.finishWithSuccess();
			LogTask("Sequence finished");
		}
		else
		{
			control.curTask = control.subtasks.get(curPos + 1);
			LogTask("next sequence task");
			if(!control.curTask.checkConditions()){
				control.finishWithFailure();
				LogTask("sequence failed");
			}else{
				control.curTask.getControl().safeStart();
			}
		}
	}
}