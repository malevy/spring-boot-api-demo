package net.malevy.hyperdemo.commands;

import net.malevy.hyperdemo.AuthMother;
import net.malevy.hyperdemo.TaskRepository;
import net.malevy.hyperdemo.commands.impl.DeleteSingleTaskCommandHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertTrue;

public class DeleteSingleTaskCommandHandlerTest {

    private TaskRepository repo;
    private DeleteSingleTaskCommandHandler handler;

    @BeforeEach
    public void setup() {

        repo = mock(TaskRepository.class);
        handler = new DeleteSingleTaskCommandHandler(repo);
    }

    @Test
    public void whenCheckedForCorrectCommand_ReturnsTrue() {
        assertTrue("should support correct command type", handler.canHandle(DeleteSingleTaskCommand.class));
    }

    @Test
    public void whenCheckedForincorrectCommand_ReturnsFalse() {
        assertFalse("should not support type", handler.canHandle(Double.class));
    }

    @Test
    public void whenExecuted_repositoryIsCalled() {

        DeleteSingleTaskCommand command = new DeleteSingleTaskCommand(AuthMother.user()){{
            setId(0);
        }};

        String result = handler.handle(command);
        verify(repo, times(1)).deleteById(0);
    }

}
