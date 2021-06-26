package net.malevy.hyperdemo;


import net.malevy.hyperdemo.commands.CommandDispatcher;
import net.malevy.hyperdemo.commands.DeleteSingleTaskCommand;
import net.malevy.hyperdemo.commands.NoHandlerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@ExtendWith(MockitoExtension.class)
public class TaskController_DeleteTaskTest {

    @Mock
    private CommandDispatcher dispatcher;

    @InjectMocks
    private TaskController controller;

    @Test
    public void whenDeletingATask_ReturnNoContent() throws NoHandlerException {


        Mockito.when(dispatcher.handle(Mockito.any(DeleteSingleTaskCommand.class)))
                .thenReturn("removed");

        ResponseEntity<?> response = controller.deleteTask(1, AuthMother.authentication());

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "the status is wrong");
    }

}
