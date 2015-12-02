package com.ado.trader.utils;

import com.badlogic.gdx.math.RandomXS128;

/*		FFFF = 65,535; FFF = 4095
 * 
 * 		Entity ID Format: FFFF.FFFF
 * 			RESERVED  0000. = base profile
 * 			RESERVED  0001. = spawnable NPC
 * 			RESERVED  0002. = spawnable item/consumable
 * 			RESERVED  0003. = wall
 * 			   0004 - FFFF. = region ID
 * 
 * 					  xxxx.0000-0FFF = unique NPC id
 * 						  .1000-1FFF = unique item id
 * 						  .2000-2FFF = unique entity id
 * 						  .3000-3FFF = unique event trigger id
 * 
 */
public class IdGenerator {
	public enum IdType{
		NPC('0'),
		ITEM('1'),
		ENTITY('2'),
		EVENT('3');
		
		private char value;
		
		IdType(char value){
			this.value = value;
		}
		
		public char value(){
			return this.value;
		}
	}

	private final static int RESERVE_MAX = 3;
	public final static String ENTITY_MAX = "FFF";
	public final static String REGION_MAX = "FFFF";
	
	public final static String BASE_PROFILE = "0000";
	public final static String SPAWNABLE_NPC = "0001";
	public final static String SPAWNABLE_ITEM = "0002";
	public final static String WALL = "0003";
	
	public final static char NPC_ID = '0';
	public final static char ITEM_ID = '1';
	public final static char ENTITY_ID = '2';
	public final static char EVENT_ID = '3';
	
	private static RandomXS128 randGen;
	
	private IdGenerator() {
		randGen = new RandomXS128();
	}

	public static String getShortId(){
		if(randGen == null){
			new IdGenerator();
		}
		
		long id = randGen.nextLong(Long.parseLong(ENTITY_MAX, 16));
		
		return Long.toHexString(id);
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
	public static String getUniqueId(){
		return getRegionId();
	}
}
