package com.tiny.activiti.engine.impl.db;

import org.apache.ibatis.session.SqlSession;

public class BulkDeleteOperation {
    protected String statement;
    protected Object parameter;

    public BulkDeleteOperation(String statement, Object parameter) {
        this.statement = statement;
        this.parameter = parameter;
    }

    public void execute(SqlSession sqlSession) {
        sqlSession.delete(statement, parameter);
    }

    @Override
    public String toString() {
        return "bulk delete: " + statement + "(" + parameter + ")";
    }
}
