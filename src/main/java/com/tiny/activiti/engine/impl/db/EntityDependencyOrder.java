package com.tiny.activiti.engine.impl.db;

import com.tiny.activiti.engine.impl.persistence.entity.Entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntityDependencyOrder {

    public static List<Class<? extends Entity>> DELETE_ORDER = new ArrayList<Class<? extends Entity>>();
    public static List<Class<? extends Entity>> INSERT_ORDER = new ArrayList<Class<? extends Entity>>();


    static {

        /*
         * In the comments below:
         *
         * 'FK to X' : X should be BELOW the entity
         *
         * 'FK from X': X should be ABOVE the entity
         *
         */

        /* No FK */
//        DELETE_ORDER.add(PropertyEntityImpl.class);
//
//        /* No FK */
//        DELETE_ORDER.add(AttachmentEntityImpl.class);
//
//        /* No FK */
//        DELETE_ORDER.add(CommentEntityImpl.class);
//
//        /* No FK */
//        DELETE_ORDER.add(EventLogEntryEntityImpl.class);
//
//        /*
//         * FK to Deployment
//         * FK to ByteArray
//         */
//        DELETE_ORDER.add(ModelEntityImpl.class);
//
//        /*
//         * FK to ByteArray
//         */
//        DELETE_ORDER.add(JobEntityImpl.class);
//        DELETE_ORDER.add(TimerJobEntityImpl.class);
//        DELETE_ORDER.add(SuspendedJobEntityImpl.class);
//        DELETE_ORDER.add(DeadLetterJobEntityImpl.class);
//
//        /*
//         * FK to ByteArray
//         * FK to Exeution
//         */
//        DELETE_ORDER.add(VariableInstanceEntityImpl.class);
//
//        /*
//         * FK to ByteArray
//         * FK to ProcessDefinition
//         */
//        DELETE_ORDER.add(ProcessDefinitionInfoEntityImpl.class);
//
//        /*
//         * FK from ModelEntity
//         * FK from JobEntity
//         * FK from VariableInstanceEntity
//         *
//         * FK to DeploymentEntity
//         */
//        DELETE_ORDER.add(ByteArrayEntityImpl.class);
//
//        /*
//         * FK from ModelEntity
//         * FK from JobEntity
//         * FK from VariableInstanceEntity
//         *
//         * FK to DeploymentEntity
//         */
//        DELETE_ORDER.add(ResourceEntityImpl.class);
//
//        /*
//         * FK from ByteArray
//         */
//        DELETE_ORDER.add(DeploymentEntityImpl.class);
//
//        /*
//         * FK to Execution
//         */
//        DELETE_ORDER.add(EventSubscriptionEntityImpl.class);
//
//        /*
//         * FK to Execution
//         */
//        DELETE_ORDER.add(CompensateEventSubscriptionEntityImpl.class);
//
//        /*
//         * FK to Execution
//         */
//        DELETE_ORDER.add(MessageEventSubscriptionEntityImpl.class);
//
//        /*
//         * FK to Execution
//         */
//        DELETE_ORDER.add(SignalEventSubscriptionEntityImpl.class);
//
//
//        /*
//         * FK to process definition
//         * FK to Execution
//         * FK to Task
//         */
//        DELETE_ORDER.add(IdentityLinkEntityImpl.class);
//
//        /*
//         * FK from IdentityLink
//         *
//         * FK to Execution
//         * FK to process definition
//         */
//        DELETE_ORDER.add(TaskEntityImpl.class);
//
//        /*
//         * FK from VariableInstance
//         * FK from EventSubscription
//         * FK from IdentityLink
//         * FK from Task
//         *
//         * FK to ProcessDefinition
//         */
//        DELETE_ORDER.add(ExecutionEntityImpl.class);
//
//        /*
//         * FK from Task
//         * FK from IdentityLink
//         * FK from execution
//         */
//        DELETE_ORDER.add(ProcessDefinitionEntityImpl.class);
//
//        /*
//         * FK to User
//         * FK to Group
//         */
//        DELETE_ORDER.add(MembershipEntityImpl.class);
//
//        /*
//         * Fk from Membership
//         */
//        DELETE_ORDER.add(UserEntityImpl.class);
//
//        /*
//         * FK from Membership
//         */
//        DELETE_ORDER.add(GroupEntityImpl.class);
//
//
//        // History entities have no FK's
//
//        DELETE_ORDER.add(HistoricIdentityLinkEntityImpl.class);
//
//        DELETE_ORDER.add(IdentityInfoEntityImpl.class);
//
//        DELETE_ORDER.add(HistoricActivityInstanceEntityImpl.class);
//        DELETE_ORDER.add(HistoricProcessInstanceEntityImpl.class);
//        DELETE_ORDER.add(HistoricTaskInstanceEntityImpl.class);
//        DELETE_ORDER.add(HistoricScopeInstanceEntityImpl.class);
//
//        DELETE_ORDER.add(HistoricVariableInstanceEntityImpl.class);
//
//        DELETE_ORDER.add(HistoricDetailAssignmentEntityImpl.class);
//        DELETE_ORDER.add(HistoricDetailTransitionInstanceEntityImpl.class);
//        DELETE_ORDER.add(HistoricDetailVariableInstanceUpdateEntityImpl.class);
//        DELETE_ORDER.add(HistoricFormPropertyEntityImpl.class);
//        DELETE_ORDER.add(HistoricDetailEntityImpl.class);

        INSERT_ORDER = new ArrayList<Class<? extends Entity>>(DELETE_ORDER);
        Collections.reverse(INSERT_ORDER);

    }
}
