package com.tiny.activiti.engine.impl.util;

import com.tiny.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import com.tiny.activiti.engine.impl.context.Context;

import java.io.InputStream;

public abstract class ReflectUtil {
    public static ClassLoader getClassLoader() {
        ClassLoader loader = getCustomClassLoader();
        if (loader == null) {
            loader = Thread.currentThread().getContextClassLoader();
        }
        return loader;
    }

    public static InputStream getResourceAsStream(String name) {
        InputStream resourceStream = null;
        ClassLoader classLoader = getCustomClassLoader();
        if (classLoader != null) {
            resourceStream = classLoader.getResourceAsStream(name);
        }

        if (resourceStream == null) {
            // Try the current Thread context classloader
            classLoader = Thread.currentThread().getContextClassLoader();
            resourceStream = classLoader.getResourceAsStream(name);
            if (resourceStream == null) {
                // Finally, try the classloader for this class
                classLoader = ReflectUtil.class.getClassLoader();
                resourceStream = classLoader.getResourceAsStream(name);
            }
        }
        return resourceStream;
    }

    private static ClassLoader getCustomClassLoader() {
        ProcessEngineConfigurationImpl processEngineConfiguration = Context.getProcessEngineConfiguration();
        if (processEngineConfiguration != null) {
            final ClassLoader classLoader = processEngineConfiguration.getClassLoader();
            if (classLoader != null) {
                return classLoader;
            }
        }
        return null;
    }
}
