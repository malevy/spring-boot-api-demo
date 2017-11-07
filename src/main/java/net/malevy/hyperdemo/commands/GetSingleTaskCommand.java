package net.malevy.hyperdemo.commands;

import net.malevy.hyperdemo.TaskRepository;
import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.models.domain.TaskConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GetSingleTaskCommand implements Command {

    private TaskRepository repository;

    public GetSingleTaskCommand(TaskRepository repository) {
        this.repository = repository;
    }

    public Optional<Task> execute(Integer id) {

        return repository.findById(id)
                .map(TaskConverter::fromDto);

    }
}
