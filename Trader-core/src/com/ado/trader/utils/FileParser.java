package com.ado.trader.utils;

import com.ado.trader.GameMain;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

public class FileParser {
	public StringBuilder string;
	FileHandle file;

	public void initParser(String path, boolean writing, boolean external){
		if(writing){
			string = new StringBuilder();
		}else{
			string = null;
		}
		if(external){
			file = Gdx.files.external(path);
		}else{
			file = Gdx.files.internal(path);
		}
	}
	public void newNode(){
		if(string.length()==0){return;}
		string.append("\"");
	}
	public void addElement(String key, String value){
		string.append(key+":"+value+";");
	}
	public void addElement(String key, Array<String> values){
		string.append(key+":");
		for(String s: values){
			string.append(s+",");
		}
		string.append(";");
	}
	public String getString(){
		return string.toString();
	}
	public void writeToFile(){
		if(string.length()==0){
			Gdx.app.log(GameMain.LOG, "Nothing to write");
			file.delete();
			return;
		}
		file.writeString(getString(), false);
		file = null;
	}
	public Array<ArrayMap<String, String>> readFile(){
		Array<ArrayMap<String, String>> data = new Array<ArrayMap<String, String>>();
		String tmp= file.readString();
		if(tmp.startsWith("#")){
			tmp=tmp.substring(tmp.indexOf("#")+1);
			tmp=tmp.substring(tmp.indexOf("#")+1).trim();
		}
		String[] nodes = tmp.split("\"");
		for(String node: nodes){
			ArrayMap<String, String> elements = new ArrayMap<String, String>();
			data.add(elements);
			String[] eArray = node.split(";");
			for(String e:eArray){
				if(!e.contains(":")){continue;}
				String[] values = e.split(":");
				if(values.length<2){
					elements.put(values[0], null);
				}else{
					elements.put(values[0], values[1]);
				}
			}
		}
		return data;
	}
	public FileHandle getFile() {
		return file;
	}
}
