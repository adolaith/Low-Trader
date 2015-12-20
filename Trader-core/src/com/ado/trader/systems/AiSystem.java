package com.ado.trader.systems;

import java.lang.reflect.Constructor;

import com.ado.trader.GameMain;
import com.ado.trader.entities.AiComponents.base.ParentTaskController;
import com.ado.trader.entities.AiComponents.base.Task;
import com.ado.trader.entities.components.AiProfile;
import com.ado.trader.entities.components.Inventory;
import com.ado.trader.entities.components.Movement;
import com.ado.trader.entities.components.Position;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

//processes an entitys AiProfile and returns a task or ,if task already underway, updates the task
@Wire
public class AiSystem extends EntityProcessingSystem{
	ComponentMapper<Position> pm;
	ComponentMapper<Movement> mm;
	ComponentMapper<Inventory> invenm;
	ComponentMapper<AiProfile> aim;
	
	ArrayMap<String, JsonValue> profiles;

	public Entity currentEntity;

	@SuppressWarnings("unchecked")
	public AiSystem() {
		super(Aspect.all(AiProfile.class));
		
		
		profiles = new ArrayMap<String, JsonValue>();
		
		loadAiProfiles();
	}

	//update Ai loop
	protected void process(Entity e) {
		currentEntity = e;
		if(!aim.get(e).getTaskProfile().getControl().started()){
			Gdx.app.log(GameMain.LOG, "AI started");
			aim.get(e).getTaskProfile().getControl().safeStart();
			return;
		}
//		aim.get(e).getTaskProfile().doTask();
	}
	
	private void loadAiProfiles(){
		try {
			Json j = new Json();
			
			//internal profiles
			String[] files = Gdx.files.internal("data/ai/Profiles.txt").readString().split("\n");
			for(String file: files){
				if(!file.isEmpty()){
					JsonValue p = j.fromJson(null, Gdx.files.internal("data/ai/" + file));
					profiles.put(file, p.child);
				}
			}
			
			//external profiles
			FileHandle e = Gdx.files.external("adoGame/editor/ai/");
			
			for(FileHandle c: e.list()){
				JsonValue p = j.fromJson(null, Gdx.files.external(c.path()));
				profiles.put(c.name(), p.child);
			}
			
			
		} catch (Exception e) {
			System.out.println("Error loading ai profiles");
			e.printStackTrace();
		}
	}
	
	public Task getAiProfile(String name){
		if(!profiles.containsKey(name)) return null;
		
		JsonValue profile = profiles.get(name);
		
		Task t = createProfile(profile, null);
		
		return t;
	}
	
	private Task createProfile(JsonValue task, Task parent){
		//create task
		Task t = createTask(task, parent);
		
		//create decorations
		if(task.has("deco")){
			for(JsonValue d = task.get("deco").child; d != null; d = d.next){
				t = createTask(d, t);
			}
		}
		
		//create child tasks
		if(task.has("children")){
			for(JsonValue c = task.get("children").child; c != null; c = c.next){
				createProfile(c, t);
			}
		}
		
		//add task to parent
		if(parent != null){
			((ParentTaskController)parent.getControl()).Add(t);
		}
		
		return t;
	}
	
	String pkgPath = "com.ado.trader.entities.AiComponents.";
	//Creates a task class. parent task is used only for decorations
	private Task createTask(JsonValue taskData, Task parent){
		try {
			if(taskData.has("param")){
				//task with additional args
				
				JsonValue params = taskData.get("param");
				Object[] args;
				Array<Class> argClasses = new Array<Class>();
				int index = 0;
				
				//extract params
				if(parent != null && !taskData.has("children")){
					//task with aiSys + a parent task
					args = new Object[params.asString().split(",").length + 2];
					args[0] = this;
					args[1] = parent;
					argClasses.add(AiSystem.class);
					argClasses.add(Task.class);
					index = 2;
				}else{
					//task with only aiSys
					args = new Object[params.asString().split(",").length + 1];
					args[0] = this;
					argClasses.add(AiSystem.class);
					index = 1;
				}

				//load additional args
				for(String s: params.asString().split(",")){
					args[index] = s;
					argClasses.add(s.getClass());
					index++;
				}
				
				//retrieve task class type
				Class<? extends Task> taskClass;
				if(taskData.has("children")){
					taskClass = (Class<? extends Task>) Class.forName(pkgPath + "base." + taskData.name);	
				}else{
					taskClass = (Class<? extends Task>) Class.forName(pkgPath + taskData.name);
				}
				
				Constructor<? extends Task> constructor = null;
				
				//loops all class constructors
				for(Constructor<?> c : taskClass.getConstructors()){
					if(c.getParameterTypes().length == argClasses.size){
						boolean match = false;
						//loops constructor args
						for(int x = 0; x< c.getParameterTypes().length; x++){
							if(c.getParameterTypes()[x].equals(argClasses.get(x))){
								match = true;
							}else{
								match = false;
							}
						}
						//load params on match
						if(match){
							constructor = (Constructor<? extends Task>) c;
							break;
						}
					}
				}
				
				return (Task) constructor.newInstance(args);
			}else{
				//task with no args
				
				//retrieve task class type
				Class<? extends Task> taskClass;
				if(taskData.has("children")){
					taskClass = (Class<? extends Task>) Class.forName(pkgPath + "base." + taskData.name);	
				}else{
					taskClass = (Class<? extends Task>) Class.forName(pkgPath + taskData.name);
				}
				
				String classType = taskClass.getSuperclass().getSimpleName();
				
				//load params and return
				if(parent != null && !classType.matches("LeafTask")){
					Constructor<?> constructor = taskClass.getConstructor(AiSystem.class, Task.class);
					return (Task) constructor.newInstance(this, parent);
				}else{
					Constructor<?> constructor = taskClass.getConstructor(AiSystem.class);
					return (Task) constructor.newInstance(this);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public ArrayMap<String, JsonValue> getAllAiProfiles(){
		return profiles;
	}
	public ComponentMapper<Position> getPm() {
		return pm;
	}
	public ComponentMapper<Movement> getMm() {
		return mm;
	}
	public ComponentMapper<AiProfile> getAim() {
		return aim;
	}
	public ComponentMapper<Inventory> getInvenm() {
		return invenm;
	}

	public World getWorld(){
		return world;
	}
}