package com.ado.trader.entities.AiComponents.base;


/*
 * Original code by @author Ying
 * Modified
 */

public class TaskController {

	boolean done, sucess, started;

	Task task;

	public TaskController(Task task) {
		this.task = task;
		initialize();
	}

	public void initialize()
	{
		this.done = false;
		this.sucess = true;
		this.started = false;
	}

	public void finishWithSuccess()
	{
		this.sucess = true;
		this.done = true;
	}

	/**
	 * Ends the monitored class, with failure
	 */
	public void finishWithFailure()
	{
		this.sucess = false;
		this.done = true;
	}

	/**
	 * Indicates whether the task finished successfully
	 * @return True if it did, false if it didn't
	 */
	public boolean succeeded() 
	{
		return this.sucess;
	}

	/**
	 * Indicates whether the task finished with failure
	 * @return True if it did, false if it didn't
	 */
	public boolean failed()
	{
		return !this.sucess;
	}

	/**
	 * Indicates whether the task finished
	 * @return True if it did, false if it didn't
	 */
	public boolean finished() 
	{
		return this.done;
	}

	/**
	 * Indicates whether the class has started or not
	 * @return True if it has, false if it hasn't
	 */
	public boolean started()
	{
		return this.started;
	}

	/**
	 * Marks the class as just started.
	 */
	public void reset()
	{
		this.done = false;
	}
	/**
	 * Sets the task reference
	 * @param task Task to monitor
	 */
	public void setTask(Task task)
	{
		this.task = task;
	}

	/**
	 * Starts the monitored class
	 */
	public void safeStart()
	{
		this.started = true;
		task.start();
	}

	/**
	 * Ends the monitored task
	 */
	public void safeEnd()
	{
		this.done = false;
		this.started = false;
		task.end();
	}
}
