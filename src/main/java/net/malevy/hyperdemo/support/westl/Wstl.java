package net.malevy.hyperdemo.support.westl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
@Getter
public class Wstl {

    /**
     * An OPTIONAL child property of the wstl element. It SHOULD be set to the title string of the runtime resource.
     * Document parsers MUST continue to process the document even when this element is missing.
     */
    private @Setter String title;

    /**
     * A RECOMMENDED child property of the wstl element. If it exists, it MUST be an array of action objects (see below).
     * Parsers MUST continue to process this document even when this element is missing.
     */
    private final List<Action> actions = new ArrayList<>();

    /**
     * An OPTIONAL child property of the wstl element. If it exists, it MUST be a valid content object (see below).
     * Parsers MAY use this element to render content for display. Parsers MUST ontinue to process this document
     * even when this element is missing.
     */
    private @Setter Content content;

    /**
     * A RECOMMENDED child property of the wstl element. If it exists, it MUST be a collection of that
     * represent the data associated with the runtime request.
     */
    private final List<DataItem> data = new ArrayList<>();

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

}

