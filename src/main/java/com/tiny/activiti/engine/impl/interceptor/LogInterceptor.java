package com.tiny.activiti.engine.impl.interceptor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogInterceptor extends AbstractCommandInterceptor {
    @Override
    public <T> T execute(CommandConfig config, Command<T> command) {
        if (!log.isDebugEnabled()) {
            // do nothing here if we cannot log
            return next.execute(config, command);
        }
        log.debug("\n");
        log.debug("--- starting {} --------------------------------------------------------", command.getClass().getSimpleName());
        try {

            return next.execute(config, command);

        } finally {
            log.debug("--- {} finished --------------------------------------------------------", command.getClass().getSimpleName());
            log.debug("\n");
        }
    }
}
