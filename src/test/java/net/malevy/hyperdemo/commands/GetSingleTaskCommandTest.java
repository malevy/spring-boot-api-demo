package net.malevy.hyperdemo.commands;

import net.malevy.hyperdemo.TaskRepository;
import net.malevy.hyperdemo.models.dataaccess.TaskDto;
import net.malevy.hyperdemo.models.domain.Task;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

public class GetSingleTaskCommandTest {

    private TaskRepository repo;
    private GetSingleTaskCommand cmd;

    @Before
    public void setup() {

        repo = mock(TaskRepository.class);
        cmd = new GetSingleTaskCommand(repo);
    }

    @Test
    public void whenTaskNotFound_ReturnEmpty() {

        when(repo.findById(0)).thenReturn(Optional.empty());

        Optional<Task> task = cmd.execute(0);
        assertNotNull("task was null", task);
        assertFalse("should be empty", task.isPresent());
    }

    @Test
    public void whenTaskIsFound_ConvertFromDtoAndReturn() {

        TaskDto dto = new TaskDto(1, "title", "description", "low", null, null);
        when(repo.findById(1)).thenReturn(Optional.of(dto));

        Optional<Task> task = cmd.execute(1);
        assertNotNull("task was null", task);
        assertTrue("should be empty", task.isPresent());
    }

}
