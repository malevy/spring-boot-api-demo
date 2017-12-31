package net.malevy.hyperdemo;


import net.malevy.hyperdemo.commands.CommandDispatcher;
import net.malevy.hyperdemo.commands.GetSingleTaskCommand;
import net.malevy.hyperdemo.commands.MarkTaskCompleteCommand;
import net.malevy.hyperdemo.commands.NoHandlerException;
import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.support.HttpProblem;
import net.malevy.hyperdemo.support.westl.Wstl;
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

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(MockitoJUnitRunner.class)
public class TaskController_CompleteTaskTest {

    @Mock
    private CommandDispatcher dispatcher;

    @InjectMocks
    private TaskController controller;

    private UriComponentsBuilder uriComponentsBuilder;

    @Before
    public void Setup() {
        uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");
    }

    @Test
    public void whenTheTaskIsFound_ReturnOK() throws NoHandlerException {

        Task t = new Task(1, "the-thing");

        Mockito.when(dispatcher.handle(Mockito.any(MarkTaskCompleteCommand.class)))
                .thenReturn(Optional.of(t));

        ResponseEntity<?> response = controller.getTask(1, uriComponentsBuilder);

        assertEquals("the status is wrong", HttpStatus.OK, response.getStatusCode());
        Wstl wstl = (Wstl) response.getBody();
        assertTrue("there should be data", wstl.hasData());
    }

    @Test
    public void whenTheTaskIsNotFound_ReturnNotFound() throws NoHandlerException {


        Mockito.when(dispatcher.handle(Mockito.any(MarkTaskCompleteCommand.class)))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.getTask(1, uriComponentsBuilder);

        assertEquals("the status is wrong", HttpStatus.NOT_FOUND, response.getStatusCode());
        HttpProblem p = (HttpProblem) response.getBody();
        assertEquals("the http problem status is wrong", HttpStatus.NOT_FOUND.value(), p.getStatus());
        assertEquals("the title is wrong", "Task with id 1 not found", p.getTitle());
    }


}
