package net.malevy.hyperdemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.malevy.hyperdemo.commands.CommandDispatcher;
import net.malevy.hyperdemo.commands.UpdateTaskCommand;
import net.malevy.hyperdemo.models.dataaccess.TaskDto;
import net.malevy.hyperdemo.models.viewmodels.TaskInputVM;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TaskController.class)
public class TaskController_updateTaskIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    CommandDispatcher dispatcher;

    @Test
    public void whenUpdatingATask_theDueDateIsParsedCorrectly() throws Exception {

        TaskInputVM taskInput = new TaskInputVM(){{
            setTitle("a-title");
            setDue(LocalDate.of(2017,11,26));
        }};
        ObjectMapper mapper = new ObjectMapper();
        String requestContent = mapper.writeValueAsString(taskInput);

        TaskDto dto1 = new TaskDto(1, "old-title", null, null, null, null);

        when(dispatcher.handle(any(UpdateTaskCommand.class))).thenReturn(Optional.empty());

        MockHttpServletRequestBuilder rb = put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent)
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(rb);

        ArgumentCaptor<UpdateTaskCommand> cmdCaptor = ArgumentCaptor.forClass(UpdateTaskCommand.class);

        verify(dispatcher).handle(cmdCaptor.capture());

        UpdateTaskCommand capturedCommand = cmdCaptor.getValue();
        Assertions.assertEquals(
                taskInput.getDue(),
                capturedCommand.getTaskInput().getDue(),
                "the due date was not converted correctly");

    }

}

