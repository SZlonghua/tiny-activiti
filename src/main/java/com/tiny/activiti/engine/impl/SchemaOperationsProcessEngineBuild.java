package com.tiny.activiti.engine.impl;

import com.tiny.activiti.engine.impl.db.DbSqlSession;
import com.tiny.activiti.engine.impl.interceptor.Command;
import com.tiny.activiti.engine.impl.interceptor.CommandContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SchemaOperationsProcessEngineBuild implements Command<Object> {
    @Override
    public Object execute(CommandContext commandContext) {
        log.debug("SchemaOperationsProcessEngineBuild execute");
        DbSqlSession dbSqlSession = commandContext.getDbSqlSession();
        if (dbSqlSession != null) {
            dbSqlSession.performSchemaOperationsProcessEngineBuild();
        }
        return null;
    }
}
