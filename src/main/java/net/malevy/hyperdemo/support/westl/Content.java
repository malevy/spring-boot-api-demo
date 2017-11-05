package net.malevy.hyperdemo.support.westl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * The content object is an anonymous JSON object that contains information and data for
 * rendering content for viewing. This object contains only two OPTIONAL properties (type and text).
 * If no properties are present, the content element SHOULD be ignored.
 */
@NoArgsConstructor
public class Content {

    public enum Type {
        Html, Markdown, Text
    }

    /**
     * A string representing the type of text that appears in the text property.
     * Valid values are "html", "markdown", "text". This is an OPTIONAL property.
     * Parsers SHOULD use this value as a guide on processing the contents of the text property
     * (e.g. treat the content as "html", etc.). If this property is missing or contains an
     * unrecognized value, the property SHOULD be treated as if it was set to "text".
     */
    private @Getter @Setter Type type;

    /**
     * A string representing the complete content to be rendered.
     * This content MAY require additional parsing based on the value of the type property.
     * If the type property is missing or is an unrecognized value,
     * the contents of text SHOULD be treated as plain text. This is an OPTIONAL property.
     */
    private @Getter @Setter String text;

    private final @Getter Map<String, Object> data = new HashMap<String, Object>() {
    };

}
