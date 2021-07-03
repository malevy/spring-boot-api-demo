package net.malevy.hyperdemo.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class OPAInputFactoryTests_httpInput {

    @Test
    public void methodIsCorrect() {

        HttpServletRequest request = new MockHttpServletRequest("GET", "https://example.com/api/tasks");
        var input = OPAInputFactory.buildHttpInput(request);

        assertTrue(input.containsKey("method"), "method key is missing");
        assertEquals("GET", input.get("method"), "method has the wrong value");
    }

    @Test
    public void pathIsCorrect() {
        HttpServletRequest request = new MockHttpServletRequest("GET", "https://example.com/api/tasks");
        var input = OPAInputFactory.buildHttpInput(request);

        assertTrue(input.containsKey("path"), "path key is missing");
        assertArrayEquals(new String[]{"api", "tasks"}, ((List<String>)input.get("path")).toArray(), "path is wrong");
    }

    @Test
    public void trailingSlashOnPathIsIgnored() {
        HttpServletRequest request = new MockHttpServletRequest("GET", "https://example.com/api/tasks/");
        var input = OPAInputFactory.buildHttpInput(request);

        assertTrue(input.containsKey("path"), "path key is missing");
        assertArrayEquals(new String[]{"api", "tasks"}, ((List<String>)input.get("path")).toArray(), "path is wrong");
    }

    @Test
    public void fragmentOnPathIsIgnored() {
        HttpServletRequest request = new MockHttpServletRequest("GET", "https://example.com/api/tasks#fragment");
        var input = OPAInputFactory.buildHttpInput(request);

        assertTrue(input.containsKey("path"), "path key is missing");
        assertArrayEquals(new String[]{"api", "tasks"}, ((List<String>)input.get("path")).toArray(), "path is wrong");
    }

    @Test
    public void headersAreCaptured() {

        MockHttpServletRequest mockRequest = new MockHttpServletRequest("GET", "https://example.com/api/tasks");
        mockRequest.addHeader("my-header", "my-value");

        var input = OPAInputFactory.buildHttpInput(mockRequest);

        assertTrue(input.containsKey("headers"), "headers key is missing");
        assertEquals("my-value",
                ((Map<String, Object>)input.get("headers")).get("my-header"),
                "method has the wrong value");
    }

}
