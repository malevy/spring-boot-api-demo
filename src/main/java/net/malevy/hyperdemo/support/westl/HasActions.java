package net.malevy.hyperdemo.support.westl;

public interface HasActions {

    /**
     * A RECOMMENDED child property. If it exists, it MUST be an array of action objects (see below).
     * Parsers MUST continue to process this document even when this element is missing.
     */
    Iterable<Action> getActions();

    /**
     * Determine if this instance has associated Actions
     * @return TRUE if this instance has Actions; otherwise FALSE
     */
    boolean hasActions();
}
