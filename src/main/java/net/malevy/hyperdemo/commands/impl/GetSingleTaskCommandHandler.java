package net.malevy.hyperdemo.commands.impl;

import net.malevy.hyperdemo.TaskRepository;
import net.malevy.hyperdemo.commands.CommandHandler;
import net.malevy.hyperdemo.commands.GetSingleTaskCommand;
import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.models.domain.TaskConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Optional;

@Component
public class GetSingleTaskCommandHandler implements CommandHandler<GetSingleTaskCommand, Optional<Task>> {

    private final TaskRepository repository;

    @Autowired
    public GetSingleTaskCommandHandler(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Task> handle(GetSingleTaskCommand getSingleTaskCommand) {
        Assert.notNull(getSingleTaskCommand,"must supply request object");

        return repository.findById(getSingleTaskCommand.getId())
                .map(TaskConverter::fromDto);
    }

    @Override
    public boolean canHandle(Class<?> clazz) {
        return GetSingleTaskCommand.class.isAssignableFrom(clazz);
    }
}
