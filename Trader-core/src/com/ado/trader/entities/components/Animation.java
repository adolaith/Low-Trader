package com.ado.trader.entities.components;

import com.ado.trader.systems.AnimationSystem;
import com.ado.trader.utils.GameServices;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.brashmonkey.spriter.Player;

public class Animation extends SerializableComponent{
	Player player;

	public Animation(){
	}
	public Animation(Player player) {
		this.player = player;
	}
	
	public Player getPlayer(){
		return player;
	}
	public void setPosition(Vector2 pos){
		player.setPosition(pos.x, pos.y);
	}
	
	@Override
	public void save(Json writer) {
		
//		writer.writeValue("anim", skeleton.getData().getName());
	}
	@Override
	public void load(JsonValue data) {
		AnimationSystem animSys = GameServices.getWorld().getSystem(AnimationSystem.class);
		this.player = animSys.createPlayer(data.asString());
	}
}
