package com.tiny.activiti.engine.impl.persistence.cache;

import com.tiny.activiti.engine.impl.persistence.entity.Entity;

import java.util.*;

public class EntityCacheImpl implements EntityCache{

    protected Map<Class<?>, Map<String, CachedEntity>> cachedObjects = new HashMap<Class<?>, Map<String,CachedEntity>>();

    @Override
    public CachedEntity put(Entity entity, boolean storeState) {
        Map<String, CachedEntity> classCache = cachedObjects.get(entity.getClass());
        if (classCache == null) {
            classCache = new HashMap<String, CachedEntity>();
            cachedObjects.put(entity.getClass(), classCache);
        }
        CachedEntity cachedObject = new CachedEntity(entity, storeState);
        classCache.put(entity.getId(), cachedObject);
        return cachedObject;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T findInCache(Class<T> entityClass, String id) {
        CachedEntity cachedObject = null;
        Map<String, CachedEntity> classCache = cachedObjects.get(entityClass);

        if (classCache == null) {
            classCache = findClassCacheByCheckingSubclasses(entityClass);
        }

        if (classCache != null) {
            cachedObject = classCache.get(id);
        }

        if (cachedObject != null) {
            return (T) cachedObject.getEntity();
        }

        return null;
    }

    protected Map<String, CachedEntity> findClassCacheByCheckingSubclasses(Class<?> entityClass) {
        for (Class<?> clazz : cachedObjects.keySet()) {
            if (entityClass.isAssignableFrom(clazz)) {
                return cachedObjects.get(clazz);
            }
        }
        return null;
    }

    @Override
    public void cacheRemove(Class<?> entityClass, String entityId) {
        Map<String, CachedEntity> classCache = cachedObjects.get(entityClass);
        if (classCache == null) {
            return;
        }
        classCache.remove(entityId);
    }

    @Override
    public <T> Collection<CachedEntity> findInCacheAsCachedObjects(Class<T> entityClass) {
        Map<String, CachedEntity> classCache = cachedObjects.get(entityClass);
        if (classCache != null) {
            return classCache.values();
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> findInCache(Class<T> entityClass) {
        Map<String, CachedEntity> classCache = cachedObjects.get(entityClass);

        if (classCache == null) {
            classCache = findClassCacheByCheckingSubclasses(entityClass);
        }

        if (classCache != null) {
            List<T> entities = new ArrayList<T>(classCache.size());
            for (CachedEntity cachedObject : classCache.values()) {
                entities.add((T) cachedObject.getEntity());
            }
            return entities;
        }

        return Collections.emptyList();
    }

    public Map<Class<?>, Map<String, CachedEntity>> getAllCachedEntities() {
        return cachedObjects;
    }
    @Override
    public void flush() {

    }

    @Override
    public void close() {

    }
}
