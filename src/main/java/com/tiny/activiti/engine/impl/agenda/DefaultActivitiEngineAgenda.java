package com.tiny.activiti.engine.impl.agenda;

import com.tiny.activiti.engine.ActivitiEngineAgenda;
import com.tiny.activiti.engine.impl.interceptor.CommandContext;
import com.tiny.activiti.engine.impl.persistence.entity.ExecutionEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

@Slf4j
public class DefaultActivitiEngineAgenda implements ActivitiEngineAgenda {

    protected LinkedList<Runnable> operations = new LinkedList<Runnable>();
    protected CommandContext commandContext;

    public DefaultActivitiEngineAgenda(CommandContext commandContext) {
        this.commandContext = commandContext;
    }

    @Override
    public boolean isEmpty() {
        return operations.isEmpty();
    }

    @Override
    public Runnable getNextOperation() {
        return operations.poll();
    }

    /**
     * Generic method to plan a {@link Runnable}.
     */
    @Override
    public void planOperation(Runnable operation) {
        operations.add(operation);

        if (operation instanceof AbstractOperation) {
            ExecutionEntity execution = ((AbstractOperation) operation).getExecution();
            if (execution != null) {
                commandContext.addInvolvedExecution(execution);
            }
        }

        log.debug("Operation {} added to agenda", operation.getClass());
    }
}
