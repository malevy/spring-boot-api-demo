package net.malevy.hyperdemo.support;

import java.util.Optional;
import java.util.function.Supplier;

public class OptionalUtils {

    /**
     * Allows for the chaining of Optional expressions
     * @param a - the Optional to return if a value is present
     * @param b - the supplier that produces the Optioanl that should be returned if the first is not present
     * @param <T> - the type of the value
     * @return - the first optional if the value is present; otherwise the second
     */
    public static <T> Optional<T> or(Optional<T> a, Supplier<Optional<T>> b) {
        return a.isPresent()
                ? a
                : b.get();
    }

}
