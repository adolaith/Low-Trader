package com.ado.trader.entities.AiComponents.base;

import com.ado.trader.systems.AiSystem;

public class Parallel extends ParentTask {

	public Parallel(AiSystem aiSys) {
		super(aiSys);
	}

	public Parallel(AiSystem aiSys, String name) {
		super(aiSys, name);
	}

	@Override
	public void childSucceeded() {
		control.finishWithSuccess();
	}

	@Override
	public void childFailed() {
		control.finishWithFailure();
	}
	@Override
	public void doTask() {
		int count = 0;
		boolean result = true;
		for(Task t: control.subtasks){
			if(!t.getControl().started() && t.checkConditions()){
				t.getControl().safeStart();
			}else if(t.getControl().started()){
				t.doTask();
			}
			if(t.getControl().finished()){
				if(t.getControl().failed()){
					result = false;
					for(Task task: control.subtasks){
						task.getControl().safeEnd();		
					}
					break;
				}
				count++;
			}
		}
		if(count == control.subtasks.size){
			for(Task t: control.subtasks){
				t.getControl().safeEnd();
			}
			if(result){
				control.finishWithSuccess();
			}else{
				control.finishWithFailure();
			}
		}
	}
}
