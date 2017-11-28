package net.malevy.hyperdemo.commands.impl;

import net.malevy.hyperdemo.TaskRepository;
import net.malevy.hyperdemo.commands.CommandHandler;
import net.malevy.hyperdemo.commands.UpdateTaskCommand;
import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.models.domain.TaskConverter;
import org.modelmapper.AbstractConverter;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Optional;

@Component
public class UpdateTaskCommandHandler implements CommandHandler<UpdateTaskCommand, Optional<Task>> {

    private TaskRepository repository;

    @Autowired
    public UpdateTaskCommandHandler(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Task> handle(UpdateTaskCommand command) {
        Assert.notNull(command,"must supply request object");

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(new AbstractConverter<String, Task.Importance>() {
            @Override
            protected Task.Importance convert(String s) {
                return Task.Importance.lookup(s).orElse(Task.Importance.NORMAL);
            }
        });

        Optional<Task> task = repository
                .findById(command.getId())
                .map(TaskConverter::fromDto);

        try {
            task.ifPresent(t -> modelMapper.map(command.getTaskInput(), t));
        } catch (MappingException me) {
            // surface the actual exception
            Throwable baseCause = exposeOriginalCause(me);
            throw new IllegalArgumentException(baseCause.getMessage());
        }

        return task.map(TaskConverter::toDto)
                .map(repository::save)
                .map(TaskConverter::fromDto);
    }

    private Throwable exposeOriginalCause(MappingException me) {
        Throwable cause = me.getCause();
        while (null != cause.getCause()) cause = cause.getCause();
        return cause;
    }

    @Override
    public boolean canHandle(Class<?> clazz) {
        return UpdateTaskCommand.class.isAssignableFrom(clazz);
    }
}
