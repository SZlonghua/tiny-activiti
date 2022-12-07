package com.tiny.activiti.engine.impl.interceptor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandInvoker extends AbstractCommandInterceptor {

    @Override
    public <T> T execute(CommandConfig config, Command<T> command) {
        log.debug("CommandInvoker execute");
        return command.execute(null);
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
