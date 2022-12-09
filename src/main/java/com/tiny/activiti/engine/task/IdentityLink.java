package com.tiny.activiti.engine.task;

public interface IdentityLink {

    /**
     * Returns the type of link. See {@link IdentityLinkType} for the native supported types by Activiti.
     */
    String getType();

    /**
     * If the identity link involves a user, then this will be a non-null id of a user. That userId can be used to query for user information through the {@link UserQuery} API.
     */
    String getUserId();

    /**
     * If the identity link involves a group, then this will be a non-null id of a group. That groupId can be used to query for user information through the {@link GroupQuery} API.
     */
    String getGroupId();

    /**
     * The id of the task associated with this identity link.
     */
    String getTaskId();

    /**
     * The process definition id associated with this identity link.
     */
    String getProcessDefinitionId();

    /**
     * The process instance id associated with this identity link.
     */
    String getProcessInstanceId();
}
