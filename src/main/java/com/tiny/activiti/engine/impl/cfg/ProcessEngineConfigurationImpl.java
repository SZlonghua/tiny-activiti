package com.tiny.activiti.engine.impl.cfg;

import com.tiny.activiti.engine.ActivitiException;
import com.tiny.activiti.engine.ProcessEngine;
import com.tiny.activiti.engine.ProcessEngineConfiguration;
import com.tiny.activiti.engine.impl.ProcessEngineImpl;
import com.tiny.activiti.engine.impl.db.DbIdGenerator;
import com.tiny.activiti.engine.impl.db.DbSqlSessionFactory;
import com.tiny.activiti.engine.impl.db.IbatisVariableTypeHandler;
import com.tiny.activiti.engine.impl.interceptor.CommandConfig;
import com.tiny.activiti.engine.impl.interceptor.CommandExecutor;
import com.tiny.activiti.engine.impl.interceptor.SessionFactory;
import com.tiny.activiti.engine.impl.util.IoUtil;
import com.tiny.activiti.engine.impl.util.ReflectUtil;
import com.tiny.activiti.engine.impl.variable.VariableType;
import com.tiny.activiti.engine.impl.variable.VariableTypes;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.apache.ibatis.type.JdbcType;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
public abstract class ProcessEngineConfigurationImpl extends ProcessEngineConfiguration {

    public static final String DEFAULT_MYBATIS_MAPPING_FILE = "com/tiny/activiti/db/mapping/mappings.xml";

    // MYBATIS SQL SESSION FACTORY //////////////////////////////////////////////

    protected SqlSessionFactory sqlSessionFactory;
    protected TransactionFactory transactionFactory;

    // SESSION FACTORIES /////////////////////////////////////////////////////////

    protected List<SessionFactory> customSessionFactories;
    protected DbSqlSessionFactory dbSqlSessionFactory;
    protected Map<Class<?>, SessionFactory> sessionFactories;

    // ID GENERATOR ///////////////////////////////////////////////////////////////

    protected IdGenerator idGenerator;
    protected DataSource idGeneratorDataSource;

    // OTHER //////////////////////////////////////////////////////////////////////

    protected VariableTypes variableTypes;

    protected Map<Object, Object> beans;

    protected CommandExecutor commandExecutor;

    // COMMAND EXECUTORS ////////////////////////////////////////////////////////

    protected CommandConfig defaultCommandConfig;
    protected CommandConfig schemaCommandConfig;



    protected boolean isBulkInsertEnabled = true;

    protected int maxNrOfStatementsInBulkInsert = 100;

    public Map<Object, Object> getBeans() {
        return beans;
    }

    public ProcessEngineConfigurationImpl setBeans(Map<Object, Object> beans) {
        this.beans = beans;
        return this;
    }

    @Override
    public ProcessEngine buildProcessEngine() {
        init();
        ProcessEngineImpl processEngine = new ProcessEngineImpl(this);
        postProcessEngineInitialisation();

        return processEngine;
    }

    protected void postProcessEngineInitialisation() {
        log.info("ProcessEngine post init");
    }

    public void init() {
        log.info("ProcessEngine init");
        initDataSource();

        initCommandExecutors();
        initIdGenerator();

        initTransactionFactory();

        initSqlSessionFactory();

        initSessionFactories();
    }

    public void initIdGenerator() {
        if (idGenerator == null) {
            CommandExecutor idGeneratorCommandExecutor = getCommandExecutor();

            DbIdGenerator dbIdGenerator = new DbIdGenerator();
            dbIdGenerator.setIdBlockSize(idBlockSize);
            dbIdGenerator.setCommandExecutor(idGeneratorCommandExecutor);
            dbIdGenerator.setCommandConfig(getDefaultCommandConfig().transactionRequiresNew());
            idGenerator = dbIdGenerator;
        }
    }

    public CommandConfig getDefaultCommandConfig() {
        return defaultCommandConfig;
    }

    public void setDefaultCommandConfig(CommandConfig defaultCommandConfig) {
        this.defaultCommandConfig = defaultCommandConfig;
    }

    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    public ProcessEngineConfigurationImpl setCommandExecutor(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
        return this;
    }

    public void initSessionFactories() {
        if (sessionFactories == null) {
            sessionFactories = new HashMap<Class<?>, SessionFactory>();
            initDbSqlSessionFactory();
//            addSessionFactory(new GenericManagerFactory(EntityCache.class, EntityCacheImpl.class));
        }
    }

    public void initDbSqlSessionFactory() {
        if (dbSqlSessionFactory == null) {
            dbSqlSessionFactory = createDbSqlSessionFactory();
        }
        dbSqlSessionFactory.setDatabaseType(databaseType);
        dbSqlSessionFactory.setIdGenerator(idGenerator);
        dbSqlSessionFactory.setSqlSessionFactory(sqlSessionFactory);
        dbSqlSessionFactory.setDbIdentityUsed(isDbIdentityUsed);
        dbSqlSessionFactory.setDbHistoryUsed(isDbHistoryUsed);
        dbSqlSessionFactory.setDatabaseTablePrefix(databaseTablePrefix);
        dbSqlSessionFactory.setTablePrefixIsSchema(tablePrefixIsSchema);
        dbSqlSessionFactory.setDatabaseCatalog(databaseCatalog);
        dbSqlSessionFactory.setDatabaseSchema(databaseSchema);
        dbSqlSessionFactory.setBulkInsertEnabled(isBulkInsertEnabled, databaseType);
        dbSqlSessionFactory.setMaxNrOfStatementsInBulkInsert(maxNrOfStatementsInBulkInsert);
        addSessionFactory(dbSqlSessionFactory);
    }

