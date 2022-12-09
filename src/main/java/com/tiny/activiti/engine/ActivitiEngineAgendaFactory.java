package com.tiny.activiti.engine;

import com.tiny.activiti.engine.impl.interceptor.CommandContext;

public interface ActivitiEngineAgendaFactory {

    ActivitiEngineAgenda createAgenda(CommandContext commandContext);

}
