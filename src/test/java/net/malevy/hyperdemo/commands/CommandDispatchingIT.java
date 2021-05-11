package net.malevy.hyperdemo.commands;

import net.malevy.hyperdemo.HypermediaDemoApplication;
import net.malevy.hyperdemo.commands.CommandDispatchingTestComponents.StubCommand;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.springframework.test.util.AssertionErrors.assertEquals;


@ActiveProfiles(profiles = "unittest")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CommandDispatchingIT.class, HypermediaDemoApplication.class})
@ComponentScan
public class CommandDispatchingIT {

    @Autowired
    private CommandDispatcher dispatcher;

    @Test
    public void canResolveHandler() throws NoHandlerException {

        StubCommand command = new StubCommand() {{
            a = 1;
            b = 2;
        }};

        Integer result = this.dispatcher.handle(command);

        assertEquals("the result is not right",(Integer)3, result);

    }
}

