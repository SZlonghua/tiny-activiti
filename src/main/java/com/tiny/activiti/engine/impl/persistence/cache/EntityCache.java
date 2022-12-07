package com.tiny.activiti.engine.impl.persistence.cache;

import com.tiny.activiti.engine.impl.interceptor.Session;
import com.tiny.activiti.engine.impl.persistence.entity.Entity;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface EntityCache extends Session {

    /**
     * Returns all cached {@link Entity} instances as a map
     * with following structure: { entityClassName, {entityId, entity} }
     */
    Map<Class<?>, Map<String, CachedEntity>> getAllCachedEntities();

    /**
     * Adds the gives {@link Entity} to the cache.
     *
     * @param entity The {@link Entity} instance
     * @param storeState If true, the current state {@link Entity#getPersistentState()} will be stored for future diffing.
     *                   Note that, if false, the {@link Entity} will always be seen as changed.
     * @return Returns a {@link CachedEntity} instance, which can be enriched later on.
     */
    CachedEntity put(Entity entity, boolean storeState);

    /**
     * Returns the cached {@link Entity} instance of the given class with the provided id.
     * Returns null if such a {@link Entity} cannot be found.
     */
    <T> T findInCache(Class<T> entityClass, String id);

    /**
     * Returns all cached {@link Entity} instances of a given type.
     * Returns an empty list if no instances of the given type exist.
     */
    <T> List<T> findInCache(Class<T> entityClass);

    /**
     * Returns all {@link CachedEntity} instances for the given type.
     * The difference with {@link #findInCache(Class)} is that here the whole {@link CachedEntity}
     * is returned, which gives access to the persistent state at the moment of putting it in the cache.
     */
    <T> Collection<CachedEntity> findInCacheAsCachedObjects(Class<T> entityClass);

    /**
     * Removes the {@link Entity} of the given type with the given id from the cache.
     */
    void cacheRemove(Class<?> entityClass, String entityId);
}
