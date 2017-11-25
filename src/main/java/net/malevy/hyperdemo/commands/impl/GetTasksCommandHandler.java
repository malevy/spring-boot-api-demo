package net.malevy.hyperdemo.commands.impl;

import net.malevy.hyperdemo.TaskRepository;
import net.malevy.hyperdemo.commands.CommandHandler;
import net.malevy.hyperdemo.commands.GetSingleTaskCommand;
import net.malevy.hyperdemo.commands.GetTasksCommand;
import net.malevy.hyperdemo.models.dataaccess.TaskDto;
import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.models.domain.TaskConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableArgumentResolver;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Optional;

@Component
public class GetTasksCommandHandler implements CommandHandler<GetTasksCommand, Page<Task>> {

    public static final Integer MAXPAGESIZE = 2000;
    public static final Integer DEFAULTPAGE = 0;
    public static final Integer DEFAULTPAGESIZE = 25;

    private TaskRepository repository;

    @Autowired
    public GetTasksCommandHandler(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<Task> handle(GetTasksCommand command) {
        Assert.notNull(command,"must supply request object");

        int pageNumber = forcePageNumberToValidRange(command);

        int pageSize = forcePageSizeToValidRange(command);

        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);

        Page<Task> page = repository.findAll(pageRequest)
                .map(TaskConverter::fromDto);

        return page;
    }

    public int forcePageSizeToValidRange(GetTasksCommand command) {
        return (int) Optional.ofNullable(command.getPageSize())
                    .map(s -> Math.max(s, 0))
                    .map(s -> Math.min(s, MAXPAGESIZE))
                    .orElse(DEFAULTPAGESIZE);
    }

    public int forcePageNumberToValidRange(GetTasksCommand command) {
        return Optional.ofNullable(command.getPage())
                    .map(p -> Math.max(p, 0))
                    .orElse(DEFAULTPAGE);
    }

    @Override
    public boolean canHandle(Class<?> clazz) {
        return GetTasksCommand.class.isAssignableFrom(clazz);
    }
}
