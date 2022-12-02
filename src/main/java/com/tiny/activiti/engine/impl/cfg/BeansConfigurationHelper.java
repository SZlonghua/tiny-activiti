package com.tiny.activiti.engine.impl.cfg;

import com.tiny.activiti.engine.ProcessEngineConfiguration;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class BeansConfigurationHelper {

    public static ProcessEngineConfiguration parseProcessEngineConfigurationFromResource(String resource, String beanName) {
        Resource springResource = new ClassPathResource(resource);
        return parseProcessEngineConfiguration(springResource, beanName);
    }

    public static ProcessEngineConfiguration parseProcessEngineConfiguration(Resource springResource, String beanName) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        xmlBeanDefinitionReader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_XSD);
        xmlBeanDefinitionReader.loadBeanDefinitions(springResource);
        ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) beanFactory.getBean(beanName);
        processEngineConfiguration.setBeans(new SpringBeanFactoryProxyMap(beanFactory));
        return processEngineConfiguration;
    }
}
