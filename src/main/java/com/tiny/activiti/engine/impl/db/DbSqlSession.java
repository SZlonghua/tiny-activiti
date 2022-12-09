package com.tiny.activiti.engine.impl.db;

import com.tiny.activiti.engine.ActivitiException;
import com.tiny.activiti.engine.ActivitiOptimisticLockingException;
import com.tiny.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import com.tiny.activiti.engine.impl.context.Context;
import com.tiny.activiti.engine.impl.interceptor.Session;
import com.tiny.activiti.engine.impl.persistence.cache.CachedEntity;
import com.tiny.activiti.engine.impl.persistence.cache.EntityCache;
import com.tiny.activiti.engine.impl.persistence.entity.Entity;
import com.tiny.activiti.engine.impl.persistence.entity.ExecutionEntity;
import com.tiny.activiti.engine.impl.persistence.entity.PropertyEntity;
import com.tiny.activiti.engine.impl.util.IoUtil;
import com.tiny.activiti.engine.impl.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

@Slf4j
public class DbSqlSession implements Session {

    protected SqlSession sqlSession;
    protected DbSqlSessionFactory dbSqlSessionFactory;
    protected EntityCache entityCache;

    protected Map<Class<? extends Entity>, Map<String, Entity>> insertedObjects
            = new HashMap<Class<? extends Entity>, Map<String, Entity>>();
    protected Map<Class<? extends Entity>, Map<String, Entity>> deletedObjects
            = new HashMap<Class<? extends Entity>, Map<String, Entity>>();
    protected Map<Class<? extends Entity>, List<BulkDeleteOperation>> bulkDeleteOperations
            = new HashMap<Class<? extends Entity>, List<BulkDeleteOperation>>();
    protected List<Entity> updatedObjects = new ArrayList<Entity>();

    protected String connectionMetadataDefaultCatalog;
    protected String connectionMetadataDefaultSchema;


