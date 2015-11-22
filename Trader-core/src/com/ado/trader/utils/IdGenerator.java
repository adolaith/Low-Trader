package com.ado.trader.utils;

import com.badlogic.gdx.math.RandomXS128;

/*		FFFF = 65,535; FFF = 4095
 * 
 * 		Entity ID Format: FFFF.FFFF
 * 			RESERVED  0000. = editor only profile
 * 			RESERVED  0001. = spawnable NPC
 * 			RESERVED  0002. = spawnable item/consumable
 * 			   0003 - FFFF. = region ID
 * 
 * 					  xxxx.0000-0FFF = region unique NPC id
 * 						  .1000-1FFF = region unique item id
 * 						  .2000-2FFF = region unique wall id
 * 						  .3000-3FFF = region unique entity id
 * 						  .4000-4FFF = region unique event trigger id
 * 
 */
public class IdGenerator {
	public enum IdType{
		NPC("0"),
		ITEM("1"),
		WALL("2"),
		ENTITY("3"),
		EVENT("4");
		
		private String value;
		
		IdType(String value){
			this.value = value;
		}
		
		public String value(){
			return this.value;
		}
	}

	private final static int RESERVE_MAX = 2;
	public final static String ENTITY_MAX = "FFF";
	public final static String REGION_MAX = "FFFF";
	
	public final static String EDITOR_ONLY = "0000";
	public final static String SPAWNABLE_NPC = "0001";
	public final static String SPAWNABLE_ITEM = "0002";
	
	public final static String NPC_ID = "0";
	public final static String ITEM_ID = "1";
	public final static String WALL_ID = "2";
	public final static String ENTITY_ID = "3";
	public final static String EVENT_ID = "4";
	
	private static RandomXS128 randGen;
	
	private IdGenerator() {
		randGen = new RandomXS128();
	}

	public static String getEntityId(IdType type){
		if(randGen == null){
			new IdGenerator();
		}
		
		long id = randGen.nextLong(Long.parseLong(ENTITY_MAX, 16));
		
		return type.value + Long.toHexString(id);
	}
	
	public static String getRegionId(){
		if(randGen == null){
			new IdGenerator();
		}
		
		long id;
		
		do{
			id = randGen.nextLong(Long.parseLong(REGION_MAX, 16));
		}while(id <= RESERVE_MAX );
		
		return Long.toHexString(id);
	}
}
