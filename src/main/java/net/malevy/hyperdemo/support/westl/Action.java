package net.malevy.hyperdemo.support.westl;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import net.malevy.hyperdemo.messageconverters.WellKnown;

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
    private final @Getter @NonNull String name;

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
        Append, Partial, Read, Remove, Replace
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

    /**
     * the media type of the returned resource (optional).
     * refer to http://tools.ietf.org/html/rfc5988
     */
    @Builder.Default
    private @Getter String contentType = "";

    /**
     * Are there any inputs associated with this action?
     * @return boolean - TRUE if there are any inputs for this action; otherwise FALSE
     */
    public boolean hasInputs() {
        return null != this.inputs && !inputs.isEmpty();
    }

    /**
     * Determines if this Action represents the URI of the associated resource
     * @return boolean - TRUE if this is a SELF URI; otherwise FALSE
     */
    public boolean isSelf() {
        return null != rels && rels.stream().anyMatch(WellKnown.Rels.SELF::equalsIgnoreCase);
    }

    /**
     * Determines if this action represents a Safe action by evaluating the Type property
     * @return TRUE if this action is SAFE; otherwise FALSE
     */
    public boolean isSafe() {
        return Type.Safe.equals(this.type);
    }
}
