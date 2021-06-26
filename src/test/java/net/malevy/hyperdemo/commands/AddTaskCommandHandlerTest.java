package net.malevy.hyperdemo.commands;

import net.malevy.hyperdemo.TaskRepository;
import net.malevy.hyperdemo.commands.impl.AddTaskCommandHandler;
import net.malevy.hyperdemo.models.dataaccess.TaskDto;
import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.models.viewmodels.TaskInputVM;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.*;

public class AddTaskCommandHandlerTest {

    private TaskRepository repo;
    private AddTaskCommandHandler handler;
    private User user;

    @BeforeEach
    public void setup() {

        repo = mock(TaskRepository.class);
        handler = new AddTaskCommandHandler(repo);
        user = new User("jack", "password", AuthorityUtils.NO_AUTHORITIES);
    }

    @Test
    public void whenCheckedForCorrectCommand_ReturnsTrue() {
        assertTrue("should support correct command type", handler.canHandle(AddTaskCommand.class));
    }

    @Test
    public void whenCheckedForincorrectCommand_ReturnsFalse() {
        assertFalse("should not support type", handler.canHandle(Double.class));
    }

    @Test()
    public void whenNoTaskProvided_throw() {

        AddTaskCommand command = new AddTaskCommand(user, new TaskInputVM());
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
    }

    @Test
    public void whenTaskInfoIsProvided_convertToATask() {

        // return the dto that is saved
        when(repo.save(any(TaskDto.class))).thenAnswer(a -> a.getArguments()[0]);

        TaskInputVM vm = new TaskInputVM() {{
            setDescription("new-description");
            setTitle("a-title");
            setImportance("high");
            setDue(LocalDate.of(2017,11,26));
        }};
        AddTaskCommand command = new AddTaskCommand(user, vm);

        Task task = handler.handle(command);
        assertEquals("title is wrong", vm.getTitle(), task.getTitle());
        assertEquals("description is wrong", vm.getDescription(), task.getDescription());
        assertEquals("importance is wrong", vm.getImportance().toUpperCase(), task.getImportance().toString());
        assertEquals("due date is wrong", vm.getDue(), task.getDue());
    }

    @Test
    public void whenATaskIsCreated_ItIsPersisted() {

        // return the dto that is saved
        when(repo.save(any(TaskDto.class))).thenAnswer(a -> a.getArguments()[0]);

        TaskInputVM vm = new TaskInputVM() {{
            setDescription("new-description");
            setTitle("a-title");
            setImportance("high");
            setDue(LocalDate.of(2017,11,26));
        }};
        AddTaskCommand command = new AddTaskCommand(user, vm);

        handler.handle(command);

        ArgumentCaptor<TaskDto> captor = ArgumentCaptor.forClass(TaskDto.class);
        verify(repo, times(1)).save(captor.capture());

        final TaskDto captured = captor.getValue();
        assertEquals("title is wrong", vm.getTitle(), captured.getTitle());
        assertEquals("description is wrong", vm.getDescription(), captured.getDescription());
        assertEquals("importance is wrong", vm.getImportance().toUpperCase(),
                captured.getImportance());
        assertEquals("due date is wrong", vm.getDue(), captured.getDue());
    }

}
