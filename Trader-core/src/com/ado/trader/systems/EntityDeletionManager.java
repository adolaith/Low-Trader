package com.ado.trader.systems;

import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.utils.Bag;

public class EntityDeletionManager extends Manager {
	private Bag<Entity> entities = new Bag<Entity>();

	public EntityDeletionManager() {
	}

    @Override
    public void added(Entity e) {
        entities.add(e);
    }

    @Override
    public void deleted(Entity e) {
        entities.remove(e);
    }

    public void deleteAllEntities() {
        for (Entity e : entities)
            e.deleteFromWorld();
    }

}
