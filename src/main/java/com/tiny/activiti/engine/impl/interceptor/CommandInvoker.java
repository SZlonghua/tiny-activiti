package com.tiny.activiti.engine.impl.interceptor;

import com.tiny.activiti.engine.impl.agenda.AbstractOperation;
import com.tiny.activiti.engine.impl.context.Context;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandInvoker extends AbstractCommandInterceptor {
    /*public <T> T execute(CommandConfig config, Command<T> command) {
        log.debug("CommandInvoker execute");
        final CommandContext commandContext = Context.getCommandContext();
        return command.execute(commandContext);
    }*/

    @Override
    public <T> T execute(CommandConfig config, Command<T> command) {
        log.debug("CommandInvoker execute");
        final CommandContext commandContext = Context.getCommandContext();

        // Execute the command.
        // This will produce operations that will be put on the agenda.
        commandContext.getAgenda().planOperation(new Runnable() {
            @Override
            public void run() {
                commandContext.setResult(command.execute(commandContext));
            }
        });
        // Run loop for agenda
        executeOperations(commandContext);

        // At the end, call the execution tree change listeners.
        // TODO: optimization: only do this when the tree has actually changed (ie check dbSqlSession).
        /*if (commandContext.hasInvolvedExecutions()) {
            Context.getAgenda().planExecuteInactiveBehaviorsOperation();
            executeOperations(commandContext);
        }*/

        return (T) commandContext.getResult();
    }

    protected void executeOperations(final CommandContext commandContext) {
        while (!commandContext.getAgenda().isEmpty()) {
            Runnable runnable = commandContext.getAgenda().getNextOperation();
            executeOperation(runnable);
        }
    }

    public void executeOperation(Runnable runnable) {
        if (runnable instanceof AbstractOperation) {
            AbstractOperation operation = (AbstractOperation) runnable;

            // Execute the operation if the operation has no execution (i.e. it's an operation not working on a process instance)
            // or the operation has an execution and it is not ended
            if (operation.getExecution() == null || !operation.getExecution().isEnded()) {

                if (log.isDebugEnabled()) {
                    log.debug("Executing operation {} ", operation.getClass());
                }

                runnable.run();

            }

        } else {
            runnable.run();
        }
    }

    @Override
    public CommandInterceptor getNext() {
        return null;
    }

    @Override
    public void setNext(CommandInterceptor next) {
        throw new UnsupportedOperationException("CommandInvoker must be the last interceptor in the chain");
    }
}
