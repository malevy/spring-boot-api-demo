package net.malevy.hyperdemo.commands;

import lombok.Getter;
import org.springframework.security.core.userdetails.User;

public class AbstractCommand<TResponse> implements Command<TResponse> {

    @Getter
    private final User user;

    public AbstractCommand(User user) {
        this.user = user;
    }
}

