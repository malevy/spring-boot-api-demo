package net.malevy.hyperdemo.commands;

import net.malevy.hyperdemo.HypermediaDemoApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@ActiveProfiles(profiles = "unittest")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HypermediaDemoApplication.class, CommandFactoryIT.class})
@ComponentScan
public class CommandFactoryIT {

    @Autowired
    private CommandFactory factory;

    @Test
    public void whenRequested_canCreateCommand() {
        TestCommand cmd = factory.getCommand(TestCommand.class);
        assertNotNull("the command was not created", cmd);
    }


    @Component
    class TestCommand implements Command {}

}
