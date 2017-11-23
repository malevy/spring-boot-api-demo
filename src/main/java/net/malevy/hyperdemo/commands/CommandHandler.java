package net.malevy.hyperdemo.commands;

public interface CommandHandler<TRequest extends Command<TResponse>, TResponse> {

    /**
     * Process the given request
     * @param request
     * @return
     */
    TResponse handle(TRequest request);

    /**
     * Indicates if this handler can process the specified command
     * @param clazz - the type of the command
     * @return TRUE if this handler supports the specified command; otherwise FALSE
     */
    boolean canHandle(Class<?> clazz);

}
