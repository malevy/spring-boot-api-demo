package net.malevy.hyperdemo;


import net.malevy.hyperdemo.commands.CommandDispatcher;
import net.malevy.hyperdemo.commands.DeleteSingleTaskCommand;
import net.malevy.hyperdemo.commands.NoHandlerException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.Assert.assertEquals;


@RunWith(MockitoJUnitRunner.class)
public class TaskController_DeleteTaskTest {

    @Mock
    private CommandDispatcher dispatcher;

    @InjectMocks
    private TaskController controller;

    @Test
    public void whenDeletingATask_ReturnNoContent() throws NoHandlerException {

        Mockito.when(dispatcher.handle(Mockito.any(DeleteSingleTaskCommand.class)))
                .thenReturn("removed");

        ResponseEntity<?> response = controller.deleteTask(1);

        assertEquals("the status is wrong", HttpStatus.OK, response.getStatusCode());
    }

}
