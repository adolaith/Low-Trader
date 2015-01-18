package com.ado.trader.systems;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.ado.trader.GameMain;
import com.ado.trader.entities.AiComponents.ResetDecorator;
import com.ado.trader.entities.AiComponents.base.Parallel;
import com.ado.trader.entities.AiComponents.base.ParentTaskController;
import com.ado.trader.entities.AiComponents.base.Selector;
import com.ado.trader.entities.AiComponents.base.Sequence;
import com.ado.trader.entities.AiComponents.base.Task;
import com.ado.trader.entities.components.AiProfile;
import com.ado.trader.entities.components.Inventory;
import com.ado.trader.entities.components.Movement;
import com.ado.trader.entities.components.Position;
import com.ado.trader.gui.GameServices;
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

//processes an entitys AiProfile and returns a task or ,if task already underway, updates the task
@Wire
public class AiSystem extends EntityProcessingSystem{
	ComponentMapper<Position> pm;
	ComponentMapper<Movement> mm;
	ComponentMapper<Inventory> invenm;
	ComponentMapper<AiProfile> aim;
	ArrayMap<String, String[]> profiles;

	public Entity currentEntity;
	public GameServices gameRes;

	@SuppressWarnings("unchecked")
	public AiSystem(GameServices gameRes) {
		super(Aspect.getAspectForAll(AiProfile.class));
		
		this.gameRes = gameRes;
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
		aim.get(e).getTaskProfile().doTask();
//		Gdx.app.log(GameMain.LOG, "*********************");
	}
	private void loadAiProfiles(){
		try {
			profiles = new ArrayMap<String, String[]>();
			FileHandle folder = Gdx.files.internal("./bin/data/ai/");
			FileHandle[] files = folder.list();
			for(FileHandle file: files){
				BufferedReader reader = file.reader(200);
				String line = reader.readLine();
				String[] tasks = new String[200];
				
				//loop while not end of file
				for(int x = 0;line != null && !line.matches("null"); x++){
					line = line.trim();
					char flag = line.charAt(0); 
					if(flag != '#'){
						tasks[x] = line;	
					}else{
						x--;
					}
					line = reader.readLine();
				}
				profiles.put(file.name(), tasks);
			}
		} catch (IOException e) {
			System.out.println("Error loading ai profiles");
			e.printStackTrace();
		}
	}
	
	private int taskIndex;
	
	public Task getAiProfile(String name){
		if(!profiles.containsKey(name)) return null;
		
		String[] profile = profiles.get(name);
		
		Task root = new Selector(this, "root");
		root = new ResetDecorator(this, root);
		
		taskIndex = 0;
		while(taskIndex < profile.length){
			String line = profile[taskIndex];
			if(line == null){
				break;
			}
			createNewTask(profile, root);
		}
		return root;
	}
	private Task createNewTask(String[] profile, Task task){
		String line = profile[taskIndex];
		char flag = line.charAt(0);
		switch(flag){
		case '*':
			if(task == null){
				return createParentTask(profile, task);
			}
			createParentTask(profile, task);
			break;
		case '>':
			taskIndex = taskIndex + 1;
			
			Task deco = createNewTask(profile, null);
			
			deco = createTask(line, deco);
			if(task == null){
				return deco;
			}
			((ParentTaskController)task.getControl()).Add(deco);
			break;
		case '-':
			int index = taskIndex;
			taskIndex = taskIndex + 1;
			if(task != null){
				Task t = createTask(profile[index], null);
				((ParentTaskController)task.getControl()).Add(t);
			}else{
				return createTask(profile[index], null);
			}
			break;
		}
		return null;
	}
	private Task createParentTask(String[] profile, Task task){
		String line = profile[taskIndex];
		Task parent = null;
		
		String[] t = line.substring(1).split(":"); 
		
		switch(t[0]){
		case "Sequence":
			parent = new Sequence(this, t[1]);
			break;
		case "Selector":
			parent = new Selector(this, t[1]);
			break;
		case "Parallel":
			parent = new Parallel(this, t[1]);
			break;
		}
		taskIndex = taskIndex + 1;
		while(!profile[taskIndex].substring(0).matches("!")){
			createNewTask(profile, parent);
		}
		taskIndex = taskIndex + 1;
		if(task != null){
			((ParentTaskController)task.getControl()).Add(parent);
			return null;
		}
		return parent;
	}
	private Task createTask(String taskData, Task task){
		try {
			String path = "com.ado.trader.entities.AiComponents.";
			String[] data = taskData.split(":");
			
			if(data.length != 1){
				
				Object[] args;
				Array<Class> argClasses = new Array<Class>();
				int index = 0;
				if(task != null){
					args = new Object[data[1].split(",").length + 2];
					args[0] = this;
					args[1] = task;
					argClasses.add(AiSystem.class);
					argClasses.add(Task.class);
					index = 2;
				}else{
					args = new Object[data[1].split(",").length + 1];
					args[0] = this;
					argClasses.add(AiSystem.class);
					index = 1;
				}
				for(String s: data[1].split(",")){
					args[index] = s;
					argClasses.add(s.getClass());
					index++;
				}
				
				Class<? extends Task> taskClass = (Class<? extends Task>) Class.forName(path + data[0].substring(1));
				Constructor<? extends Task> constructor = null;
				
				//loops all constructors
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
						if(match){
							constructor = (Constructor<? extends Task>) c;
						}
					}
				}
				return (Task) constructor.newInstance(args);
			}else{
				Class<?> taskClass = Class.forName(path + data[0].substring(1));
				
				if(task != null){
					Constructor<?> constructor = taskClass.getConstructor(AiSystem.class, Task.class);
					return (Task) constructor.newInstance(this, task);
				}else{
					Constructor<?> constructor = taskClass.getConstructor(AiSystem.class);
					return (Task) constructor.newInstance(this);
				}
			}

		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	public ArrayMap<String, String[]> getAllAiProfiles(){
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