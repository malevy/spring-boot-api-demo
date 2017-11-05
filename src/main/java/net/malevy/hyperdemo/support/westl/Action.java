package net.malevy.hyperdemo.support.westl;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

import java.net.URI;
import java.util.List;


/**
 * The action object is an anonymous JSON object that contains meta-data information about each state transition.
 * This object contains a number of properties. The only one that is REQUIRED is the name property.
 * All others are OPTIONAL.
 */
@Builder
public class Action {

    /**
     * The internal name of the transition. This is a REQUIRED property.
     */
    private final @NonNull String name;

    /**
     * A string that describes this transition. This is an OPTIONAL property.
     * Parsers MAY use this as additional information when rendering the input for users.
     */
    private @Getter String description;

    public enum Type {
        Safe, Unsafe
    }

    /**
     * Indicates the network request type for the transition.
     * It MUST be set to one of the following values: safe or unsafe.
     */
    private final @Getter @NonNull Type type;

    public enum RequestType {
        Append, Diff, Read, Remove, Replace
    }

    /**
     * Indicates the application request type for the transition.
     * It MUST be set to one of the following values: read, append, replace, remove, diff.
     */
    private final @Getter @NonNull RequestType action;

    /**
     * Contains a space-separated list of string values.
     * These values can be used to tag the transition for later search/retrieval.
     */
    private @Getter String target;

    /**
     * Contains a string that represents the human prompt for this transition.
     * This value can be used as labels for links and forms.
     */
    private @Getter String prompt;

    /**
     * Contains the URL associated with the transition. This value SHOULD only be populated in the runtime version of WeSTL documents but MAY be set at design-time.
     * If populated, this value MUST comply with the rules of [RFC3986]
     */
    private @Getter URI href;

    /**
     * Contains an collection of link relation values for the transition. This value MUST comply with the rules of [RFC5988]
     */
    private @Singular @Getter List<String> rels;

    /**
     * Contains an collection of input objects
     */
    private @Singular @Getter List<Input> inputs;

}
