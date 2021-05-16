package net.malevy.hyperdemo;


import net.malevy.hyperdemo.commands.CommandDispatcher;
import net.malevy.hyperdemo.commands.GetTasksCommand;
import net.malevy.hyperdemo.commands.NoHandlerException;
import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.support.westl.Wstl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class TaskController_GetTasksTest {

    @Mock
    private CommandDispatcher dispatcher;

    @InjectMocks
    private TaskController controller;

    private UriComponentsBuilder uriComponentsBuilder;

    @BeforeEach
    public void Setup() {
        uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");
    }

    @Test
    public void whenTasksAreFound_ReturnOK() throws NoHandlerException {

        Task t = new Task(1, "the-thing");
        Sort sort;
        Pageable pageable = PageRequest.of(1,1);
        Page<Task> result = new PageImpl<>(Collections.singletonList(t), pageable, 10);

        Mockito.when(dispatcher.handle(Mockito.any(GetTasksCommand.class)))
                .thenReturn(result);

        ResponseEntity<?> response = controller.getTasks(1, 1, uriComponentsBuilder);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "the status is wrong");
        Wstl wstl = (Wstl) response.getBody();
        Assertions.assertTrue(wstl.hasData(), "there should be data");
    }

    @Test
    public void whenNoTasksAreFound_ReturnOK() throws NoHandlerException {

        Pageable pageable = PageRequest.of(1,1);
        Page<Task> result = new PageImpl<>(Collections.emptyList(), pageable, 0);

        Mockito.when(dispatcher.handle(Mockito.any(GetTasksCommand.class)))
                .thenReturn(result);

        ResponseEntity<?> response = controller.getTasks(1, 1, uriComponentsBuilder);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "the status is wrong");
    }


}
