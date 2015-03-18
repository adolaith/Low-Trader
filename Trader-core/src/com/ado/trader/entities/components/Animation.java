package com.ado.trader.entities.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;

public class Animation extends Component {
	public Skeleton skeleton;
	public AnimationState mainState;
	public AnimationState secondaryState;
	int width, height;

	public Animation(){
	}
	public Animation(Skeleton skeleton, AnimationStateData animData, int width, int height) {
		this.skeleton = skeleton;
		mainState = new AnimationState(animData);
		secondaryState = new AnimationState(animData);
		this.width = width;
		this.height = height;
	}
	public void setPosition(Vector2 pos){
		skeleton.setX(pos.x+width/2);
		skeleton.setY(pos.y+height/2);
	}
	public Skeleton getSkeleton() {
		return skeleton;
	}
	public AnimationState getMainState() {
		return mainState;
	}
	public AnimationState getSecondaryState() {
		return secondaryState;
	}
	public void setAnimationData(AnimationStateData animData){
		mainState = new AnimationState(animData);
		secondaryState = new AnimationState(animData);
	}
	public void setTileSize(int w, int h){
		width = w;
		height = h;
	}
	public void resetAnimation(){
		mainState.clearTracks();
		skeleton.setToSetupPose();
		if(skeleton.getData().getName().matches("human")){
			String headName = skeleton.findSlot("head").getAttachment().getName();
			String bodyName = skeleton.getSkin().getName();
			skeleton.setAttachment("head", "human/guyF_head"+Integer.valueOf(headName.substring(headName.length()-1)));
			skeleton.setSkin("m"+bodyName.substring(1, bodyName.indexOf("_"))+"_Front");
		}
	}
}
