package com.tiny.activiti.engine.impl.cfg.standalone;

import com.tiny.activiti.engine.impl.cfg.TransactionContext;
import com.tiny.activiti.engine.impl.cfg.TransactionListener;
import com.tiny.activiti.engine.impl.cfg.TransactionPropagation;
import com.tiny.activiti.engine.impl.cfg.TransactionState;
import com.tiny.activiti.engine.impl.db.DbSqlSession;
import com.tiny.activiti.engine.impl.interceptor.Command;
import com.tiny.activiti.engine.impl.interceptor.CommandConfig;
import com.tiny.activiti.engine.impl.interceptor.CommandContext;
import com.tiny.activiti.engine.impl.interceptor.CommandExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class StandaloneMybatisTransactionContext implements TransactionContext {

    protected CommandContext commandContext;
    protected Map<TransactionState, List<TransactionListener>> stateTransactionListeners;

    public StandaloneMybatisTransactionContext(CommandContext commandContext) {
        this.commandContext = commandContext;
    }

    public void addTransactionListener(TransactionState transactionState, TransactionListener transactionListener) {
        if (stateTransactionListeners == null) {
            stateTransactionListeners = new HashMap<TransactionState, List<TransactionListener>>();
        }
        List<TransactionListener> transactionListeners = stateTransactionListeners.get(transactionState);
        if (transactionListeners == null) {
            transactionListeners = new ArrayList<TransactionListener>();
            stateTransactionListeners.put(transactionState, transactionListeners);
        }
        transactionListeners.add(transactionListener);
    }

    public void commit() {

        log.debug("firing event committing...");
        fireTransactionEvent(TransactionState.COMMITTING, false);

        log.debug("committing the ibatis sql session...");
        getDbSqlSession().commit();
        log.debug("firing event committed...");
        fireTransactionEvent(TransactionState.COMMITTED, true);

    }

    /**
     * Fires the event for the provided {@link TransactionState}.
     *
     * @param transactionState The {@link TransactionState} for which the listeners will be called.
     * @param executeInNewContext If true, the listeners will be called in a new command context.
     *                            This is needed for example when firing the {@link TransactionState#COMMITTED}
     *                            event: the transacation is already committed and executing logic in the same
     *                            context could lead to strange behaviour (for example doing a {@link SqlSession#update(String)}
     *                            would actually roll back the update (as the MyBatis context is already committed
     *                            and the internal flags have not been correctly set).
     */
    protected void fireTransactionEvent(TransactionState transactionState, boolean executeInNewContext) {
        if (stateTransactionListeners==null) {
            return;
        }
        final List<TransactionListener> transactionListeners = stateTransactionListeners.get(transactionState);
        if (transactionListeners==null) {
            return;
        }

        if (executeInNewContext) {
            CommandExecutor commandExecutor = commandContext.getProcessEngineConfiguration().getCommandExecutor();
            CommandConfig commandConfig = new CommandConfig(false, TransactionPropagation.REQUIRES_NEW);
            commandExecutor.execute(commandConfig, new Command<Void>() {
                public Void execute(CommandContext commandContext) {
                    executeTransactionListeners(transactionListeners, commandContext);
                    return null;
                }
            });
        } else {
            executeTransactionListeners(transactionListeners, commandContext);
        }

    }

    protected void executeTransactionListeners(List<TransactionListener> transactionListeners, CommandContext commandContext) {
        for (TransactionListener transactionListener : transactionListeners) {
            transactionListener.execute(commandContext);
        }
    }

    protected DbSqlSession getDbSqlSession() {
        return commandContext.getDbSqlSession();
    }

    public void rollback() {
        try {
            try {
                log.debug("firing event rolling back...");
                fireTransactionEvent(TransactionState.ROLLINGBACK, false);

            } catch (Throwable exception) {
                log.info("Exception during transaction: {}", exception.getMessage());
                commandContext.exception(exception);
            } finally {
                log.debug("rolling back ibatis sql session...");
                getDbSqlSession().rollback();
            }

        } catch (Throwable exception) {
            log.info("Exception during transaction: {}", exception.getMessage());
            commandContext.exception(exception);

        } finally {
            log.debug("firing event rolled back...");
            fireTransactionEvent(TransactionState.ROLLED_BACK, true);
        }
    }
}
