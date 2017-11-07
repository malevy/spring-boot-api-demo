package net.malevy.hyperdemo.models.domain;

import net.malevy.hyperdemo.models.dataaccess.TaskDto;
import org.springframework.util.Assert;

public class TaskConverter {

    public static Task fromDto(TaskDto dto) {
        Assert.notNull(dto, "must provide a DTO instance");

        Task.Importance priority = Task.Importance
                .lookup(dto.getImportance())
                .orElse(Task.Importance.NORMAL);

        Task task = new Task(dto.getId(),
                dto.getTitle(),
                dto.getDescription(),
                priority,
                dto.getDue(),
                dto.getCompletedOn());

        return task;

    }

}
