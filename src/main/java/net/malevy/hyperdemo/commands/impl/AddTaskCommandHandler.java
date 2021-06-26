package net.malevy.hyperdemo.commands.impl;

import net.malevy.hyperdemo.TaskRepository;
import net.malevy.hyperdemo.commands.AddTaskCommand;
import net.malevy.hyperdemo.commands.CommandHandler;
import net.malevy.hyperdemo.models.ModelMapperUtil;
import net.malevy.hyperdemo.models.dataaccess.TaskDto;
import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.models.domain.TaskConverter;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class AddTaskCommandHandler implements CommandHandler<AddTaskCommand, Task> {

    private final TaskRepository repository;

    @Autowired
    public AddTaskCommandHandler(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public Task handle(final AddTaskCommand command) {
        Assert.notNull(command,"must supply request object");
        Assert.notNull(command.getTaskInput(), "supplied request does not contain an input value");

        final ModelMapper modelMapper = ModelMapperUtil.build();

        try {
            final Task task = new Task(-1, command.getTaskInput().getTitle(), command.getUser().getUsername());

            modelMapper.map(command.getTaskInput(), task);
            final TaskDto dto = TaskConverter.toDto(task);
            final TaskDto persistedDto = repository.save(dto);
            return TaskConverter.fromDto(persistedDto) ;
        } catch (MappingException me) {
            // surface the actual exception
            Throwable baseCause = ModelMapperUtil.findRootException(me);
            throw new IllegalArgumentException(baseCause.getMessage());
        }

    }

    @Override
    public boolean canHandle(Class<?> clazz) {
        return AddTaskCommand.class.isAssignableFrom(clazz);
    }
}
