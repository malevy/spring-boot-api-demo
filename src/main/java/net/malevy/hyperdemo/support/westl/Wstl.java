package net.malevy.hyperdemo.support.westl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.malevy.hyperdemo.messageconverters.WellKnown;

import java.util.*;

/**
 * WeSTL - Web Service Transition Language (http://rwcbook.github.io/wstl-spec/)
 *
 * This format was designed to make it easier for service developers to focus on
 * the state transition details of a Web service instead of the resource details.
 * To that end, a minimal WeSTL document contains a list of possible state transitions.
 *
 * The top-level element in every WeSTL document. This is a REQUIRED element.
 */
@NoArgsConstructor
public class Wstl implements HasActions {

    /**
     * An OPTIONAL child property of the wstl element. It SHOULD be set to the title string of the runtime resource.
     * Document parsers MUST continue to process the document even when this element is missing.
     */
    private @Getter @Setter String title;

    private final List<Action> actions = new ArrayList<>();

    /**
     * An OPTIONAL child property of the wstl element. If it exists, it MUST be a valid content object (see below).
     * Parsers MAY use this element to render content for display. Parsers MUST ontinue to process this document
     * even when this element is missing.
     */
    private @Getter @Setter Content content;

    private final List<Datum> data = new ArrayList<>();

    /**
     * Determine if this instance has associated Actions
     * @return TRUE if this instance has Actions; otherwise FALSE
     */
    public boolean hasActions() {
        return !actions.isEmpty();
    }

    /**
     * Determines if this instance has associated data items
     * @return TRUE if this instance has associated Data; otherwise FALSE
     */
    public boolean hasData() {
        return !data.isEmpty();
    }

    /**
     * Helper to find the Action representing the identity of this resource (ie self)
     * @return the Action that represents the identity of this resource
     */
    public Optional<Action> getSelf() {
        return actions.stream()
                .filter(Action::isSelf)
                .findFirst();
    }

    /**
     * A RECOMMENDED child property of the wstl element. If it exists, it MUST be an array of action objects (see below).
     * Parsers MUST continue to process this document even when this element is missing.
     */
    public List<Action> getActions() { return Collections.unmodifiableList(this.actions); }

    /**
     * Add an action to the Actions collection
     * @param action
     */
    public void addAction(@NonNull Action action ) { this.actions.add(action); }

    /**
     * A RECOMMENDED child property of the wstl element. If it exists, it MUST be a collection of that
     * represent the data associated with the runtime request.
     */
    public List<Datum> getData() { return Collections.unmodifiableList(this.data); }

    /**
     * Add an item to the Data collection
     * @param item
     */
    public void addData(@NonNull Datum item) { this.data.add(item); }
}

