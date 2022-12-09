package com.tiny.activiti.engine.impl.persistence.entity;

import com.tiny.activiti.engine.delegate.DelegateExecution;
import com.tiny.activiti.engine.impl.db.HasRevision;
import com.tiny.activiti.engine.runtime.Execution;
import com.tiny.activiti.engine.runtime.ProcessInstance;

import java.util.Date;
import java.util.List;

public interface ExecutionEntity extends DelegateExecution, Execution, ProcessInstance, Entity, HasRevision {
    void setBusinessKey(String businessKey);

    void setProcessDefinitionId(String processDefinitionId);

    void setProcessDefinitionKey(String processDefinitionKey);

    void setProcessDefinitionName(String processDefinitionName);

    void setProcessDefinitionVersion(Integer processDefinitionVersion);

    void setDeploymentId(String deploymentId);

    ExecutionEntity getProcessInstance();

    void setProcessInstance(ExecutionEntity processInstance);

    ExecutionEntity getParent();

    void setParent(ExecutionEntity parent);

    ExecutionEntity getSuperExecution();

    void setSuperExecution(ExecutionEntity superExecution);

    ExecutionEntity getSubProcessInstance();

    void setSubProcessInstance(ExecutionEntity subProcessInstance);

    void setRootProcessInstanceId(String rootProcessInstanceId);

    ExecutionEntity getRootProcessInstance();

    void setRootProcessInstance(ExecutionEntity rootProcessInstance);

    List<? extends ExecutionEntity> getExecutions();

    void addChildExecution(ExecutionEntity executionEntity);

    List<TaskEntity> getTasks();
//
//    List<EventSubscriptionEntity> getEventSubscriptions();
//
//    List<JobEntity> getJobs();
//
//    List<TimerJobEntity> getTimerJobs();

    List<IdentityLinkEntity> getIdentityLinks();

    void setProcessInstanceId(String processInstanceId);

    void setParentId(String parentId);

    void setEnded(boolean isEnded);

    void setEventName(String eventName);

    String getDeleteReason();

    void setDeleteReason(String deleteReason);

    int getSuspensionState();

    void setSuspensionState(int suspensionState);

    boolean isEventScope();

    void setEventScope(boolean isEventScope);

    boolean isMultiInstanceRoot();

    void setMultiInstanceRoot(boolean isMultiInstanceRoot);

    void setName(String name);

    void setDescription(String description);

    void setLocalizedName(String localizedName);

    void setLocalizedDescription(String localizedDescription);

    void setTenantId(String tenantId);

    Date getLockTime();

    void setLockTime(Date lockTime);

    boolean isDeleted();

    void setDeleted(boolean isDeleted);

    void forceUpdate();

    String getStartUserId();

    void setStartUserId(String startUserId);

    Date getStartTime();

    void setStartTime(Date startTime);
}
