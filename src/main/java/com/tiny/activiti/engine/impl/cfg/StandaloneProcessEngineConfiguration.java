package com.tiny.activiti.engine.impl.cfg;

import com.tiny.activiti.engine.impl.interceptor.CommandInterceptor;

public class StandaloneProcessEngineConfiguration extends ProcessEngineConfigurationImpl {
    @Override
    public CommandInterceptor createTransactionInterceptor() {
        return null;
    }
}
