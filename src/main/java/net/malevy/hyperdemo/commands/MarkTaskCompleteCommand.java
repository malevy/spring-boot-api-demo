package net.malevy.hyperdemo.commands;

import lombok.Data;
import net.malevy.hyperdemo.models.domain.Task;

import java.util.Optional;

@Data
public class MarkTaskCompleteCommand implements Command<Optional<Task>> {

    private Integer id;

}
