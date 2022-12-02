package com.tiny.activiti.engine.impl.db;

import com.tiny.activiti.engine.impl.persistence.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class EntityDependencyOrder {

    public static List<Class<? extends Entity>> INSERT_ORDER = new ArrayList<Class<? extends Entity>>();
}
