package com.tiny.activiti.engine.impl.interceptor;

import com.tiny.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import com.tiny.activiti.engine.impl.context.Context;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandContextInterceptor extends AbstractCommandInterceptor {
    protected CommandContextFactory commandContextFactory;
    protected ProcessEngineConfigurationImpl processEngineConfiguration;

    public CommandContextInterceptor(CommandContextFactory commandContextFactory, ProcessEngineConfigurationImpl processEngineConfiguration) {
        this.commandContextFactory = commandContextFactory;
        this.processEngineConfiguration = processEngineConfiguration;
    }

    @Override
    public <T> T execute(CommandConfig config, Command<T> command) {
        log.debug("CommandContextInterceptor execute");

        CommandContext context = Context.getCommandContext();

        boolean contextReused = false;
        // We need to check the exception, because the transaction can be in a
        // rollback state, and some other command is being fired to compensate (eg. decrementing job retries)
        if (!config.isContextReusePossible() || context == null || context.getException() != null) {
            context = commandContextFactory.createCommandContext(command);
        } else {
            log.debug("Valid context found. Reusing it for the current command '{}'", command.getClass().getCanonicalName());
            contextReused = true;
            context.setReused(true);
        }

        try {

            // Push on stack
            Context.setCommandContext(context);
            Context.setProcessEngineConfiguration(processEngineConfiguration);
            /*if (processEngineConfiguration.getActiviti5CompatibilityHandler() != null) {
                Context.setActiviti5CompatibilityHandler(processEngineConfiguration.getActiviti5CompatibilityHandler());
            }*/

            return next.execute(config, command);

        } catch (Exception e) {

            context.exception(e);

        } finally {
            try {
                if (!contextReused) {
                    context.close();
                }
            } finally {

                // Pop from stack
                Context.removeCommandContext();
                Context.removeProcessEngineConfiguration();
                Context.removeBpmnOverrideContext();
//                Context.removeActiviti5CompatibilityHandler();
            }
        }

        return null;
    }
}
