package net.malevy.hyperdemo.support.westl;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Datum implements HasActions {

    private final @Getter String classification;

    /**
     * A RECOMMENDED child property. If it exists, it MUST be an array of action objects (see below).
     * Parsers MUST continue to process this document even when this element is missing.
     */
    private final List<Action> actions = new ArrayList<>();

    /**
     * A RECOMMENDED property. If it exists, it MUST be a collection of name-value pairs that
     * represent the data associated with the runtime request.
     */
    private final @Getter Map<String, String> properties = new HashMap<>();

    public Datum(String classification) {
        this.classification = classification;
    }

    public List<Action> getActions() {
        return actions;
    }

    /**
     * Determine if this instance has associated Actions
     * @return TRUE if this instance has Actions; otherwise FALSE
     */
    @Override
    public boolean hasActions() {
        return !actions.isEmpty();
    }

}
