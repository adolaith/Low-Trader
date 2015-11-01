package com.ado.trader.entities.AiComponents;

import com.ado.trader.entities.AiComponents.base.LeafTask;
import com.ado.trader.systems.AiSystem;

public class WaitUntil extends LeafTask {
	int waitUntil;

	//0=morning,1=daytime, 2=evening, 3=night
	public WaitUntil(int seconds, AiSystem aiSys) {
		super(aiSys);
		waitUntil = seconds;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void end() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doTask() {
		switch(waitUntil){
		case 0:
			if(aiSys.game.getTime().isMorning()){control.finishWithSuccess();}
			break;
		case 1:
			if(aiSys.game.getTime().isDayTime()){control.finishWithSuccess();}
			break;
		case 2:
			if(aiSys.game.getTime().isEvening()){control.finishWithSuccess();}
			break;
		case 3:
			if(aiSys.game.getTime().isNight()){control.finishWithSuccess();}
			break;
		}
	}

	@Override
	public boolean checkConditions() {
		// TODO Auto-generated method stub
		return true;
	}

}