    public DbSqlSessionFactory createDbSqlSessionFactory() {
        return new DbSqlSessionFactory();
    }

    public void addSessionFactory(SessionFactory sessionFactory) {
        sessionFactories.put(sessionFactory.getSessionType(), sessionFactory);
    }

    public void initSqlSessionFactory() {
        if (sqlSessionFactory == null) {
            InputStream inputStream = null;
            try {
                inputStream = getMyBatisXmlConfigurationStream();

                Environment environment = new Environment("default", transactionFactory, dataSource);
                Reader reader = new InputStreamReader(inputStream);
                Properties properties = new Properties();
                properties.put("prefix", databaseTablePrefix);

                //set default properties
                properties.put("limitBefore" , "");
                properties.put("limitAfter" , "");
                properties.put("limitBetween" , "");
                properties.put("limitOuterJoinBetween" , "");
                properties.put("limitBeforeNativeQuery" , "");
                properties.put("orderBy" , "order by ${orderByColumns}");
                properties.put("blobType" , "BLOB");
                properties.put("boolValue" , "TRUE");

                if (databaseType != null) {
                    properties.load(getResourceAsStream("com/tiny/activiti/db/properties/"+databaseType+".properties"));
                }

                Configuration configuration = initMybatisConfiguration(environment, reader, properties);
                sqlSessionFactory = new DefaultSqlSessionFactory(configuration);

            } catch (Exception e) {
                throw new ActivitiException("Error while building ibatis SqlSessionFactory: " + e.getMessage(), e);
            } finally {
                IoUtil.closeSilently(inputStream);
            }
        }
    }

    public Configuration initMybatisConfiguration(Environment environment, Reader reader, Properties properties) {
        XMLConfigBuilder parser = new XMLConfigBuilder(reader, "", properties);
        Configuration configuration = parser.getConfiguration();

        if(databaseType != null) {
            configuration.setDatabaseId(databaseType);
        }

        configuration.setEnvironment(environment);

        initMybatisTypeHandlers(configuration);
//        initCustomMybatisMappers(configuration);

//        configuration = parseMybatisConfiguration(configuration, parser);
        return configuration;
    }

    public void initMybatisTypeHandlers(Configuration configuration) {
        configuration.getTypeHandlerRegistry().register(VariableType.class, JdbcType.VARCHAR, new IbatisVariableTypeHandler());
    }

    public InputStream getMyBatisXmlConfigurationStream() {
        return getResourceAsStream(DEFAULT_MYBATIS_MAPPING_FILE);
    }
    protected InputStream getResourceAsStream(String resource) {
        return ReflectUtil.getResourceAsStream(resource);
    }

    public void initTransactionFactory() {
        if (transactionFactory == null) {
            if (transactionsExternallyManaged) {
                transactionFactory = new ManagedTransactionFactory();
            } else {
                transactionFactory = new JdbcTransactionFactory();
            }
        }
    }

    public void initDataSource() {
        if (dataSource == null) {
            if (jdbcUrl != null) {
                if ((jdbcDriver == null) || (jdbcUsername == null)) {
                    throw new ActivitiException("DataSource or JDBC properties have to be specified in a process engine configuration");
                }

                log.debug("initializing datasource to db: {}", jdbcUrl);

                PooledDataSource pooledDataSource = new PooledDataSource(ReflectUtil.getClassLoader(), jdbcDriver, jdbcUrl, jdbcUsername, jdbcPassword);

                if (jdbcPingEnabled == true) {
                    pooledDataSource.setPoolPingEnabled(true);
                    if (jdbcPingQuery != null) {
                        pooledDataSource.setPoolPingQuery(jdbcPingQuery);
                    }
                    pooledDataSource.setPoolPingConnectionsNotUsedFor(jdbcPingConnectionNotUsedFor);
                }
                if (jdbcDefaultTransactionIsolationLevel > 0) {
                    pooledDataSource.setDefaultTransactionIsolationLevel(jdbcDefaultTransactionIsolationLevel);
                }
                dataSource = pooledDataSource;
            }

            if (dataSource instanceof PooledDataSource) {
                // ACT-233: connection pool of Ibatis is not properly
                // initialized if this is not called!
                ((PooledDataSource) dataSource).forceCloseAll();
            }
        }

        if (databaseType == null) {
            initDatabaseType();
        }
    }

    protected static Properties databaseTypeMappings = getDefaultDatabaseTypeMappings();

