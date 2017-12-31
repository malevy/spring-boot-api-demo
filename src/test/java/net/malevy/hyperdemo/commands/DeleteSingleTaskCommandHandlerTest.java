package net.malevy.hyperdemo.commands;

import net.malevy.hyperdemo.TaskRepository;
import net.malevy.hyperdemo.commands.impl.DeleteSingleTaskCommandHandler;
import net.malevy.hyperdemo.commands.impl.GetSingleTaskCommandHandler;
import net.malevy.hyperdemo.models.dataaccess.TaskDto;
import net.malevy.hyperdemo.models.domain.Task;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.verification.Times;
import org.mockito.verification.VerificationMode;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DeleteSingleTaskCommandHandlerTest {

    private TaskRepository repo;
    private DeleteSingleTaskCommandHandler handler;

    @Before
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

        DeleteSingleTaskCommand command = new DeleteSingleTaskCommand(){{
            setId(0);
        }};

        String result = handler.handle(command);
        verify(repo, times(1)).delete(0);
    }

}
