package com.tiny.activiti.engine.impl.db;

import com.tiny.activiti.engine.impl.cfg.IdGenerator;
import com.tiny.activiti.engine.impl.interceptor.CommandConfig;
import com.tiny.activiti.engine.impl.interceptor.CommandExecutor;

public class DbIdGenerator implements IdGenerator {
    protected int idBlockSize;
    protected long nextId;
    protected long lastId = -1;

    protected CommandExecutor commandExecutor;
    protected CommandConfig commandConfig;

    public synchronized String getNextId() {
        if (lastId < nextId) {
            getNewBlock();
        }
        long _nextId = nextId++;
        return Long.toString(_nextId);
    }

    protected synchronized void getNewBlock() {
        /*IdBlock idBlock = commandExecutor.execute(commandConfig, new GetNextIdBlockCmd(idBlockSize));
        this.nextId = idBlock.getNextId();
        this.lastId = idBlock.getLastId();*/
    }

    public int getIdBlockSize() {
        return idBlockSize;
    }

    public void setIdBlockSize(int idBlockSize) {
        this.idBlockSize = idBlockSize;
    }

    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    public void setCommandExecutor(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    public CommandConfig getCommandConfig() {
        return commandConfig;
    }

    public void setCommandConfig(CommandConfig commandConfig) {
        this.commandConfig = commandConfig;
    }
}
