package com.ado.trader.entities.AiComponents.base;

import com.badlogic.gdx.utils.Array;
/*
 * Original code by @author Ying
 * Modified
 */
public class ParentTaskController extends TaskController{
	
	Array<Task> subtasks;
    
    /**
     * Current updating task
     */
     Task curTask;
    
	/**
     * Creates a new instance of the ParentTaskController class
     * @param task
     */
    public ParentTaskController(Task task) 
    {
            super(task);
            
            this.subtasks = new Array<Task>();
            this.curTask = null;
    }
    
    public Task getSubTask(String name){
    	for(Task t: subtasks){
    		if(t.name.matches(name)){
    			return t;
    		}
    	}
    	return null;
    }
    
    public void removeSubTask(String name){
    	for(Task t: subtasks){
    		if(t.name.matches(name)){
    			subtasks.removeValue(t, false);
    		}
    	}
    }
    
    /**
     * Adds a new subtask to the end of the subtask list.
     * @param task Task to add
     */
    public void Add(Task task)
    {
            subtasks.add(task);
    }
    
    /**
     * Resets the task as if it had just started.
     */
    public void Reset()
    {
            super.reset();
            this.curTask = subtasks.first();
    }
}