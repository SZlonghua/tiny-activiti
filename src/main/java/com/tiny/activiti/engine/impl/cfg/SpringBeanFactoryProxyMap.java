package com.tiny.activiti.engine.impl.cfg;

import com.tiny.activiti.engine.ActivitiException;
import org.springframework.beans.factory.BeanFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class SpringBeanFactoryProxyMap implements Map<Object, Object> {
    protected BeanFactory beanFactory;

    public SpringBeanFactoryProxyMap(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public Object get(Object key) {
        if ((key == null) || (!String.class.isAssignableFrom(key.getClass()))) {
            return null;
        }
        return beanFactory.getBean((String) key);
    }

    public boolean containsKey(Object key) {
        if ((key == null) || (!String.class.isAssignableFrom(key.getClass()))) {
            return false;
        }
        return beanFactory.containsBean((String) key);
    }

    public Set<Object> keySet() {
        throw new ActivitiException("unsupported operation on configuration beans");
    }

    public void clear() {
        throw new ActivitiException("can't clear configuration beans");
    }

    public boolean containsValue(Object value) {
        throw new ActivitiException("can't search values in configuration beans");
    }

    public Set<Map.Entry<Object, Object>> entrySet() {
        throw new ActivitiException("unsupported operation on configuration beans");
    }

    public boolean isEmpty() {
        throw new ActivitiException("unsupported operation on configuration beans");
    }

    public Object put(Object key, Object value) {
        throw new ActivitiException("unsupported operation on configuration beans");
    }

    public void putAll(Map<? extends Object, ? extends Object> m) {
        throw new ActivitiException("unsupported operation on configuration beans");
    }

    public Object remove(Object key) {
        throw new ActivitiException("unsupported operation on configuration beans");
    }

    public int size() {
        throw new ActivitiException("unsupported operation on configuration beans");
    }

    public Collection<Object> values() {
        throw new ActivitiException("unsupported operation on configuration beans");
    }
}
