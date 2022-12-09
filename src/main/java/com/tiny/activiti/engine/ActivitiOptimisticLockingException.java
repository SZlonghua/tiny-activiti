package com.tiny.activiti.engine;

public class ActivitiOptimisticLockingException extends ActivitiException {

    private static final long serialVersionUID = 1L;

    public ActivitiOptimisticLockingException(String message) {
        super(message);
    }
}
