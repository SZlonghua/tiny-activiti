package com.tiny.activiti.engine.impl.interceptor;

public interface Command<T> {

    T execute(CommandContext commandContext);

}
