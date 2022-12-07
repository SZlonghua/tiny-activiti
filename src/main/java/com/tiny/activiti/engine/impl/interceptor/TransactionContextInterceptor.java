package com.tiny.activiti.engine.impl.interceptor;

import com.tiny.activiti.engine.impl.cfg.TransactionContext;
import com.tiny.activiti.engine.impl.cfg.TransactionContextFactory;
import com.tiny.activiti.engine.impl.context.Context;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransactionContextInterceptor extends AbstractCommandInterceptor {

    protected TransactionContextFactory transactionContextFactory;

    public TransactionContextInterceptor(TransactionContextFactory transactionContextFactory) {
        this.transactionContextFactory = transactionContextFactory;
    }
    @Override
    public <T> T execute(CommandConfig config, Command<T> command) {
        log.debug("TransactionContextInterceptor execute");
        CommandContext commandContext = Context.getCommandContext();
        // Storing it in a variable, to reference later (it can change during command execution)
        boolean isReused = commandContext.isReused();

        try {

            if (transactionContextFactory != null && !isReused) {
                TransactionContext transactionContext = transactionContextFactory.openTransactionContext(commandContext);
                Context.setTransactionContext(transactionContext);
                commandContext.addCloseListener(new TransactionCommandContextCloseListener(transactionContext));
            }

            return next.execute(config, command);

        } finally {
            if (transactionContextFactory != null && !isReused) {
                Context.removeTransactionContext();
            }
        }
    }
}
