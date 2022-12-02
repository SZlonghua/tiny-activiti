package com.tiny.activiti.engine.impl;

import com.tiny.activiti.engine.ProcessEngine;
import com.tiny.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;

import java.util.Map;

public class ProcessEngineImpl implements ProcessEngine {
    protected String name;
    /*protected RepositoryService repositoryService;
    protected RuntimeService runtimeService;
    protected HistoryService historicDataService;
    protected IdentityService identityService;
    protected TaskService taskService;
    protected FormService formService;
    protected ManagementService managementService;
    protected DynamicBpmnService dynamicBpmnService;
    protected FormRepositoryService formEngineRepositoryService;
    protected org.activiti.form.api.FormService formEngineFormService;
    protected AsyncExecutor asyncExecutor;
    protected CommandExecutor commandExecutor;
    protected Map<Class<?>, SessionFactory> sessionFactories;
    protected TransactionContextFactory transactionContextFactory;*/
    protected ProcessEngineConfigurationImpl processEngineConfiguration;

    public ProcessEngineImpl(ProcessEngineConfigurationImpl processEngineConfiguration) {
        this.processEngineConfiguration = processEngineConfiguration;
        this.name = processEngineConfiguration.getProcessEngineName();
        /*this.repositoryService = processEngineConfiguration.getRepositoryService();
        this.runtimeService = processEngineConfiguration.getRuntimeService();
        this.historicDataService = processEngineConfiguration.getHistoryService();
        this.identityService = processEngineConfiguration.getIdentityService();
        this.taskService = processEngineConfiguration.getTaskService();
        this.formService = processEngineConfiguration.getFormService();
        this.managementService = processEngineConfiguration.getManagementService();
        this.dynamicBpmnService = processEngineConfiguration.getDynamicBpmnService();
        this.asyncExecutor = processEngineConfiguration.getAsyncExecutor();
        this.commandExecutor = processEngineConfiguration.getCommandExecutor();
        this.sessionFactories = processEngineConfiguration.getSessionFactories();
        this.transactionContextFactory = processEngineConfiguration.getTransactionContextFactory();
        this.formEngineRepositoryService = processEngineConfiguration.getFormEngineRepositoryService();
        this.formEngineFormService = processEngineConfiguration.getFormEngineFormService();

        if (processEngineConfiguration.isUsingRelationalDatabase() && processEngineConfiguration.getDatabaseSchemaUpdate() != null) {
            commandExecutor.execute(processEngineConfiguration.getSchemaCommandConfig(), new SchemaOperationsProcessEngineBuild());
        }

        if (name == null) {
            log.info("default activiti ProcessEngine created");
        } else {
            log.info("ProcessEngine {} created", name);
        }

        ProcessEngines.registerProcessEngine(this);

        if (asyncExecutor != null && asyncExecutor.isAutoActivate()) {
            asyncExecutor.start();
        }

        if (processEngineConfiguration.getProcessEngineLifecycleListener() != null) {
            processEngineConfiguration.getProcessEngineLifecycleListener().onProcessEngineBuilt(this);
        }

        processEngineConfiguration.getEventDispatcher()
                .dispatchEvent(ActivitiEventBuilder.createGlobalEvent(ActivitiEventType.ENGINE_CREATED));*/
    }

    public String getName() {
        return name;
    }
}
