package com.tiny.activiti.engine.impl.interceptor;

import com.tiny.activiti.engine.ActivitiException;
import com.tiny.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import com.tiny.activiti.engine.impl.db.DbSqlSession;
import com.tiny.activiti.engine.impl.persistence.cache.EntityCache;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CommandContext {

    protected Command<?> command;
    protected Map<Class<?>, SessionFactory> sessionFactories;
    protected Map<Class<?>, Session> sessions = new HashMap<Class<?>, Session>();
    protected Throwable exception;
    protected ProcessEngineConfigurationImpl processEngineConfiguration;
    protected List<CommandContextCloseListener> closeListeners;
    protected boolean reused;


    public CommandContext(Command<?> command, ProcessEngineConfigurationImpl processEngineConfiguration) {
        this.command = command;
        this.processEngineConfiguration = processEngineConfiguration;
//        this.failedJobCommandFactory = processEngineConfiguration.getFailedJobCommandFactory();
        this.sessionFactories = processEngineConfiguration.getSessionFactories();
//        this.agenda = processEngineConfiguration.getEngineAgendaFactory().createAgenda(this);
    }

    public ProcessEngineConfigurationImpl getProcessEngineConfiguration() {
        return processEngineConfiguration;
    }

    public DbSqlSession getDbSqlSession() {
        return getSession(DbSqlSession.class);
    }

    public EntityCache getEntityCache() {
        return getSession(EntityCache.class);
    }



    public <T> T getSession(Class<T> sessionClass) {
        Session session = sessions.get(sessionClass);
        if (session == null) {
            SessionFactory sessionFactory = sessionFactories.get(sessionClass);
            if (sessionFactory == null) {
                throw new ActivitiException("no session factory configured for " + sessionClass.getName());
            }
            log.debug("CommandContext start create session {}",sessionClass);
            session = sessionFactory.openSession(this);
            log.debug("CommandContext completed create session {}",sessionClass);
            sessions.put(sessionClass, session);
        }

        return (T) session;
    }

    public void close() {

        // The intention of this method is that all resources are closed properly, even if exceptions occur
        // in close or flush methods of the sessions or the transaction context.

        try {
            try {
                try {
                    executeCloseListenersClosing();
                    if (exception == null) {
                        flushSessions();
                    }
                } catch (Throwable exception) {
                    exception(exception);
                } finally {

                    try {
                        if (exception == null) {
                            executeCloseListenersAfterSessionFlushed();
                        }
                    } catch (Throwable exception) {
                        exception(exception);
                    }

                    if (exception != null) {
                        logException();
                        executeCloseListenersCloseFailure();
                    } else {
                        executeCloseListenersClosed();
                    }

                }
            } catch (Throwable exception) {
                // Catch exceptions during rollback
                exception(exception);
            } finally {
                // Sessions need to be closed, regardless of exceptions/commit/rollback
                closeSessions();
            }
        } catch (Throwable exception) {
            // Catch exceptions during session closing
            exception(exception);
        }

        if (exception != null) {
            rethrowExceptionIfNeeded();
        }
    }

    public void addCloseListener(CommandContextCloseListener commandContextCloseListener) {
        if (closeListeners == null) {
            closeListeners = new ArrayList<CommandContextCloseListener>(1);
        }
        closeListeners.add(commandContextCloseListener);
    }

    public List<CommandContextCloseListener> getCloseListeners() {
        return closeListeners;
    }

    protected void executeCloseListenersClosing() {
        if (closeListeners != null) {
            try {
                for (CommandContextCloseListener listener : closeListeners) {
                    listener.closing(this);
                }
            } catch (Throwable exception) {
                exception(exception);
            }
        }
    }

    protected void executeCloseListenersAfterSessionFlushed() {
        if (closeListeners != null) {
            try {
                for (CommandContextCloseListener listener : closeListeners) {
                    listener.afterSessionsFlush(this);
                }
            } catch (Throwable exception) {
                exception(exception);
            }
        }
    }

    protected void executeCloseListenersClosed() {
        if (closeListeners != null) {
            try {
                for (CommandContextCloseListener listener : closeListeners) {
                    listener.closed(this);
                }
            } catch (Throwable exception) {
                exception(exception);
            }
        }
    }

    protected void executeCloseListenersCloseFailure() {
        if (closeListeners != null) {
            try {
                for (CommandContextCloseListener listener : closeListeners) {
                    listener.closeFailure(this);
                }
            } catch (Throwable exception) {
                exception(exception);
            }
        }
    }

    protected void logException() {
        log.error("Error while closing command context", exception);
    }

    protected void rethrowExceptionIfNeeded() throws Error {
        if (exception instanceof Error) {
            throw (Error) exception;
        } else if (exception instanceof RuntimeException) {
            throw (RuntimeException) exception;
        } else {
            throw new ActivitiException("exception while executing command " + command, exception);
        }
    }


    protected void flushSessions() {
        for (Session session : sessions.values()) {
            session.flush();
        }
    }

    protected void closeSessions() {
        for (Session session : sessions.values()) {
            try {
                session.close();
            } catch (Throwable exception) {
                exception(exception);
            }
        }
    }

    public void exception(Throwable exception) {
        if (this.exception == null) {
            this.exception = exception;

        } else {
            log.error("masked exception in command context. for root cause, see below as it will be rethrown later.", exception);
            MDC.clear();
        }
    }

    public Throwable getException() {
        return exception;
    }

    public boolean isReused() {
        return reused;
    }

    public void setReused(boolean reused) {
        this.reused = reused;
    }
}
