package com.tiny.activiti.engine.impl.interceptor;

public abstract class AbstractCommandInterceptor implements CommandInterceptor{

    protected CommandInterceptor next;

    public CommandInterceptor getNext() {
        return next;
    }

    public void setNext(CommandInterceptor next) {
        this.next = next;
    }
}
