package net.malevy.hyperdemo.commands.impl;

import net.malevy.hyperdemo.commands.Command;
import net.malevy.hyperdemo.commands.CommandDispatcher;
import net.malevy.hyperdemo.commands.CommandHandler;
import net.malevy.hyperdemo.commands.NoHandlerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CommandDispatcherImpl implements CommandDispatcher {

    private final CommandHandler<?,?>[] handlers;

    @Autowired
    public CommandDispatcherImpl(CommandHandler<?,?>[] handlers) {
        this.handlers = handlers;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TResponse, TRequest extends Command<TResponse>> TResponse handle(TRequest command) throws NoHandlerException {

        return Arrays.stream(handlers)
                .filter(h -> h.canHandle(command.getClass()))
                .findFirst()
                .map(h -> (CommandHandler<TRequest, TResponse>)h)
                .orElseThrow(() -> new NoHandlerException(command.getClass()))
                .handle(command);

    }


}
