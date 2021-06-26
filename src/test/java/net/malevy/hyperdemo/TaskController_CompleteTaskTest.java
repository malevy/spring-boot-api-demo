package net.malevy.hyperdemo;


import net.malevy.hyperdemo.commands.*;
import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.support.HttpProblem;
import net.malevy.hyperdemo.support.westl.Wstl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class TaskController_CompleteTaskTest {

    @Mock
    private CommandDispatcher dispatcher;

    @InjectMocks
    private TaskController controller;

    private UriComponentsBuilder uriComponentsBuilder;
    private Authentication authentication = AuthMother.authentication();

    @BeforeEach
    public void Setup() {

        uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");
    }

    @Test
    public void whenTheTaskIsFound_ReturnOK() throws NoHandlerException {

        Task t = new Task(1, "the-thing", "jack");

        Mockito.when(dispatcher.handle(Mockito.any(MarkTaskCompleteCommand.class)))
                .thenReturn(Optional.of(t));

        ResponseEntity<?> response = controller.completeTask(1, uriComponentsBuilder, authentication);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),"the status is wrong");
        Wstl wstl = (Wstl) response.getBody();
        assertTrue(wstl.hasData(),"there should be data");
    }

    @Test
    public void whenTheTaskIsNotFound_ReturnNotFound() throws NoHandlerException {

        Mockito.when(dispatcher.handle(Mockito.any(MarkTaskCompleteCommand.class)))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.completeTask(1, uriComponentsBuilder, authentication);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "the status is wrong");
        HttpProblem p = (HttpProblem) response.getBody();
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), p.getStatus(), "the http problem status is wrong");
        Assertions.assertEquals( "Task with id 1 not found", p.getTitle(), "the title is wrong");
    }


}
