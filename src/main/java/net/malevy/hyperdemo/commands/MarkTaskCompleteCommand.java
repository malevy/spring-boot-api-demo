package net.malevy.hyperdemo.commands;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.malevy.hyperdemo.models.domain.Task;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

public class MarkTaskCompleteCommand extends AbstractCommand<Optional<Task>> {

    @Getter
    @Setter
    private Integer id;

    public MarkTaskCompleteCommand(User user) {
        super(user);
    }
}
