package net.malevy.hyperdemo.commands;

import net.malevy.hyperdemo.HypermediaDemoApplication;
import net.malevy.hyperdemo.commands.CommandDispatchingTestComponents.StubCommand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@ActiveProfiles(profiles = "unittest")
@RunWith(SpringJUnit4ClassRunner.class)
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

