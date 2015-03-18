package com.ado.trader.entities.components;

import com.ado.trader.entities.AiComponents.base.Task;
import com.artemis.Component;

public class AiProfile extends Component{
	Task taskProfile;

	public AiProfile() {
		taskProfile = null;
	}
	public AiProfile(Task task){
		this.taskProfile = task;
	}
	public Task getTaskProfile() {
		return taskProfile;
	}
	public void setAiProfile(Task taskProfile) {
		this.taskProfile = taskProfile;
	}
}
