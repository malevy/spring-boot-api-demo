package net.malevy.hyperdemo.support.westl;

import lombok.*;

import java.util.List;

/**
 * The input object is an anonymous JSON object that contains meta-data information about
 * each input argument for a state stransition. This object contains a number of properties.
 * The only one that is REQUIRED is the name property. All others are OPTIONAL.
 */
@Builder
public class Input {

    /**
     * The name of the input argument.
     */
    private final @Getter @NonNull String name;

    /**
     * The human-readable prompt associated with the argument.
     */
    private @Getter String prompt;

    /**
     * The value for this argument. This MAY be left blank and filled in at runtime.
     * It MAY contain a placeholder that complies with the [RFC6570] specification and may be resolved at runtime.
     */
    private @Getter String value;

    /**
     * A flag to indicate this value is to be rendered as read-only at runtime.
     */
    private @Getter boolean readOnly = false;

    /**
     * A flag to indicate this value is an required input.
     */
    private @Getter boolean required = false;

    /**
     * A regex value to be used as an input validator at runtime.
     * If it exists, its value MUST comply with the [HTMLPattern] specification.
     */
    private @Getter String pattern;

    public enum Type {
        Text, TextArea, Select
    }

    /**
     * An OPTIONAL property indicating the display type used when rendering the input.
     * Valid values are textarea (render as a multiline input) and select (render as a list of input options).
     * If this property is missing or set of an unknown value, the input SHOULD be rendered as a simple
     * text input (e.g. type="text").
     */
    private @Getter Type type;

    /**
     * An OPTIONAL property indicated the values to use when rendering a select-type input of options.
     */
    private @Singular("suggest") @Getter List<SuggestItem> suggest;

}
