package com.tiny.activiti.engine.impl.interceptor;

import com.tiny.activiti.engine.impl.cfg.TransactionContext;

public class TransactionCommandContextCloseListener implements CommandContextCloseListener{
    protected TransactionContext transactionContext;

    public TransactionCommandContextCloseListener(TransactionContext transactionContext) {
        this.transactionContext = transactionContext;
    }

    @Override
    public void closing(CommandContext commandContext) {

    }

    @Override
    public void afterSessionsFlush(CommandContext commandContext) {
        transactionContext.commit();
    }

    @Override
    public void closed(CommandContext commandContext) {

    }

    @Override
    public void closeFailure(CommandContext commandContext) {
        transactionContext.rollback();
    }
}
