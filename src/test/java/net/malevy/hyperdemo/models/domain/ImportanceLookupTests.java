package net.malevy.hyperdemo.models.domain;

import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertFalse;

public class ImportanceLookupTests {

    @Test
    public void missingValue_ReturnsEmpty() {
        assertFalse("missing value returns empty", Task.Importance.lookup(null).isPresent());
    }

    @Test
    public void exactStringMatch_ReturnsExpected() {
        assertEquals(Optional.of(Task.Importance.HIGH), Task.Importance.lookup("HIGH"));
    }

    @Test
    public void differentCaseShouldMatch_ReturnsExpected() {
        assertEquals(Optional.of(Task.Importance.HIGH), Task.Importance.lookup("high"));
    }

    @Test
    public void incorrectValue_ReturnsEmpty() {
        assertFalse("no value should be returned", Task.Importance.lookup("not-there").isPresent());
    }

}
