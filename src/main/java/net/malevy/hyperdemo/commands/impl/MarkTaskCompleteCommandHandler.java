package net.malevy.hyperdemo.commands.impl;

import net.malevy.hyperdemo.TaskRepository;
import net.malevy.hyperdemo.commands.CommandHandler;
import net.malevy.hyperdemo.commands.GetSingleTaskCommand;
import net.malevy.hyperdemo.commands.MarkTaskCompleteCommand;
import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.models.domain.TaskConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Optional;

@Component
public class MarkTaskCompleteCommandHandler implements CommandHandler<MarkTaskCompleteCommand, Optional<Task>> {

    private TaskRepository repository;

    @Autowired
    public MarkTaskCompleteCommandHandler(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Task> handle(MarkTaskCompleteCommand command) {
        Assert.notNull(command,"must supply request object");

        Optional<Task> task = repository
                .findById(command.getId())
                .map(TaskConverter::fromDto);

        task.ifPresent(t -> t.markComplete());
        task.map(TaskConverter::toDto)
                .ifPresent(d -> repository.save(d));

        return task;
    }

    @Override
    public boolean canHandle(Class<?> clazz) {
        return MarkTaskCompleteCommand.class.isAssignableFrom(clazz);
    }
}
