package net.malevy.hyperdemo.commands;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.models.viewmodels.TaskInputVM;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.Assert;

import java.util.Optional;

public class UpdateTaskCommand extends AbstractCommand<Optional<Task>> {

    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private TaskInputVM taskInput;

    public UpdateTaskCommand(User user, Integer id, TaskInputVM taskInput) {
        super(user);

        Assert.notNull(taskInput, "must provide new task information");

        this.id = id;
        this.taskInput = taskInput;
    }
}
