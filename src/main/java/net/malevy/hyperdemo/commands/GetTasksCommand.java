package net.malevy.hyperdemo.commands;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.malevy.hyperdemo.TaskRepository;
import net.malevy.hyperdemo.models.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.User;

public class GetTasksCommand extends AbstractCommand<Page<Task>> {

    @Getter
    @Setter
    private TaskRepository repository;

    @Getter
    @Setter
    private Integer page;

    @Getter
    @Setter
    private Integer pageSize;

    public GetTasksCommand(User user, Integer page, Integer pageSize) {
        super(user);
        this.page = page;
        this.pageSize = pageSize;
    }
}
