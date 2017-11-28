package net.malevy.hyperdemo.commands;

import lombok.Data;
import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.models.viewmodels.TaskInputVM;
import org.springframework.util.Assert;

import java.util.Optional;

@Data
public class UpdateTaskCommand implements Command<Optional<Task>> {

    private Integer id;
    private TaskInputVM taskInput;

    public UpdateTaskCommand(Integer id, TaskInputVM taskInput) {
        Assert.notNull(taskInput, "must provide new task information");

        this.id = id;
        this.taskInput = taskInput;
    }
}
