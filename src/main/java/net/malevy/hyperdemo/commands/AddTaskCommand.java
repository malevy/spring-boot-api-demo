package net.malevy.hyperdemo.commands;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.models.viewmodels.TaskInputVM;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.Assert;

public class AddTaskCommand extends AbstractCommand<Task> {

    @Getter
    @Setter
    private TaskInputVM taskInput;

    public AddTaskCommand(User user, TaskInputVM taskInput) {
        super(user);

        Assert.notNull(taskInput, "must provide new task information");
        this.taskInput = taskInput;
    }

}
