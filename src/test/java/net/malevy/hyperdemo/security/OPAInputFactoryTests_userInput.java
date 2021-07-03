package net.malevy.hyperdemo.security;

import net.malevy.hyperdemo.AuthMother;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class OPAInputFactoryTests_userInput {

    @Test
    public void usernameIsCorrect() {
        Authentication auth = AuthMother.authentication();
        Map<String, Object> input = OPAInputFactory.buildUserInput(auth);

        assertTrue(input.containsKey("name"), "name key is missing");
        assertEquals(input.get("name"), auth.getName(), "name is wrong");
    }

    @Test
    public void isAuthenticationIsCorrect() {
        Authentication auth = AuthMother.authentication(AuthMother.adminUser());
        Map<String, Object> input = OPAInputFactory.buildUserInput(auth);

        assertTrue(input.containsKey("isAuthenticated"), "isAuthenticated key is missing");
        assertTrue((Boolean) input.get("isAuthenticated"), "isAuthenticated is wrong");
    }

    @Test
    public void authoritiesAreCorrect() {
        Authentication auth = AuthMother.authentication(AuthMother.adminUser());
        Map<String, Object> input = OPAInputFactory.buildUserInput(auth);

        assertTrue(input.containsKey("authorities"), "authorities key is missing");
        assertArrayEquals(new String[]{"admin"},
                ((List<String>)input.get("authorities")).toArray(),
                "authorities are wrong");
    }


}
