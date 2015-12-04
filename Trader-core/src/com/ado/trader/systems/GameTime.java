package com.ado.trader.systems;

import com.ado.trader.GameMain;
import com.badlogic.gdx.Gdx;

public class GameTime extends VoidIntervalSystem {
	int daysPlayed, worldTime;
	float counter;
	final float interval = 1.0f;
	Time time;
	
	public GameTime(float tickInterval){
		super(tickInterval);
		time = Time.DAWN;
	}
	@Override
	protected boolean checkProcessing() {
		if(super.checkProcessing()){
			worldTime++;
			if(worldTime % 60 == 0){
				return true;
			}
		}
		return false;
	}

	@Override
	protected void processSystem() {
		setTimeOfDay();
		
		if(worldTime>=Time.NIGHT.getValue()*60){
//			world.getSystem(FarmSystem.class).process();
			time = Time.DAWN;
			daysPlayed++;
			worldTime = 0;
			Gdx.app.log(GameMain.LOG, "MIDNIGHT RESET!");
		}
	}
	private void setTimeOfDay(){
		Time[] times = Time.values();
		for(int i = 0; i < times.length; i++){
			if(worldTime / 60 < times[i].getValue()){
				time = times[i];
				return;
			}
		}
	}
	public Time getTimeOfDay(){
		return time;
	}
	public int getTime(){
		return worldTime;
	}
	public int getDays() {
		return daysPlayed;
	}
	public void loadSettings(int daysPlayed, Time t, int worldTime){
		this.worldTime = worldTime;
		this.time = t;
		this.daysPlayed = daysPlayed;
	}
	public enum Time{
		DAWN(5), MORNING(9), DAY(18), NIGHT(24);
		private Time(int t){
			this.t=t;
		}
		private int t;
		public int getValue(){
			return t;
		}
	}
}