    public static String[] JDBC_METADATA_TABLE_TYPES = { "TABLE" };

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
        dbSchemaUpdate();
    }

    public String dbSchemaUpdate() {
        if (!isEngineTablePresent()) {
            dbSchemaCreateEngine();
        }
        if (!isHistoryTablePresent()) {
            dbSchemaCreateHistory();
        }
        if (!isIdentityTablePresent()) {
            dbSchemaCreateIdentity();
        }
        return null;
    }

    protected void dbSchemaCreateIdentity() {
        executeMandatorySchemaResource("create", "identity");
    }

    protected void dbSchemaCreateHistory() {
        executeMandatorySchemaResource("create", "history");
    }

    protected void dbSchemaCreateEngine() {
        executeMandatorySchemaResource("create", "engine");
    }

    public void executeMandatorySchemaResource(String operation, String component) {
        executeSchemaResource(operation, component, getResourceForDbOperation(operation, operation, component), false);
    }

    public void executeSchemaResource(String operation, String component, String resourceName, boolean isOptional) {
        InputStream inputStream = null;
        try {
            inputStream = ReflectUtil.getResourceAsStream(resourceName);
            if (inputStream == null) {
                if (isOptional) {
                    log.info("no schema resource {} for {}", resourceName, operation);
                } else {
                    throw new ActivitiException("resource '" + resourceName + "' is not available");
                }
            } else {
                executeSchemaResource(operation, component, resourceName, inputStream);
            }

        } finally {
            IoUtil.closeSilently(inputStream);
        }
    }

    private void executeSchemaResource(String operation, String component, String resourceName, InputStream inputStream) {
        log.info("performing {} on {} with resource {}", operation, component, resourceName);
        String sqlStatement = null;
        String exceptionSqlStatement = null;
        try {
            Connection connection = sqlSession.getConnection();
            Exception exception = null;
            byte[] bytes = IoUtil.readInputStream(inputStream, resourceName);
            String ddlStatements = new String(bytes);

            // Special DDL handling for certain databases
            try {
                if (isMysql()) {
                    DatabaseMetaData databaseMetaData = connection.getMetaData();
                    int majorVersion = databaseMetaData.getDatabaseMajorVersion();
                    int minorVersion = databaseMetaData.getDatabaseMinorVersion();
                    log.info("Found MySQL: majorVersion=" + majorVersion + " minorVersion=" + minorVersion);

                    // Special care for MySQL < 5.6
                    if (majorVersion <= 5 && minorVersion < 6) {
                        throw new ActivitiException("MySQL must not be luwer 5.6, Not support mysql version"+majorVersion+minorVersion);
                    }
                }
            } catch (Exception e) {
                log.info("Could not get database metadata", e);
            }

            BufferedReader reader = new BufferedReader(new StringReader(ddlStatements));
            String line = readNextTrimmedLine(reader);
            boolean inOraclePlsqlBlock = false;
            while (line != null) {
                if (line.startsWith("# ")) {
                    log.debug(line.substring(2));

                } else if (line.startsWith("-- ")) {
                    log.debug(line.substring(3));

                } else if (line.length() > 0) {

                    if (isOracle() && line.startsWith("begin")) {
                        inOraclePlsqlBlock = true;
                        sqlStatement = addSqlStatementPiece(sqlStatement, line);

                    } else if ((line.endsWith(";") && inOraclePlsqlBlock == false) || (line.startsWith("/") && inOraclePlsqlBlock == true)) {

                        if (inOraclePlsqlBlock) {
                            inOraclePlsqlBlock = false;
                        } else {
                            sqlStatement = addSqlStatementPiece(sqlStatement, line.substring(0, line.length() - 1));
                        }

                        Statement jdbcStatement = connection.createStatement();
                        try {
                            // no logging needed as the connection will log it
                            log.debug("SQL: {}", sqlStatement);
                            jdbcStatement.execute(sqlStatement);
                            jdbcStatement.close();
                        } catch (Exception e) {
                            if (exception == null) {
                                exception = e;
                                exceptionSqlStatement = sqlStatement;
                            }
                            log.error("problem during schema {}, statement {}", operation, sqlStatement, e);
                        } finally {
                            sqlStatement = null;
                        }
                    } else {
                        sqlStatement = addSqlStatementPiece(sqlStatement, line);
                    }
                }

                line = readNextTrimmedLine(reader);
            }

            if (exception != null) {
                throw exception;
            }

            log.debug("activiti db schema {} for component {} successful", operation, component);

        } catch (Exception e) {
            throw new ActivitiException("couldn't " + operation + " db schema: " + exceptionSqlStatement, e);
        }
    }

    protected String addSqlStatementPiece(String sqlStatement, String line) {
        if (sqlStatement == null) {
            return line;
        }
        return sqlStatement + " \n" + line;
    }

    protected String readNextTrimmedLine(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line != null) {
            line = line.trim();
        }
        return line;
    }

    public boolean isMysql() {
        return dbSqlSessionFactory.getDatabaseType().equals("mysql");
    }

    public boolean isOracle() {
        return dbSqlSessionFactory.getDatabaseType().equals("oracle");
    }

    public String getResourceForDbOperation(String directory, String operation, String component) {
        String databaseType = dbSqlSessionFactory.getDatabaseType();
        return "com/tiny/activiti/db/" + directory + "/activiti." + databaseType + "." + operation + "." + component + ".sql";
    }


    public <T extends Entity> T selectById(Class<T> entityClass, String id) {
        return selectById(entityClass, id, true);
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> T selectById(Class<T> entityClass, String id, boolean useCache) {
        T entity = null;

        if (useCache) {
            entity = entityCache.findInCache(entityClass, id);
            if (entity != null) {
                return entity;
            }
        }

        String selectStatement = dbSqlSessionFactory.getSelectStatement(entityClass);
        selectStatement = dbSqlSessionFactory.mapStatement(selectStatement);
        entity = (T) sqlSession.selectOne(selectStatement, id);
        if (entity == null) {
            return null;
        }

        entityCache.put(entity, true); // true -> store state so we can see later if it is updated later on
        return entity;
    }

    public boolean isEngineTablePresent() {
        return isTablePresent("ACT_RU_EXECUTION");
    }

    public boolean isHistoryTablePresent() {
        return isTablePresent("ACT_HI_PROCINST");
    }

    public boolean isIdentityTablePresent() {
        return isTablePresent("ACT_ID_USER");
    }

    public boolean isTablePresent(String tableName) {
        // ACT-1610: in case the prefix IS the schema itself, we don't add the
        // prefix, since the check is already aware of the schema
        if (!dbSqlSessionFactory.isTablePrefixIsSchema()) {
            tableName = prependDatabaseTablePrefix(tableName);
        }

        Connection connection = null;
        try {
            connection = sqlSession.getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet tables = null;

            String catalog = this.connectionMetadataDefaultCatalog;
            if (dbSqlSessionFactory.getDatabaseCatalog() != null && dbSqlSessionFactory.getDatabaseCatalog().length() > 0) {
                catalog = dbSqlSessionFactory.getDatabaseCatalog();
            }

            String schema = this.connectionMetadataDefaultSchema;
            if (dbSqlSessionFactory.getDatabaseSchema() != null && dbSqlSessionFactory.getDatabaseSchema().length() > 0) {
                schema = dbSqlSessionFactory.getDatabaseSchema();
            }

            String databaseType = dbSqlSessionFactory.getDatabaseType();

            if ("postgres".equals(databaseType)) {
                tableName = tableName.toLowerCase();
            }

            if (schema != null && "oracle".equals(databaseType)) {
                schema = schema.toUpperCase();
            }

            if (catalog != null && catalog.length() == 0) {
                catalog = null;
            }

            try {
                tables = databaseMetaData.getTables(catalog, schema, tableName, JDBC_METADATA_TABLE_TYPES);
                return tables.next();
            } finally {
                try {
                    tables.close();
                } catch (Exception e) {
                    log.error("Error closing meta data tables", e);
                }
            }

        } catch (Exception e) {
            throw new ActivitiException("couldn't check if tables are already present using metadata: " + e.getMessage(), e);
        }
    }

    protected String prependDatabaseTablePrefix(String tableName) {
        return dbSqlSessionFactory.getDatabaseTablePrefix() + tableName;
    }

    public SqlSession getSqlSession() {
        return sqlSession;
    }

    public DbSqlSessionFactory getDbSqlSessionFactory() {
        return dbSqlSessionFactory;
    }

    @Override
    public void flush() {
        determineUpdatedObjects(); // Needs to be done before the removeUnnecessaryOperations, as removeUnnecessaryOperations will remove stuff from the cache
        removeUnnecessaryOperations();

        if (log.isDebugEnabled()) {
            debugFlush();
        }

        flushInserts();
        flushUpdates();
        flushDeletes();
    }

    protected void debugFlush() {
        log.debug("Flushing dbSqlSession");
        int nrOfInserts = 0, nrOfUpdates = 0, nrOfDeletes = 0;
        for (Map<String, Entity> insertedObjectMap: insertedObjects.values()) {
            for (Entity insertedObject : insertedObjectMap.values()) {
                log.debug("  insert {}", insertedObject);
                nrOfInserts++;
            }
        }
        for (Entity updatedObject : updatedObjects) {
            log.debug("  update {}", updatedObject);
            nrOfUpdates++;
        }
        for (Map<String, Entity> deletedObjectMap: deletedObjects.values()) {
            for (Entity deletedObject : deletedObjectMap.values()) {
                log.debug("  delete {} with id {}", deletedObject, deletedObject.getId());
                nrOfDeletes++;
            }
        }
        for (Collection<BulkDeleteOperation> bulkDeleteOperationList : bulkDeleteOperations.values()) {
            for (BulkDeleteOperation bulkDeleteOperation : bulkDeleteOperationList) {
                log.debug("  {}", bulkDeleteOperation);
                nrOfDeletes++;
            }
        }
        log.debug("flush summary: {} insert, {} update, {} delete.", nrOfInserts, nrOfUpdates, nrOfDeletes);
        log.debug("now executing flush...");
    }

    protected void flushInserts() {

        if (insertedObjects.size() == 0) {
            return;
        }

        // Handle in entity dependency order
        for (Class<? extends Entity> entityClass : EntityDependencyOrder.INSERT_ORDER) {
            if (insertedObjects.containsKey(entityClass)) {
                flushInsertEntities(entityClass, insertedObjects.get(entityClass).values());
                insertedObjects.remove(entityClass);
            }
        }

        // Next, in case of custom entities or we've screwed up and forgotten some entity
        if (insertedObjects.size() > 0) {
            for (Class<? extends Entity> entityClass : insertedObjects.keySet()) {
                flushInsertEntities(entityClass, insertedObjects.get(entityClass).values());
            }
        }

        insertedObjects.clear();
    }
    protected void flushInsertEntities(Class<? extends Entity> entityClass, Collection<Entity> entitiesToInsert) {
        if (entitiesToInsert.size() == 1) {
            flushRegularInsert(entitiesToInsert.iterator().next(), entityClass);
        } else if (Boolean.FALSE.equals(dbSqlSessionFactory.isBulkInsertable(entityClass))) {
            for (Entity entity : entitiesToInsert) {
                flushRegularInsert(entity, entityClass);
            }
        }	else {
            flushBulkInsert(entitiesToInsert, entityClass);
        }
    }
    protected void flushRegularInsert(Entity entity, Class<? extends Entity> clazz) {
        String insertStatement = dbSqlSessionFactory.getInsertStatement(entity);
        insertStatement = dbSqlSessionFactory.mapStatement(insertStatement);

        if (insertStatement==null) {
            throw new ActivitiException("no insert statement for " + entity.getClass() + " in the ibatis mapping files");
        }

        log.debug("inserting: {}", entity);
        sqlSession.insert(insertStatement, entity);

        // See https://activiti.atlassian.net/browse/ACT-1290
        if (entity instanceof HasRevision) {
            incrementRevision(entity);
        }
    }

    protected void flushBulkInsert(Collection<Entity> entities, Class<? extends Entity> clazz) {
        String insertStatement = dbSqlSessionFactory.getBulkInsertStatement(clazz);
        insertStatement = dbSqlSessionFactory.mapStatement(insertStatement);

        if (insertStatement==null) {
            throw new ActivitiException("no insert statement for " + entities.iterator().next().getClass() + " in the ibatis mapping files");
        }

        Iterator<Entity> entityIterator = entities.iterator();
        Boolean hasRevision = null;

        while (entityIterator.hasNext()) {
            List<Entity> subList = new ArrayList<Entity>();
            int index = 0;
            while (entityIterator.hasNext() && index < dbSqlSessionFactory.getMaxNrOfStatementsInBulkInsert()) {
                Entity entity = entityIterator.next();
                subList.add(entity);

                if (hasRevision == null) {
                    hasRevision = entity instanceof HasRevision;
                }
                index++;
            }
            sqlSession.insert(insertStatement, subList);
        }

        if (hasRevision != null && hasRevision) {
            entityIterator = entities.iterator();
            while (entityIterator.hasNext()) {
                incrementRevision(entityIterator.next());
            }
        }

    }

    protected void incrementRevision(Entity insertedObject) {
        HasRevision revisionEntity = (HasRevision) insertedObject;
        if (revisionEntity.getRevision() == 0) {
            revisionEntity.setRevision(revisionEntity.getRevisionNext());
        }
    }

    protected void flushUpdates() {
        for (Entity updatedObject : updatedObjects) {
            String updateStatement = dbSqlSessionFactory.getUpdateStatement(updatedObject);
            updateStatement = dbSqlSessionFactory.mapStatement(updateStatement);

            if (updateStatement == null) {
                throw new ActivitiException("no update statement for " + updatedObject.getClass() + " in the ibatis mapping files");
            }

            log.debug("updating: {}", updatedObject);
            int updatedRecords = sqlSession.update(updateStatement, updatedObject);
            if (updatedRecords == 0) {
                throw new ActivitiOptimisticLockingException(updatedObject + " was updated by another transaction concurrently");
            }

            // See https://activiti.atlassian.net/browse/ACT-1290
            if (updatedObject instanceof HasRevision) {
                ((HasRevision) updatedObject).setRevision(((HasRevision) updatedObject).getRevisionNext());
            }

        }
        updatedObjects.clear();
    }

    protected void flushDeletes() {

        if (deletedObjects.size() == 0 && bulkDeleteOperations.size() == 0) {
            return;
        }

        // Handle in entity dependency order
        for (Class<? extends Entity> entityClass : EntityDependencyOrder.DELETE_ORDER) {
            if (deletedObjects.containsKey(entityClass)) {
                flushDeleteEntities(entityClass, deletedObjects.get(entityClass).values());
                deletedObjects.remove(entityClass);
            }
            flushBulkDeletes(entityClass);
        }

        // Next, in case of custom entities or we've screwed up and forgotten some entity
        if (deletedObjects.size() > 0) {
            for (Class<? extends Entity> entityClass : deletedObjects.keySet()) {
                flushDeleteEntities(entityClass, deletedObjects.get(entityClass).values());
                flushBulkDeletes(entityClass);
            }
        }

        deletedObjects.clear();
    }

    protected void flushBulkDeletes(Class<? extends Entity> entityClass) {
        // Bulk deletes
        if (bulkDeleteOperations.containsKey(entityClass)) {
            for (BulkDeleteOperation bulkDeleteOperation : bulkDeleteOperations.get(entityClass)) {
                bulkDeleteOperation.execute(sqlSession);
            }
        }
    }

    protected void flushDeleteEntities(Class<? extends Entity> entityClass, Collection<Entity> entitiesToDelete) {
        for (Entity entity : entitiesToDelete) {
            String deleteStatement = dbSqlSessionFactory.getDeleteStatement(entity.getClass());
            deleteStatement = dbSqlSessionFactory.mapStatement(deleteStatement);
            if (deleteStatement == null) {
                throw new ActivitiException("no delete statement for " + entity.getClass() + " in the ibatis mapping files");
            }

            // It only makes sense to check for optimistic locking exceptions
            // for objects that actually have a revision
            if (entity instanceof HasRevision) {
                int nrOfRowsDeleted = sqlSession.delete(deleteStatement, entity);
                if (nrOfRowsDeleted == 0) {
                    throw new ActivitiOptimisticLockingException(entity + " was updated by another transaction concurrently");
                }
            } else {
                sqlSession.delete(deleteStatement, entity);
            }
        }
    }

    protected void removeUnnecessaryOperations() {

        for (Class<? extends Entity> entityClass : deletedObjects.keySet()) {

            // Collect ids of deleted entities + remove duplicates
            Set<String> ids = new HashSet<String>();
            Iterator<Entity> entitiesToDeleteIterator = deletedObjects.get(entityClass).values().iterator();
            while (entitiesToDeleteIterator.hasNext()) {
                Entity entityToDelete = entitiesToDeleteIterator.next();
                if (!ids.contains(entityToDelete.getId())) {
                    ids.add(entityToDelete.getId());
                } else {
                    entitiesToDeleteIterator.remove(); // Removing duplicate deletes
                }
            }

            // Now we have the deleted ids, we can remove the inserted objects (as they cancel each other)
            for (String id : ids) {
                if (insertedObjects.containsKey(entityClass) && insertedObjects.get(entityClass).containsKey(id)) {
                    insertedObjects.get(entityClass).remove(id);
                    deletedObjects.get(entityClass).remove(id);
                }
            }

        }
    }

    public void determineUpdatedObjects() {
        updatedObjects = new ArrayList<Entity>();
        Map<Class<?>, Map<String, CachedEntity>> cachedObjects = entityCache.getAllCachedEntities();
        for (Class<?> clazz : cachedObjects.keySet()) {

            Map<String, CachedEntity> classCache = cachedObjects.get(clazz);
            for (CachedEntity cachedObject : classCache.values()) {

                Entity cachedEntity = cachedObject.getEntity();

                // Executions are stored as a hierarchical tree, and updates are important to execute
                // even when the execution are deleted, as they can change the parent-child relationships.
                // For the other entities, this is not applicable and an update can be discarded when an update follows.

                if (!isEntityInserted(cachedEntity) &&
                        (ExecutionEntity.class.isAssignableFrom(cachedEntity.getClass()) || !isEntityToBeDeleted(cachedEntity)) &&
                        cachedObject.hasChanged()
                ) {
                    updatedObjects.add(cachedEntity);
                }
            }
        }
    }

    public boolean isEntityInserted(Entity entity) {
        return insertedObjects.containsKey(entity.getClass())
                && insertedObjects.get(entity.getClass()).containsKey(entity.getId());
    }

    public boolean isEntityToBeDeleted(Entity entity) {
        return deletedObjects.containsKey(entity.getClass())
                && deletedObjects.get(entity.getClass()).containsKey(entity.getId());
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
