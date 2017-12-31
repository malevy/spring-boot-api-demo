package net.malevy.hyperdemo.commands.impl;

import net.malevy.hyperdemo.TaskRepository;
import net.malevy.hyperdemo.commands.CommandHandler;
import net.malevy.hyperdemo.commands.DeleteSingleTaskCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class DeleteSingleTaskCommandHandler implements CommandHandler<DeleteSingleTaskCommand, String> {

    private TaskRepository repository;

    @Autowired
    public DeleteSingleTaskCommandHandler(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public String handle(DeleteSingleTaskCommand deleteSingleTaskCommand) {
        Assert.notNull(deleteSingleTaskCommand,"must supply request object");

        repository.delete(deleteSingleTaskCommand.getId());
        return "removed";
    }

    @Override
    public boolean canHandle(Class<?> clazz) {
        return DeleteSingleTaskCommand.class.isAssignableFrom(clazz);
    }
}
