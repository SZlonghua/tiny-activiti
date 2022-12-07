package com.tiny.activiti.engine.impl.context;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tiny.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import com.tiny.activiti.engine.impl.cfg.TransactionContext;
import com.tiny.activiti.engine.impl.interceptor.CommandContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Context {

    protected static ThreadLocal<Stack<CommandContext>> commandContextThreadLocal = new ThreadLocal<Stack<CommandContext>>();
    protected static ThreadLocal<Stack<ProcessEngineConfigurationImpl>> processEngineConfigurationStackThreadLocal = new ThreadLocal<Stack<ProcessEngineConfigurationImpl>>();
    protected static ThreadLocal<Stack<TransactionContext>> transactionContextThreadLocal = new ThreadLocal<Stack<TransactionContext>>();
    protected static ThreadLocal<Map<String, ObjectNode>> bpmnOverrideContextThreadLocal = new ThreadLocal<Map<String, ObjectNode>>();

    public static CommandContext getCommandContext() {
        Stack<CommandContext> stack = getStack(commandContextThreadLocal);
        if (stack.isEmpty()) {
            return null;
        }
        return stack.peek();
    }

    public static void setCommandContext(CommandContext commandContext) {
        getStack(commandContextThreadLocal).push(commandContext);
    }

    public static void removeCommandContext() {
        getStack(commandContextThreadLocal).pop();
    }

    public static ProcessEngineConfigurationImpl getProcessEngineConfiguration() {
        Stack<ProcessEngineConfigurationImpl> stack = getStack(processEngineConfigurationStackThreadLocal);
        if (stack.isEmpty()) {
            return null;
        }
        return stack.peek();
    }

    public static void setProcessEngineConfiguration(ProcessEngineConfigurationImpl processEngineConfiguration) {
        getStack(processEngineConfigurationStackThreadLocal).push(processEngineConfiguration);
    }

    public static void removeProcessEngineConfiguration() {
        getStack(processEngineConfigurationStackThreadLocal).pop();
    }

    public static void setTransactionContext(TransactionContext transactionContext) {
        getStack(transactionContextThreadLocal).push(transactionContext);
    }

    public static void removeTransactionContext() {
        getStack(transactionContextThreadLocal).pop();
    }

    protected static Map<String, ObjectNode> getBpmnOverrideContext() {
        Map<String, ObjectNode> bpmnOverrideMap = bpmnOverrideContextThreadLocal.get();
        if (bpmnOverrideMap == null) {
            bpmnOverrideMap = new HashMap<String, ObjectNode>();
        }
        return bpmnOverrideMap;
    }

    protected static void addBpmnOverrideElement(String id, ObjectNode infoNode) {
        Map<String, ObjectNode> bpmnOverrideMap = bpmnOverrideContextThreadLocal.get();
        if (bpmnOverrideMap == null) {
            bpmnOverrideMap = new HashMap<String, ObjectNode>();
            bpmnOverrideContextThreadLocal.set(bpmnOverrideMap);
        }
        bpmnOverrideMap.put(id, infoNode);
    }

    public static void removeBpmnOverrideContext() {
        bpmnOverrideContextThreadLocal.remove();
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
