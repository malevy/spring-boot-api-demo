package net.malevy.hyperdemo.support.westl;

import lombok.Value;

public @Value class SuggestItem {

    private String text;
    private String value;

    public static SuggestItem from(String text) {
        return new SuggestItem(text, text);
    }

}
