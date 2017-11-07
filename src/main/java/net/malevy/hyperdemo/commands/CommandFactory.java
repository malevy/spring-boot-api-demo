package net.malevy.hyperdemo.commands;

public interface CommandFactory {

    /**
     * get an instance of the given command
     * @param command the type of command to build
     * @param <C> the command type to return
     * @return an instance of the indicated command type
     */
    <C extends Command> C getCommand(Class<C> command);

}
