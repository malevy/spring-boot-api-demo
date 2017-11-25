package net.malevy.hyperdemo.commands;

import io.vavr.collection.Array;
import net.malevy.hyperdemo.TaskRepository;
import net.malevy.hyperdemo.commands.impl.GetTasksCommandHandler;
import net.malevy.hyperdemo.models.dataaccess.TaskDto;
import net.malevy.hyperdemo.models.domain.Task;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetTasksCommandHandlerTest {

    private TaskRepository repo;
    private GetTasksCommandHandler handler;

    @Before
    public void Setup() {
        repo = mock(TaskRepository.class);
        handler = new GetTasksCommandHandler(repo);
    }

    @Test
    public void whenCheckedForCorrectCommand_ReturnsTrue() {
        assertTrue("should support correct command type", handler.canHandle(GetTasksCommand.class));
    }

    @Test
    public void whenCheckedForincorrectCommand_ReturnsFalse() {
        assertFalse("should not support type", handler.canHandle(Double.class));
    }

    @Test
    public void whenCalled_dtosAreConvertedToTasks() {
        TaskDto dto = new TaskDto(1, "title", "description", "low", null, null);

        Page<TaskDto> repoResult = new PageImpl<TaskDto>(Arrays.asList(dto));

        when(repo.findAll(any(Pageable.class))).thenReturn(repoResult);

        GetTasksCommand cmd = new GetTasksCommand(0,5);

        Page<Task> result = handler.handle(cmd);

        assertNotNull("the result should not be null",result);
        assertEquals("wrong number of tasks", 1, result.getTotalElements());
    }

    @Test
    public void verifyPageNumberWithinBounds() {
        GetTasksCommand cmd = new GetTasksCommand(-10,5);
        assertEquals("lower bound enforcement is wrong", 0, handler.forcePageNumberToValidRange(cmd));
    }

    @Test
    public void verifyPageSizeWithinBounds() {
        GetTasksCommand cmd = new GetTasksCommand(1,-10);
        assertEquals("lower bound enforcement is wrong", 0, handler.forcePageSizeToValidRange(cmd));

        cmd.setPageSize(GetTasksCommandHandler.MAXPAGESIZE+10);
        assertEquals("lower bound enforcement is wrong", (long)GetTasksCommandHandler.MAXPAGESIZE, handler.forcePageSizeToValidRange(cmd));
    }

    @Test
    public void whenPageNumberIsMissing_useDefault() {
        GetTasksCommand cmd = new GetTasksCommand(null,10);
        assertEquals("default page number was not used", (long)GetTasksCommandHandler.DEFAULTPAGE, handler.forcePageNumberToValidRange(cmd));
    }

    @Test
    public void whenPageSizeIsMissing_useDefault() {
        GetTasksCommand cmd = new GetTasksCommand(10,null);
        assertEquals("default page size was not used", (long)GetTasksCommandHandler.DEFAULTPAGESIZE, handler.forcePageSizeToValidRange(cmd));

    }
}
