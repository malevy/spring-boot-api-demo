package net.malevy.hyperdemo.commands;

import net.malevy.hyperdemo.TaskRepository;
import net.malevy.hyperdemo.commands.impl.GetTasksCommandHandler;
import net.malevy.hyperdemo.models.dataaccess.TaskDto;
import net.malevy.hyperdemo.models.domain.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetTasksCommandHandlerTest {

    private TaskRepository repo;
    private GetTasksCommandHandler handler;

    @BeforeEach
    public void Setup() {
        repo = mock(TaskRepository.class);
        handler = new GetTasksCommandHandler(repo);
    }

    @Test
    public void whenCheckedForCorrectCommand_ReturnsTrue() {
        assertTrue( handler.canHandle(GetTasksCommand.class), "should support correct command type");
    }

    @Test
    public void whenCheckedForincorrectCommand_ReturnsFalse() {
        assertFalse( handler.canHandle(Double.class), "should not support type");
    }

    @Test
    public void whenCalled_dtosAreConvertedToTasks() {
        TaskDto dto = new TaskDto(1, "title", "description", "low", null, null, "jack");

        Page<TaskDto> repoResult = new PageImpl<TaskDto>(Arrays.asList(dto));

        when(repo.findAll(any(Pageable.class))).thenReturn(repoResult);

        GetTasksCommand cmd = new GetTasksCommand(0,5);

        Page<Task> result = handler.handle(cmd);

        assertNotNull(result, "the result should not be null");
        assertEquals( 1, result.getTotalElements(), "wrong number of tasks");
    }

    @Test
    public void verifyPageNumberWithinBounds() {
        GetTasksCommand cmd = new GetTasksCommand(-10,5);
        assertEquals( 0, handler.forcePageNumberToValidRange(cmd), "lower bound enforcement is wrong");
    }

    @Test
    public void verifyPageSizeWithinBounds() {
        GetTasksCommand cmd = new GetTasksCommand(1,-10);
        assertEquals( 0, handler.forcePageSizeToValidRange(cmd), "lower bound enforcement is wrong");

        cmd.setPageSize(GetTasksCommandHandler.MAXPAGESIZE+10);
        assertEquals( (long)GetTasksCommandHandler.MAXPAGESIZE, handler.forcePageSizeToValidRange(cmd), "lower bound enforcement is wrong");
    }

    @Test
    public void whenPageNumberIsMissing_useDefault() {
        GetTasksCommand cmd = new GetTasksCommand(null,10);
        assertEquals( (long)GetTasksCommandHandler.DEFAULTPAGE, handler.forcePageNumberToValidRange(cmd), "default page number was not used");
    }

    @Test
    public void whenPageSizeIsMissing_useDefault() {
        GetTasksCommand cmd = new GetTasksCommand(10,null);
        assertEquals( (long)GetTasksCommandHandler.DEFAULTPAGESIZE, handler.forcePageSizeToValidRange(cmd), "default page size was not used");

    }
}
