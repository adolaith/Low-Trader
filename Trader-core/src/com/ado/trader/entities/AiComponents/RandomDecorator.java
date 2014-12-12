package com.ado.trader.entities.AiComponents;

import java.util.Random;

import com.ado.trader.entities.AiComponents.base.Task;
import com.ado.trader.entities.AiComponents.base.TaskDecorator;
import com.ado.trader.systems.AiSystem;
import com.badlogic.gdx.Gdx;

/**
 * Task decorator that adds a random chance
 * of being selected when checking the conditions
 * @author Ying
 *
 */
public class RandomDecorator extends TaskDecorator {
	/**
	 * Chance of this Task being chosen. 
	 * Range: ]0,100[
	 */
	private int chance;

	/**
	 * Random number generator.
	 */
	Random rand;

	public RandomDecorator(AiSystem aiSystem, Task task, String chanceInt) {
		super(aiSystem, task);
		init(Integer.valueOf(chanceInt));
	}

	public RandomDecorator(AiSystem aiSystem, Task task, String name, int chance) {
		super(aiSystem, task, name);
		init(chance);
	}
	/**
	 * Private initialization logic
	 * @param chance Chance to choose this task, range ]0,100[
	 */
	private void init(int chance)
	{
		this.chance = chance;
		if(chance > 100 || chance < 0)
		{
			LogTask("Chance value out of range!");
		}
		rand = new Random();
	}

	/**
	 * Calls the decorated DoAction
	 */
	@Override
	public void doTask() 
	{
		task.doTask();
	}

	/**
	 * Does the tasks normal confirmations plus giving it a random
	 * chance to be chosen.
	 */
	@Override
	public boolean checkConditions()
	{
		LogTask("&&&&&&&&&&&&&&&&&&&RAND TASK&&&&&&&&&&&&&&&&&&&&&&&");
		float value = rand.nextFloat() * 100;
		LogTask("value: "+ value + ". Boolean: "+ task.checkConditions() + ". rand:" + this.chance);
		return task.checkConditions() && value < this.chance;
	}

}
