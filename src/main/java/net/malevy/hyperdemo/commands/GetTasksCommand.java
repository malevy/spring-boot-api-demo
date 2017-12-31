package net.malevy.hyperdemo.commands;

import lombok.Data;
import net.malevy.hyperdemo.TaskRepository;
import net.malevy.hyperdemo.models.domain.Task;
import org.springframework.data.domain.Page;

@Data
public class GetTasksCommand implements Command<Page<Task>> {

    private TaskRepository repository;
    private Integer page;
    private Integer pageSize;

    public GetTasksCommand(Integer page, Integer pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }
}
