package net.malevy.hyperdemo.models.domain;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

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
