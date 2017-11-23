package net.malevy.hyperdemo.commands;


public interface CommandDispatcher {

    <TResponse, TRequest extends Command<TResponse>> TResponse handle(TRequest command) throws NoHandlerException;

}
