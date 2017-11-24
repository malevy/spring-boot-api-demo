package net.malevy.hyperdemo.commands;

import net.malevy.hyperdemo.TaskRepository;
import net.malevy.hyperdemo.commands.impl.GetSingleTaskCommandHandler;
import net.malevy.hyperdemo.models.dataaccess.TaskDto;
import net.malevy.hyperdemo.models.domain.Task;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

public class GetSingleTaskCommandHandlerTest {

    private TaskRepository repo;
    private GetSingleTaskCommandHandler handler;

    @Before
    public void setup() {

        repo = mock(TaskRepository.class);
        handler = new GetSingleTaskCommandHandler(repo);
    }

    @Test
    public void whenCheckedForCorrectCommand_ReturnsTrue() {
        assertTrue("should support correct command type", handler.canHandle(GetSingleTaskCommand.class));
    }

    @Test
    public void whenCheckedForincorrectCommand_ReturnsFalse() {
        assertFalse("should not support type", handler.canHandle(Double.class));
    }

    @Test
    public void whenTaskNotFound_ReturnEmpty() {

        when(repo.findById(0)).thenReturn(Optional.empty());

        GetSingleTaskCommand command = new GetSingleTaskCommand(){{
            setId(0);
        }};

        Optional<Task> task = handler.handle(command);
        assertNotNull("task was null", task);
        assertFalse("should be empty", task.isPresent());
    }

    @Test
    public void whenTaskIsFound_ConvertFromDtoAndReturn() {

        TaskDto dto = new TaskDto(1, "title", "description", "low", null, null);
        when(repo.findById(1)).thenReturn(Optional.of(dto));

        GetSingleTaskCommand command = new GetSingleTaskCommand(){{
            setId(1);
        }};

        Optional<Task> task = handler.handle(command);
        assertNotNull("task was null", task);
        assertTrue("should be empty", task.isPresent());
    }

}