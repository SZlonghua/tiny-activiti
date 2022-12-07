package com.tiny.activiti.engine.impl.persistence;

import com.tiny.activiti.engine.ActivitiException;
import com.tiny.activiti.engine.impl.interceptor.CommandContext;
import com.tiny.activiti.engine.impl.interceptor.Session;
import com.tiny.activiti.engine.impl.interceptor.SessionFactory;

public class GenericManagerFactory implements SessionFactory {
    protected Class<? extends Session> typeClass;
    protected Class<? extends Session> implementationClass;

    public GenericManagerFactory(Class<? extends Session> typeClass, Class<? extends Session> implementationClass) {
        this.typeClass = typeClass;
        this.implementationClass = implementationClass;
    }
    public Class<?> getSessionType() {
        return typeClass;
    }

    public Session openSession(CommandContext commandContext) {
        try {
            return implementationClass.newInstance();
        } catch (Exception e) {
            throw new ActivitiException("couldn't instantiate " + implementationClass.getName() + ": " + e.getMessage(), e);
        }
    }
}
