package com.tiny.activiti.engine.impl.interceptor;

public interface CommandExecutor {

    CommandConfig getDefaultConfig();

    /**
     * Execute a command with the specified {@link CommandConfig}.
     */
    <T> T execute(CommandConfig config, Command<T> command);

    /**
     * Execute a command with the default {@link CommandConfig}.
     */
    <T> T execute(Command<T> command);
}
