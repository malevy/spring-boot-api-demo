package net.malevy.hyperdemo.commands;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.User;

public class DeleteSingleTaskCommand extends AbstractCommand<String> {

    @Getter
    @Setter
    private Integer id;

    public DeleteSingleTaskCommand(User user) {
        super(user);
    }

    public DeleteSingleTaskCommand(User user, Integer id) {
        super(user);
        this.id = id;
    }
}
