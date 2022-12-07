package com.tiny.activiti.engine.impl.interceptor;

import com.tiny.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;

public class CommandContextFactory {
    protected ProcessEngineConfigurationImpl processEngineConfiguration;

    public CommandContext createCommandContext(Command<?> cmd) {
        return new CommandContext(cmd, processEngineConfiguration);
    }

    public ProcessEngineConfigurationImpl getProcessEngineConfiguration() {
        return processEngineConfiguration;
    }

    public void setProcessEngineConfiguration(ProcessEngineConfigurationImpl processEngineConfiguration) {
        this.processEngineConfiguration = processEngineConfiguration;
    }
}
