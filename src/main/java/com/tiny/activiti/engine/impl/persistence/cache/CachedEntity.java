package com.tiny.activiti.engine.impl.persistence.cache;

import com.tiny.activiti.engine.impl.persistence.entity.Entity;

public class CachedEntity {

    protected Entity entity;

    /**
     * Represents the 'persistence state' at the moment this {@link CachedEntity} instance was created.
     * It is used later on to determine if a {@link Entity} has been updated, by comparing
     * the 'persistent state' at that moment with this instance here.
     */
    protected Object originalPersistentState;

    public CachedEntity(Entity entity, boolean storeState) {
        this.entity = entity;
        if (storeState) {
            this.originalPersistentState = entity.getPersistentState();
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Object getOriginalPersistentState() {
        return originalPersistentState;
    }

    public void setOriginalPersistentState(Object originalPersistentState) {
        this.originalPersistentState = originalPersistentState;
    }

    public boolean hasChanged() {
        return entity.getPersistentState() != null && !entity.getPersistentState().equals(originalPersistentState);
    }

}
