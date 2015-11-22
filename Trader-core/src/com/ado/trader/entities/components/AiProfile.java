package com.ado.trader.entities.components;

import com.ado.trader.entities.AiComponents.base.Task;
import com.ado.trader.systems.AiSystem;
import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class AiProfile extends SerializableComponent{
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
	public void save(Json writer) {
		writer.writeValue("ai", name);
	}
	@Override
	public void load(JsonValue data) {
		AiSystem aiSys = GameServices.getWorld().getSystem(AiSystem.class);
		name = data.asString();
		taskProfile = aiSys.getAiProfile(this.name);
	}
}
