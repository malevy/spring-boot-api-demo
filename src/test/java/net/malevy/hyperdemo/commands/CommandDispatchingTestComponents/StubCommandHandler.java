package net.malevy.hyperdemo.commands.CommandDispatchingTestComponents;

import net.malevy.hyperdemo.commands.CommandHandler;
import org.springframework.stereotype.Component;

@Component
public class StubCommandHandler implements CommandHandler<StubCommand, Integer> {

    @Override
    public Integer handle(StubCommand request) {
        return request.a + request.b;
    }

    @Override
    public boolean canHandle(Class<?> clazz) {
        return StubCommand.class.isAssignableFrom(clazz);
    }
}
