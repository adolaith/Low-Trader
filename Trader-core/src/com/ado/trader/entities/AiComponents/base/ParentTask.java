package com.ado.trader.entities.AiComponents.base;

import com.ado.trader.GameMain;
import com.ado.trader.systems.AiSystem;
import com.badlogic.gdx.Gdx;

/**
 * Inner node of the behavior tree, a flow director node, 
 * that selects a child to be executed next.
 * 
 * Sets a specific kind of TaskController for these kinds of tasks.
 */
/*
 * Original code by @author Ying
 * Modified
 */

public abstract class ParentTask extends Task{
	
	/**
     * TaskControler for the parent task
     */
    ParentTaskController control;

	public ParentTask(AiSystem aiSys) {
		super(aiSys);
		createController();
	}
	
	public ParentTask(AiSystem aiSys, String name) {
		super(aiSys, name);
		createController();
	}

	@Override
	public void start() {
		LogTask("Starting");
		control.curTask = control.subtasks.first();		
		if(control.curTask == null)
		{
			Gdx.app.log(GameMain.LOG, "curTask is null. ParentTask(selector) error");
		}
	}

	@Override
	public void end() {
		LogTask("Ending");
	}

	@Override
	public void doTask() {
		LogTask("Doing action");
		
		if(control.finished()){
			return;
		}
		if(control.curTask == null){
			// If there is a null task, we've done something wrong
			Gdx.app.log(GameMain.LOG, "curTask is null. ParentTask(selector) error");
			return;
		}

		// If we do have a curTask...
		if( !control.curTask.getControl().started()){
			LogTask("starting child task");
			// ... and it's not started yet, start it.
			if(control.curTask.checkConditions()){
				control.curTask.getControl().safeStart();
			}else{
				LogTask("failed to start task");
				this.childFailed();
			}
		}else if(control.curTask.getControl().finished()){

			// ... and it's finished, end it properly.
			LogTask("child task ENDING");
			control.curTask.getControl().safeEnd();

			if(control.curTask.getControl().succeeded())
			{
				this.childSucceeded();
			}else if(control.curTask.getControl().failed()){
				LogTask("CHILD HAS FAILED");
				this.childFailed();
			}
		}else{               
			// ... and it's ready, update it. 
				LogTask("Doing child task");
				control.curTask.doTask();
		}       

	}

	@Override
	public TaskController getControl() {
		return control;
	}

	@Override
	public boolean checkConditions() {
		LogTask("Checking conditions");
		
		return control.subtasks.size > 0;
	}
	
	/**
     * Abstract to be overridden in child classes. Called when a child finishes with success.
     */
    public abstract void childSucceeded();
    
    /**
     * Abstract to be overridden in child classes. Called when a child finishes with failure.
     */
    public abstract void childFailed();
    
    /**
     * Creates the TaskController.
     */
	private void createController()
    {
            this.control = new ParentTaskController(this);
    }

}
