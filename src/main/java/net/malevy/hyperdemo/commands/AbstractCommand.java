package net.malevy.hyperdemo.commands;

import lombok.Getter;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.Assert;

public class AbstractCommand<TResponse> implements Command<TResponse> {

    @Getter
    private final User user;

    public AbstractCommand(User user) {
        Assert.notNull(user, "must provide the user in context");
        this.user = user;
    }
}

