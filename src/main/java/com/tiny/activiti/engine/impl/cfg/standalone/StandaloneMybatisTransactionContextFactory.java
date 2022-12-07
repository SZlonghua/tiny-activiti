package com.tiny.activiti.engine.impl.cfg.standalone;

import com.tiny.activiti.engine.impl.cfg.TransactionContext;
import com.tiny.activiti.engine.impl.cfg.TransactionContextFactory;
import com.tiny.activiti.engine.impl.interceptor.CommandContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StandaloneMybatisTransactionContextFactory implements TransactionContextFactory {
    @Override
    public TransactionContext openTransactionContext(CommandContext commandContext) {
        log.debug("StandaloneMybatisTransactionContextFactory openTransactionContext");
        return new StandaloneMybatisTransactionContext(commandContext);
    }
}
