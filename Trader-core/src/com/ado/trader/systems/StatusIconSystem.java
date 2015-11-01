package com.ado.trader.systems;

import com.ado.trader.entities.components.Status;
import com.artemis.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

public class StatusIconSystem extends VoidIntervalSystem{
	Array<StatusIcon> free;
	Array<StatusIcon> occupied;
	ArrayMap<String, Sprite> icons;
	TextureAtlas atlas; 

	public StatusIconSystem(float tickInterval, TextureAtlas atlas) {
		super(tickInterval);
		this.atlas = atlas;
		
		free = new Array<StatusIcon>(100);
		occupied = new Array<StatusIcon>(100);
		icons = new ArrayMap<String, Sprite>();
	}
	
	@Override
	protected void processSystem() {
		for(StatusIcon icon: occupied){
			if(icon.count >= 2){
				icon.setAlpha(-0.2f);
				icon.modY += 2;
			}
			
			icon.count++;
			if(icon.count == 8){
				if(!icons.containsKey(icon.name)){
					icons.put(icon.name, icon.icon);
				}
				free.add(icon);
				occupied.removeValue(icon, false);
				world.getEntity(icon.getAnchoredEntity()).removeComponent(Status.class);
				icon.resetIcon();
			}
		}
	}
	private Sprite getIconSprite(String name){
		if(icons.containsKey(name)){
			return icons.removeKey(name);
		}
		return atlas.createSprite("gui/"+name);
	}
	
	public void newIconAnimation(String iconName, Entity anchorEntity){
		StatusIcon icon = null;
		for(StatusIcon i: free){
			if(i != null){
				icon = i;
				free.removeValue(i, false);
			}
		}
		if(icon == null){
			icon = new StatusIcon(atlas.createSprite("gui/iconBg"));
		}
		icon.setupIcon(iconName, getIconSprite(iconName), anchorEntity.getId());
		anchorEntity.addComponent(new Status(icon));
		
		occupied.add(icon);
	}
	
	public class StatusIcon{
		Sprite background, icon;
		public float count, alpha;
		public int modY;
		Integer anchoredEntity;
		String name;
		
		int width = 32;
		int height = 32;
		
		public StatusIcon(Sprite iconBackground){
			alpha = 1;
			background = iconBackground;
			anchoredEntity = null;
		}
		public void resetIcon(){
			background.setAlpha(1f);
			name = "";
			icon = null;
			anchoredEntity = null;
			count = 0;
			modY = 0;
			alpha = 1f;
		}
		public void setupIcon(String name, Sprite icon, int anchorEntity){
			this.icon = icon;
			this.anchoredEntity = anchorEntity;
			this.name = name;
			
			background.setSize(width, height);
			icon.setSize(width, height);
		}
		public void drawIcon(SpriteBatch batch, Vector2 entityPosition){
			entityPosition.x = entityPosition.x - width / 2;
			entityPosition.y = entityPosition.y + modY;
			
			batch.setColor(alpha, alpha, alpha, alpha);
			batch.draw(background, entityPosition.x, entityPosition.y, width, height);
			batch.draw(icon, entityPosition.x, entityPosition.y, width, height);
			batch.setColor(1, 1, 1, 1);
		}
		public void setAlpha(float value){
			this.alpha += value;
		}
		public Integer getAnchoredEntity() {
			return anchoredEntity;
		}
		public Sprite getBackground() {
			return background;
		}
		public Sprite getIcon() {
			return icon;
		}
	}
}
