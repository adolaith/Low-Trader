package com.ado.trader.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class FileLogger {
	static FileHandle logFile;

	private static FileHandle getLogInstance(){
		if(logFile == null){
			logFile = Gdx.files.external("adoGame/log.txt");
			logFile.writeString("", false);
		}
		return logFile;
	}
	
	public static void writeLog(String log){
		getLogInstance();
		
		logFile.writeString(log, true);
		logFile.writeString("\n", true);
	}
}
