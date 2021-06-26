package net.malevy.hyperdemo.commands;

import net.malevy.hyperdemo.AuthMother;
import net.malevy.hyperdemo.TaskRepository;
import net.malevy.hyperdemo.commands.impl.UpdateTaskCommandHandler;
import net.malevy.hyperdemo.models.dataaccess.TaskDto;
import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.models.viewmodels.TaskInputVM;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.*;

public class UpdateTaskCommandHandlerTest {

    private TaskRepository repo;
    private UpdateTaskCommandHandler handler;
    private User user = AuthMother.user();

    @BeforeEach
    public void setup() {

        repo = mock(TaskRepository.class);
        handler = new UpdateTaskCommandHandler(repo);
    }

    @Test
    public void whenCheckedForCorrectCommand_ReturnsTrue() {
        assertTrue("should support correct command type", handler.canHandle(UpdateTaskCommand.class));
    }

    @Test
    public void whenCheckedForincorrectCommand_ReturnsFalse() {
        assertFalse("should not support type", handler.canHandle(Double.class));
    }

    @Test
    public void whenTaskNotFound_ReturnEmpty() {

        when(repo.findById(0)).thenReturn(Optional.empty());

        UpdateTaskCommand command = new UpdateTaskCommand(user,0, new TaskInputVM());

        Optional<Task> task = handler.handle(command);
        assertNotNull("task was null", task);
        assertFalse("should be missing", task.isPresent());
    }

    @Test
    public void whenTaskIsFound_applyChanges() {

        TaskDto dto = new TaskDto(1, "title", "description", "low", null, null, "jack");
        when(repo.findById(1)).thenReturn(Optional.of(dto));

        // return the dto that is saved
        when(repo.save(any(TaskDto.class))).thenAnswer(a -> a.getArguments()[0]);

        TaskInputVM vm = new TaskInputVM() {{
            setDescription("new-description");
            setTitle(dto.getTitle());
            setImportance(dto.getImportance());
            setDue(LocalDate.of(2017,11,26));
        }};
        UpdateTaskCommand command = new UpdateTaskCommand(user,1, vm);

        Optional<Task> task = handler.handle(command);
        Task t = task.get();
        assertEquals("title is wrong", dto.getTitle(), t.getTitle());
        assertEquals("description is wrong", vm.getDescription(), t.getDescription());
        assertEquals("importance is wrong", dto.getImportance().toUpperCase(), t.getImportance().toString());
        assertEquals("due date is wrong", vm.getDue(), t.getDue());
    }

    @Test()
    public void whenAValidationErrorOccurs_itIsSurfaced() {

        TaskDto dto = new TaskDto(1, "title", "description", "low", null, null, "jack");
        when(repo.findById(1)).thenReturn(Optional.of(dto));

        TaskInputVM vm = new TaskInputVM() {{
            setDescription("new-description");
            setTitle("");
            setImportance(dto.getImportance());
            setDue(LocalDate.of(2017,11,26));
        }};
        UpdateTaskCommand command = new UpdateTaskCommand(user,1, vm);

        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
    }


}
