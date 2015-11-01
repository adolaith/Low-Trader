package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.ParentTaskController;
import com.ado.trader.entities.AiComponents.base.Task;
import com.ado.trader.entities.AiComponents.base.TaskDecorator;
import com.ado.trader.systems.AiSystem;

/**
 * Decorator that resets to "Started" the task it is applied to, each time said
 * task finishes.
 * 
 * @author Ying
 *
 */
public class ResetDecorator extends TaskDecorator{
        
        /**
         * Creates a new instance of the ResetDecorator class
         * @param blackboard Reference to the AI Blackboard data
         * @param task Task to decorate
         */
        public ResetDecorator(AiSystem aiSys, Task task) 
        {
                super(aiSys, task);
        }
        
        public ResetDecorator(AiSystem aiSys, Task task, String name) 
        {
                super(aiSys, task, name);
        }

        /**
         * Does the decorated task's action, and if it's done, resets it.
         */
        @Override
        public void doTask() 
        {
                this.task.doTask();
                if(this.task.getControl().finished())
                {
                	((ParentTaskController)this.task.getControl()).Reset();
                }
        }
}