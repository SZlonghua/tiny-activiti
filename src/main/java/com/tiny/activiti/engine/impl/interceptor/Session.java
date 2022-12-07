package com.tiny.activiti.engine.impl.interceptor;

public interface Session {

    void flush();

    void close();
}
