package net.malevy.hyperdemo.support.westl;

import lombok.Getter;
import lombok.NonNull;

import java.util.*;

public class Datum implements HasActions {

    private final @Getter String classification;

    private final List<Action> actions = new ArrayList<>();

    private final Map<String, String> properties = new HashMap<>();

    public Datum(String classification) {
        this.classification = classification;
    }

    /**
     * A RECOMMENDED property. If it exists, it MUST be a collection of name-value pairs that
     * represent the data associated with the runtime request.
     */
    public Map<String, String> getProperties() { return Collections.unmodifiableMap(properties); }

    /**
     * Add a property to the properties collection
     * @param key
     * @param value
     */
    public void addProperty(@NonNull String key, String value) { this.properties.put(key, value); }

    /**
     * A RECOMMENDED child property. If it exists, it MUST be an array of action objects (see below).
     * Parsers MUST continue to process this document even when this element is missing.
     */
    public List<Action> getActions() {
        return Collections.unmodifiableList(actions);
    }

    /**
     * Add an Action to the Actions collection
     * @param action
     */
    public void addAction(@NonNull Action action ) { this.actions.add(action); }

    /**
     * Determine if this instance has associated Actions
     * @return TRUE if this instance has Actions; otherwise FALSE
     */
    @Override
    public boolean hasActions() {
        return !actions.isEmpty();
    }

}
