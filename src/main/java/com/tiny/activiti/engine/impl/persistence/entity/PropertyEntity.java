package com.tiny.activiti.engine.impl.persistence.entity;

import com.tiny.activiti.engine.impl.db.HasRevision;

public interface PropertyEntity extends Entity, HasRevision {

    String getName();

    void setName(String name);

    String getValue();

    void setValue(String value);

    String getId();

    Object getPersistentState();

}
