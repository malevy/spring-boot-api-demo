package net.malevy.hyperdemo.commands;

import lombok.Data;
import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.models.viewmodels.TaskInputVM;
import org.springframework.util.Assert;

@Data
public class AddTaskCommand implements Command<Task> {

    private TaskInputVM taskInput;

    public AddTaskCommand(TaskInputVM taskInput) {
        Assert.notNull(taskInput, "must provide new task information");

        this.taskInput = taskInput;
    }

}
