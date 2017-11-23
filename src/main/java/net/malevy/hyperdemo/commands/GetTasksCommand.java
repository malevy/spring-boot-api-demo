package net.malevy.hyperdemo.commands;

import net.malevy.hyperdemo.TaskRepository;

public class GetTasksCommand implements Command {

    private TaskRepository repository;

    public GetTasksCommand(TaskRepository repository) {
        this.repository = repository;
    }
}
