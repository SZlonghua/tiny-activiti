package com.tiny.activiti.engine.impl.agenda;

import com.tiny.activiti.engine.Agenda;
import com.tiny.activiti.engine.impl.interceptor.CommandContext;
import com.tiny.activiti.engine.impl.persistence.entity.ExecutionEntity;

public abstract class AbstractOperation implements Runnable {
    protected CommandContext commandContext;
    protected Agenda agenda;
    protected ExecutionEntity execution;

    public AbstractOperation() {

    }

    public AbstractOperation(CommandContext commandContext, ExecutionEntity execution) {
        this.commandContext = commandContext;
        this.execution = execution;
        this.agenda = commandContext.getAgenda();
    }

    public CommandContext getCommandContext() {
        return commandContext;
    }

    public void setCommandContext(CommandContext commandContext) {
        this.commandContext = commandContext;
    }

    public Agenda getAgenda() {
        return agenda;
    }

    public void setAgenda(DefaultActivitiEngineAgenda agenda) {
        this.agenda = agenda;
    }

    public ExecutionEntity getExecution() {
        return execution;
    }

    public void setExecution(ExecutionEntity execution) {
        this.execution = execution;
    }
}
