package com.ado.trader.entities.AiComponents.base;

import com.ado.trader.systems.AiSystem;

/**
 * Base class for the specific decorators.
 * Decorates all the task methods except for the DoAction, for commodity. 
 * (Tough any method can be decorated in the base classes with no problem, 
 * they are decorated by default so the programmer does not forget)
 * 
 * Original code by @author Ying
 * Modified
 */
public abstract class TaskDecorator extends Task{
	/**
	 * Reference to the task to decorate
	 */
	protected Task task;

	/**
	 * Creates a new instance of the Decorator class
	 * @param blackboard Reference to the AI Blackboard data
	 * @param task Task to decorate
	 */
	public TaskDecorator(AiSystem aiSys, Task task){
		super(aiSys);
		InitTask(task);
	}
	
	public TaskDecorator(AiSystem aiSys, Task task, String name){
		super(aiSys, name);
		InitTask(task);
	}

	/**
	 * Initializes the task reference
	 * @param task Task to decorate
	 */
	private void InitTask(Task task){
		this.task = task;
		this.task.getControl().setTask(this);
	}

	/**
	 * Decorate the CheckConditions
	 */
	@Override
	public boolean checkConditions(){
		return this.task.checkConditions();
	}

	/**
	 * Decorate the end
	 */
	@Override
	public void end(){
		this.task.end();
	}

	/**
	 * Decorate the request for the Control reference
	 */
	@Override
	public TaskController getControl(){
		return this.task.getControl();
	}

	/**
	 * Decorate the start
	 */
	@Override
	public void start(){
		this.task.start();
	}
}