package com.tiny.activiti.engine;

import org.junit.Test;

public class ProcessEngineTest {

    @Test
    public void createProcessEngine(){
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml")
                .buildProcessEngine();
        System.out.println("ddddddddd");
    }

}