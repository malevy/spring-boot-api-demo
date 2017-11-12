package net.malevy.hyperdemo.support.westl;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Getter
public class DataItem {

    /**
     * A RECOMMENDED child property. If it exists, it MUST be an array of action objects.
     * Parsers MUST continue to process this document even when this element is missing.
     */
    private final List<Action> actions = new ArrayList<>();

    /**
     * A RECOMMENDED property of the wstl element. If it exists, it MUST be a collection of maps that
     * represent the data associated with the runtime request.
     */
    private final Map<String, String> properties = new HashMap<>();

}
