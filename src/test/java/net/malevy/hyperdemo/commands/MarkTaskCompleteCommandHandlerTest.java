package net.malevy.hyperdemo.commands;

import net.malevy.hyperdemo.AuthMother;
import net.malevy.hyperdemo.TaskRepository;
import net.malevy.hyperdemo.commands.impl.MarkTaskCompleteCommandHandler;
import net.malevy.hyperdemo.models.dataaccess.TaskDto;
import net.malevy.hyperdemo.models.domain.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.*;

public class MarkTaskCompleteCommandHandlerTest {

    private TaskRepository repo;
    private MarkTaskCompleteCommandHandler handler;
    private User user = AuthMother.user();

    @BeforeEach
    public void setup() {

        repo = mock(TaskRepository.class);
        handler = new MarkTaskCompleteCommandHandler(repo);
    }

    @Test
    public void whenCheckedForCorrectCommand_ReturnsTrue() {
        assertTrue("should support correct command type", handler.canHandle(MarkTaskCompleteCommand.class));
    }

    @Test
    public void whenCheckedForincorrectCommand_ReturnsFalse() {
        assertFalse("should not support type", handler.canHandle(Double.class));
    }

    @Test
    public void whenTaskNotFound_ReturnEmpty() {

        when(repo.findById(0)).thenReturn(Optional.empty());

        MarkTaskCompleteCommand command = new MarkTaskCompleteCommand(user){{
            setId(0);
        }};

        Optional<Task> task = handler.handle(command);
        assertNotNull("task was null", task);
        assertFalse("should be empty", task.isPresent());
    }

    @Test
    public void whenTaskIsFound_MarkTheTaskComplete() {

        TaskDto dto = new TaskDto(1, "title", "description", "low", null, null, "jack");
        when(repo.findById(1)).thenReturn(Optional.of(dto));

        MarkTaskCompleteCommand command = new MarkTaskCompleteCommand(user){{
            setId(1);
        }};

        Optional<Task> task = handler.handle(command);
        assertNotNull("task was null", task);
        assertTrue("should be empty", task.get().isComplete());
    }

    @Test
    public void whenTaskIsFound_SaveTheCompletedTask() {

        TaskDto dto = new TaskDto(1, "title", "description", "low", null, null, "jack");
        when(repo.findById(1)).thenReturn(Optional.of(dto));

        MarkTaskCompleteCommand command = new MarkTaskCompleteCommand(user){{
            setId(1);
        }};

        Optional<Task> task = handler.handle(command);

        ArgumentCaptor<TaskDto> dtoCaptor = ArgumentCaptor.forClass(TaskDto.class);
        verify(repo, times(1)).save(dtoCaptor.capture());

        TaskDto capturedDto = dtoCaptor.getValue();
        assertEquals("the id is wrong", dto.getId(), capturedDto.getId());
        assertNotNull("the completedOn date should be set", capturedDto.getCompletedOn());
    }


}
