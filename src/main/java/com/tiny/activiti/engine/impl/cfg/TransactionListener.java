package com.tiny.activiti.engine.impl.cfg;

import com.tiny.activiti.engine.impl.interceptor.CommandContext;

public interface TransactionListener {

    void execute(CommandContext commandContext);

}
