package com.tiny.activiti.engine;

import com.tiny.activiti.engine.impl.cfg.BeansConfigurationHelper;

import javax.sql.DataSource;

public abstract class ProcessEngineConfiguration {

    public static final String DB_SCHEMA_UPDATE_FALSE = "false";
    public static final String DB_SCHEMA_UPDATE_CREATE_DROP = "create-drop";
    public static final String DB_SCHEMA_UPDATE_TRUE = "true";

    protected String processEngineName = ProcessEngines.NAME_DEFAULT;
    protected int idBlockSize = 2500;

    protected String databaseType;
    protected String databaseSchemaUpdate = DB_SCHEMA_UPDATE_FALSE;
    protected String jdbcDriver = "org.h2.Driver";
    protected String jdbcUrl = "jdbc:h2:tcp://localhost/~/activiti";
    protected String jdbcUsername = "sa";
    protected String jdbcPassword = "";
    protected boolean isDbIdentityUsed = true;
    protected boolean isDbHistoryUsed = true;

    protected boolean jdbcPingEnabled;
    protected String jdbcPingQuery;
    protected int jdbcPingConnectionNotUsedFor;
    protected int jdbcDefaultTransactionIsolationLevel;
    protected DataSource dataSource;
    protected boolean transactionsExternallyManaged;

    protected boolean tablePrefixIsSchema;

    protected ClassLoader classLoader;

    protected String databaseCatalog = "";
    protected String databaseSchema;


    protected String databaseTablePrefix = "";

    protected ActivitiEngineAgendaFactory engineAgendaFactory;

    public String getDatabaseSchemaUpdate() {
        return databaseSchemaUpdate;
    }

    public ProcessEngineConfiguration setDatabaseSchemaUpdate(String databaseSchemaUpdate) {
        this.databaseSchemaUpdate = databaseSchemaUpdate;
        return this;
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public ProcessEngineConfiguration setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
        return this;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public ProcessEngineConfiguration setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        return this;
    }

    public String getJdbcUsername() {
        return jdbcUsername;
    }

    public ProcessEngineConfiguration setJdbcUsername(String jdbcUsername) {
        this.jdbcUsername = jdbcUsername;
        return this;
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }

    public ProcessEngineConfiguration setJdbcPassword(String jdbcPassword) {
        this.jdbcPassword = jdbcPassword;
        return this;
    }

    public boolean isTransactionsExternallyManaged() {
        return transactionsExternallyManaged;
    }

    public ProcessEngineConfiguration setTransactionsExternallyManaged(boolean transactionsExternallyManaged) {
        this.transactionsExternallyManaged = transactionsExternallyManaged;
        return this;
    }


    public String getProcessEngineName() {
        return processEngineName;
    }
    public ProcessEngineConfiguration setProcessEngineName(String processEngineName) {
        this.processEngineName = processEngineName;
        return this;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public ProcessEngineConfiguration setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    public static ProcessEngineConfiguration createProcessEngineConfigurationFromResource(String resource) {
        return createProcessEngineConfigurationFromResource(resource, "processEngineConfiguration");
    }

    public static ProcessEngineConfiguration createProcessEngineConfigurationFromResource(String resource, String beanName) {
        return BeansConfigurationHelper.parseProcessEngineConfigurationFromResource(resource, beanName);
    }

    public abstract ProcessEngine buildProcessEngine();

    public void setEngineAgendaFactory(ActivitiEngineAgendaFactory engineAgendaFactory) {
        this.engineAgendaFactory = engineAgendaFactory;
    }

    public ActivitiEngineAgendaFactory getEngineAgendaFactory() {
        return engineAgendaFactory;
    }
}
