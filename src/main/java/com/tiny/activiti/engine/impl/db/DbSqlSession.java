package com.tiny.activiti.engine.impl.db;

import com.tiny.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import com.tiny.activiti.engine.impl.context.Context;
import com.tiny.activiti.engine.impl.interceptor.Session;
import com.tiny.activiti.engine.impl.persistence.cache.EntityCache;
import com.tiny.activiti.engine.impl.persistence.entity.Entity;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DbSqlSession implements Session {

    protected SqlSession sqlSession;
    protected DbSqlSessionFactory dbSqlSessionFactory;
    protected EntityCache entityCache;

    protected Map<Class<? extends Entity>, Map<String, Entity>> insertedObjects
            = new HashMap<Class<? extends Entity>, Map<String, Entity>>();
    protected Map<Class<? extends Entity>, Map<String, Entity>> deletedObjects
            = new HashMap<Class<? extends Entity>, Map<String, Entity>>();
//    protected Map<Class<? extends Entity>, List<BulkDeleteOperation>> bulkDeleteOperations
//            = new HashMap<Class<? extends Entity>, List<BulkDeleteOperation>>();
//    protected List<Entity> updatedObjects = new ArrayList<Entity>();

    protected String connectionMetadataDefaultCatalog;
    protected String connectionMetadataDefaultSchema;

    public DbSqlSession(DbSqlSessionFactory dbSqlSessionFactory, EntityCache entityCache) {
        this.dbSqlSessionFactory = dbSqlSessionFactory;
        this.sqlSession = dbSqlSessionFactory.getSqlSessionFactory().openSession();
        this.entityCache = entityCache;
        this.connectionMetadataDefaultCatalog = dbSqlSessionFactory.getDatabaseCatalog();
        this.connectionMetadataDefaultSchema = dbSqlSessionFactory.getDatabaseSchema();
    }

    public void performSchemaOperationsProcessEngineBuild() {
        String databaseSchemaUpdate = Context.getProcessEngineConfiguration().getDatabaseSchemaUpdate();
        log.debug("Executing performSchemaOperationsProcessEngineBuild with setting " + databaseSchemaUpdate);
        /*if (ProcessEngineConfigurationImpl.DB_SCHEMA_UPDATE_DROP_CREATE.equals(databaseSchemaUpdate)) {
            try {
                dbSchemaDrop();
            } catch (RuntimeException e) {
                // ignore
            }
        }
        if (org.activiti.engine.ProcessEngineConfiguration.DB_SCHEMA_UPDATE_CREATE_DROP.equals(databaseSchemaUpdate)
                || ProcessEngineConfigurationImpl.DB_SCHEMA_UPDATE_DROP_CREATE.equals(databaseSchemaUpdate) || ProcessEngineConfigurationImpl.DB_SCHEMA_UPDATE_CREATE.equals(databaseSchemaUpdate)) {
            dbSchemaCreate();

        } else if (org.activiti.engine.ProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE.equals(databaseSchemaUpdate)) {
            dbSchemaCheckVersion();

        } else if (ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE.equals(databaseSchemaUpdate)) {
            dbSchemaUpdate();
        }*/
    }

    public SqlSession getSqlSession() {
        return sqlSession;
    }

    public DbSqlSessionFactory getDbSqlSessionFactory() {
        return dbSqlSessionFactory;
    }

    @Override
    public void flush() {
//        determineUpdatedObjects(); // Needs to be done before the removeUnnecessaryOperations, as removeUnnecessaryOperations will remove stuff from the cache
//        removeUnnecessaryOperations();
//
//        if (log.isDebugEnabled()) {
//            debugFlush();
//        }
//
//        flushInserts();
//        flushUpdates();
//        flushDeletes();
    }


    public void close() {
        sqlSession.close();
    }

    public void commit() {
        sqlSession.commit();
    }

    public void rollback() {
        sqlSession.rollback();
    }
}
