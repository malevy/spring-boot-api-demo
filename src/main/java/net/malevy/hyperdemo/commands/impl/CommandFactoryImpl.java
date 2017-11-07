package net.malevy.hyperdemo.commands.impl;

import net.malevy.hyperdemo.commands.Command;
import net.malevy.hyperdemo.commands.CommandFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class CommandFactoryImpl implements CommandFactory, ApplicationContextAware {
    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public <C extends Command> C getCommand(Class<C> command) {
        Assert.notNull(command, "must provide the type specification of the command to build");

        return this.context.getBean(command);

    }
}
