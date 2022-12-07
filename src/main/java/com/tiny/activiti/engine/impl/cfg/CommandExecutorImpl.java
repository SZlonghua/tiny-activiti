package com.tiny.activiti.engine.impl.cfg;

import com.tiny.activiti.engine.impl.interceptor.Command;
import com.tiny.activiti.engine.impl.interceptor.CommandConfig;
import com.tiny.activiti.engine.impl.interceptor.CommandExecutor;
import com.tiny.activiti.engine.impl.interceptor.CommandInterceptor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandExecutorImpl implements CommandExecutor {
    protected CommandConfig defaultConfig;
    protected CommandInterceptor first;

    public CommandExecutorImpl(CommandConfig defaultConfig, CommandInterceptor first) {
        this.defaultConfig = defaultConfig;
        this.first = first;
    }

    @Override
    public CommandConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public <T> T execute(CommandConfig config, Command<T> command) {
        log.info("CommandExecutorImpl start execute command");
        return first.execute(config, command);
    }

    @Override
    public <T> T execute(Command<T> command) {
        return execute(defaultConfig, command);
    }
}
