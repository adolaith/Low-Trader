package com.ado.trader.entities.components;

import com.ado.trader.entities.AiComponents.base.Task;
import com.ado.trader.systems.AiSystem;
import com.ado.trader.utils.GameServices;
import com.artemis.Component;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

public class AiProfile extends Component implements Serializable{
	public String name;
	Task taskProfile;

	public AiProfile() {
		taskProfile = null;
	}
	public AiProfile(String name, Task task){
		this.taskProfile = task;
	}
	public Task getTaskProfile() {
		return taskProfile;
	}
	public void setAiProfile(Task taskProfile) {
		this.taskProfile = taskProfile;
	}
	@Override
	public void write(Json json) {
		json.writeValue("name", name);
	}
	@Override
	public void read(Json json, JsonValue jsonData) {
		AiSystem aiSys = GameServices.getWorld().getSystem(AiSystem.class);
		name = jsonData.getString("name");
		taskProfile = aiSys.getAiProfile(this.name);
	}
}
