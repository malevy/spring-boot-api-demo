package net.malevy.hyperdemo;


import net.malevy.hyperdemo.commands.CommandDispatcher;
import net.malevy.hyperdemo.commands.GetSingleTaskCommand;
import net.malevy.hyperdemo.commands.GetTasksCommand;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(MockitoJUnitRunner.class)
public class TaskController_GetTasksTest {

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
    public void whenTasksAreFound_ReturnOK() throws NoHandlerException {

        Task t = new Task(1, "the-thing");
        Pageable pageable = new PageRequest(1,1);
        Page<Task> result = new PageImpl<>(Collections.singletonList(t), pageable, 10);

        Mockito.when(dispatcher.handle(Mockito.any(GetTasksCommand.class)))
                .thenReturn(result);

        ResponseEntity<?> response = controller.getTasks(1, 1, uriComponentsBuilder);

        assertEquals("the status is wrong", HttpStatus.OK, response.getStatusCode());
        Wstl wstl = (Wstl) response.getBody();
        assertTrue("there should be data", wstl.hasData());
    }

    @Test
    public void whenNoTasksAreFound_ReturnOK() throws NoHandlerException {

        Pageable pageable = new PageRequest(1,1);
        Page<Task> result = new PageImpl<>(Collections.emptyList(), pageable, 0);

        Mockito.when(dispatcher.handle(Mockito.any(GetTasksCommand.class)))
                .thenReturn(result);

        ResponseEntity<?> response = controller.getTasks(1, 1, uriComponentsBuilder);

        assertEquals("the status is wrong", HttpStatus.OK, response.getStatusCode());
    }


}
