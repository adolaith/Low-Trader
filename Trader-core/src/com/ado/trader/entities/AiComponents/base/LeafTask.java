package com.ado.trader.entities.AiComponents.base;

import com.ado.trader.systems.AiSystem;

/**
 * Leaf task (or node) in the behavior tree.
 *  
 * Specifies a TaskControler, by composition, 
 * to take care of all the control logic, 
 * without burdening the Task class with 
 * complications.
 *
 * Original code by @author Ying
 * Modified
 */
public abstract class LeafTask extends Task {
	/**
	 * Task controler to keep track of the Task state.
	 */
	protected TaskController control;

	/**
	 * Creates a new instance of the LeafTask class
	 * @param blackboard Reference to the AI Blackboard data
	 */
	public LeafTask(AiSystem aiSys) {
		super(aiSys);
		createController();
	}
	
	public LeafTask(AiSystem aiSys, String name) {
		super(aiSys, name);
		createController();
	}
	/**
	 * Creates the controller for the class
	 */
	private void createController(){
		this.control = new TaskController(this);
	}

	/**
	 * Gets the controller reference.
	 */
	@Override
	public TaskController getControl(){        
		return this.control;
	}
}