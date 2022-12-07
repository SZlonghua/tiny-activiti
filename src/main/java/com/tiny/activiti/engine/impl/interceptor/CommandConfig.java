package com.tiny.activiti.engine.impl.interceptor;

import com.tiny.activiti.engine.impl.cfg.TransactionPropagation;

public class CommandConfig {
    private boolean contextReusePossible;
    private TransactionPropagation propagation;

    public CommandConfig() {
        this.contextReusePossible = true;
        this.propagation = TransactionPropagation.REQUIRED;
    }

    public CommandConfig(boolean contextReusePossible, TransactionPropagation transactionPropagation) {
        this.contextReusePossible = contextReusePossible;
        this.propagation = transactionPropagation;
    }

    public CommandConfig transactionRequiresNew() {
        CommandConfig config = new CommandConfig();
        config.contextReusePossible = false;
        config.propagation = TransactionPropagation.REQUIRES_NEW;
        return config;
    }

    public CommandConfig transactionNotSupported() {
        CommandConfig config = new CommandConfig();
        config.contextReusePossible = false;
        config.propagation = TransactionPropagation.NOT_SUPPORTED;
        return config;
    }

    public boolean isContextReusePossible() {
        return contextReusePossible;
    }
}
