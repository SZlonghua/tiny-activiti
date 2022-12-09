package com.tiny.activiti.engine.impl.persistence.entity;

import com.tiny.activiti.engine.task.IdentityLink;

public interface IdentityLinkEntity extends IdentityLink, Entity {
    boolean isUser();

    boolean isGroup();

    void setType(String type);

    void setUserId(String userId);

    void setGroupId(String groupId);

    void setTaskId(String taskId);

    void setProcessInstanceId(String processInstanceId);

    String getProcessDefId();

    void setProcessDefId(String processDefId);

    TaskEntity getTask();

    void setTask(TaskEntity task);

    ExecutionEntity getProcessInstance();

    void setProcessInstance(ExecutionEntity processInstance);

    ProcessDefinitionEntity getProcessDef();

    void setProcessDef(ProcessDefinitionEntity processDef);

    String getProcessDefinitionId();
}
