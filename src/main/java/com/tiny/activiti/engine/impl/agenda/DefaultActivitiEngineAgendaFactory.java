package com.tiny.activiti.engine.impl.agenda;

import com.tiny.activiti.engine.ActivitiEngineAgenda;
import com.tiny.activiti.engine.ActivitiEngineAgendaFactory;
import com.tiny.activiti.engine.impl.interceptor.CommandContext;

public class DefaultActivitiEngineAgendaFactory implements ActivitiEngineAgendaFactory {
    @Override
    public ActivitiEngineAgenda createAgenda(CommandContext commandContext) {
        return new DefaultActivitiEngineAgenda(commandContext);
    }
}
