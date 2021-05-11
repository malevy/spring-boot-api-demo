package net.malevy.hyperdemo.commands.impl;

import net.malevy.hyperdemo.TaskRepository;
import net.malevy.hyperdemo.commands.CommandHandler;
import net.malevy.hyperdemo.commands.UpdateTaskCommand;
import net.malevy.hyperdemo.models.ModelMapperUtil;
import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.models.domain.TaskConverter;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Optional;

@Component
public class UpdateTaskCommandHandler implements CommandHandler<UpdateTaskCommand, Optional<Task>> {

    private final TaskRepository repository;

    @Autowired
    public UpdateTaskCommandHandler(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Task> handle(UpdateTaskCommand command) {
        Assert.notNull(command,"must supply request object");

        ModelMapper modelMapper = ModelMapperUtil.build();

        Optional<Task> task = repository
                .findById(command.getId())
                .map(TaskConverter::fromDto);

        try {
            task.ifPresent(t -> modelMapper.map(command.getTaskInput(), t));
        } catch (MappingException me) {
            // surface the actual exception
            Throwable baseCause = ModelMapperUtil.findRootException(me);
            throw new IllegalArgumentException(baseCause.getMessage());
        }

        return task.map(TaskConverter::toDto)
                .map(repository::save)
                .map(TaskConverter::fromDto);
    }

    @Override
    public boolean canHandle(Class<?> clazz) {
        return UpdateTaskCommand.class.isAssignableFrom(clazz);
    }
}
