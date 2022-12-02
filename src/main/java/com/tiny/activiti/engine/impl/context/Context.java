package com.tiny.activiti.engine.impl.context;

import com.tiny.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;

import java.util.Stack;

public class Context {

    protected static ThreadLocal<Stack<ProcessEngineConfigurationImpl>> processEngineConfigurationStackThreadLocal = new ThreadLocal<Stack<ProcessEngineConfigurationImpl>>();

    public static ProcessEngineConfigurationImpl getProcessEngineConfiguration() {
        Stack<ProcessEngineConfigurationImpl> stack = getStack(processEngineConfigurationStackThreadLocal);
        if (stack.isEmpty()) {
            return null;
        }
        return stack.peek();
    }

    protected static <T> Stack<T> getStack(ThreadLocal<Stack<T>> threadLocal) {
        Stack<T> stack = threadLocal.get();
        if (stack == null) {
            stack = new Stack<T>();
            threadLocal.set(stack);
        }
        return stack;
    }
}
