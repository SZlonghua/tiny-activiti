package com.tiny.activiti.engine.impl.db;

public interface HasRevision {

    void setRevision(int revision);

    int getRevision();

    int getRevisionNext();
}