    public static final String DATABASE_TYPE_H2 = "h2";
    public static final String DATABASE_TYPE_HSQL = "hsql";
    public static final String DATABASE_TYPE_MYSQL = "mysql";
    public static final String DATABASE_TYPE_ORACLE = "oracle";
    public static final String DATABASE_TYPE_POSTGRES = "postgres";
    public static final String DATABASE_TYPE_MSSQL = "mssql";
    public static final String DATABASE_TYPE_DB2 = "db2";

    public static Properties getDefaultDatabaseTypeMappings() {
        Properties databaseTypeMappings = new Properties();
        databaseTypeMappings.setProperty("H2", DATABASE_TYPE_H2);
        databaseTypeMappings.setProperty("HSQL Database Engine", DATABASE_TYPE_HSQL);
        databaseTypeMappings.setProperty("MySQL", DATABASE_TYPE_MYSQL);
        databaseTypeMappings.setProperty("Oracle", DATABASE_TYPE_ORACLE);
        databaseTypeMappings.setProperty("PostgreSQL", DATABASE_TYPE_POSTGRES);
        databaseTypeMappings.setProperty("Microsoft SQL Server", DATABASE_TYPE_MSSQL);
        databaseTypeMappings.setProperty(DATABASE_TYPE_DB2, DATABASE_TYPE_DB2);
        databaseTypeMappings.setProperty("DB2",DATABASE_TYPE_DB2);
        databaseTypeMappings.setProperty("DB2/NT", DATABASE_TYPE_DB2);
        databaseTypeMappings.setProperty("DB2/NT64", DATABASE_TYPE_DB2);
        databaseTypeMappings.setProperty("DB2 UDP", DATABASE_TYPE_DB2);
        databaseTypeMappings.setProperty("DB2/LINUX", DATABASE_TYPE_DB2);
        databaseTypeMappings.setProperty("DB2/LINUX390", DATABASE_TYPE_DB2);
        databaseTypeMappings.setProperty("DB2/LINUXX8664", DATABASE_TYPE_DB2);
        databaseTypeMappings.setProperty("DB2/LINUXZ64", DATABASE_TYPE_DB2);
        databaseTypeMappings.setProperty("DB2/LINUXPPC64",DATABASE_TYPE_DB2);
        databaseTypeMappings.setProperty("DB2/LINUXPPC64LE",DATABASE_TYPE_DB2);
        databaseTypeMappings.setProperty("DB2/400 SQL", DATABASE_TYPE_DB2);
        databaseTypeMappings.setProperty("DB2/6000", DATABASE_TYPE_DB2);
        databaseTypeMappings.setProperty("DB2 UDB iSeries", DATABASE_TYPE_DB2);
        databaseTypeMappings.setProperty("DB2/AIX64", DATABASE_TYPE_DB2);
        databaseTypeMappings.setProperty("DB2/HPUX", DATABASE_TYPE_DB2);
        databaseTypeMappings.setProperty("DB2/HP64", DATABASE_TYPE_DB2);
        databaseTypeMappings.setProperty("DB2/SUN", DATABASE_TYPE_DB2);
        databaseTypeMappings.setProperty("DB2/SUN64", DATABASE_TYPE_DB2);
        databaseTypeMappings.setProperty("DB2/PTX", DATABASE_TYPE_DB2);
        databaseTypeMappings.setProperty("DB2/2", DATABASE_TYPE_DB2);
        databaseTypeMappings.setProperty("DB2 UDB AS400", DATABASE_TYPE_DB2);
        return databaseTypeMappings;
    }

    public void initDatabaseType() {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            String databaseProductName = databaseMetaData.getDatabaseProductName();
            log.debug("database product name: '{}'", databaseProductName);
            databaseType = databaseTypeMappings.getProperty(databaseProductName);
            if (databaseType == null) {
                throw new ActivitiException("couldn't deduct database type from database product name '" + databaseProductName + "'");
            }
            log.debug("using database type: {}", databaseType);

        } catch (SQLException e) {
            log.error("Exception while initializing Database connection", e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                log.error("Exception while closing the Database connection", e);
            }
        }
    }

    public VariableTypes getVariableTypes() {
        return variableTypes;
    }

    public ProcessEngineConfigurationImpl setVariableTypes(VariableTypes variableTypes) {
        this.variableTypes = variableTypes;
        return this;
    }

    // command executors
    // ////////////////////////////////////////////////////////

    public void initCommandExecutors() {
        initDefaultCommandConfig();
        initSchemaCommandConfig();
//        initCommandInvoker();
//        initCommandInterceptors();
        initCommandExecutor();
    }

    public void initDefaultCommandConfig() {
        if (defaultCommandConfig == null) {
            defaultCommandConfig = new CommandConfig();
        }
    }

    public void initSchemaCommandConfig() {
        if (schemaCommandConfig == null) {
            schemaCommandConfig = new CommandConfig().transactionNotSupported();
        }
    }

    public void initCommandExecutor() {
        if (commandExecutor == null) {
            log.info("initCommandExecutor");
            /*CommandInterceptor first = initInterceptorChain(commandInterceptors);
            commandExecutor = new CommandExecutorImpl(getDefaultCommandConfig(), first);*/
        }
    }
}
