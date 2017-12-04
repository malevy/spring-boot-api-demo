package net.malevy.hyperdemo.messageconverters;

import com.google.code.siren4j.component.Field;
import com.google.code.siren4j.component.Link;
import com.google.code.siren4j.component.impl.ActionImpl;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.sun.deploy.util.ArrayUtil;
import net.malevy.hyperdemo.support.westl.Action;
import net.malevy.hyperdemo.support.westl.Input;
import net.malevy.hyperdemo.support.westl.Wstl;
import net.minidev.json.JSONArray;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.http.MockHttpOutputMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.*;

public class SirenWstlHttpMessageConverter_singleItemTest {

    private SirenWstlHttpMessageConverter converter;

    @Before
    public void Setup() {
        converter = new SirenWstlHttpMessageConverter();
    }

    @Test
    public void converterSupportWstl() {
        assertTrue(converter.supports(Wstl.class));
    }

    @Test
    public void converterSupportsSiren() {
        Optional<MediaType> compatibleMediaTypes = converter.getSupportedMediaTypes()
                .stream()
                .filter(SirenWstlHttpMessageConverter.SIREN::isCompatibleWith)
                .findFirst();

        assertTrue("CJ should be supported by the converter", compatibleMediaTypes.isPresent());
    }

    @Test
    public void canWriteSiren() {
        assertTrue(converter.canWrite(Wstl.class, SirenWstlHttpMessageConverter.SIREN));
    }

    @Test
    public void canReadSiren() {
        assertFalse(converter.canRead(Wstl.class, SirenWstlHttpMessageConverter.SIREN));
    }

    @Test
    public void whenTopLevelActionsExist_writeThemOut() throws URISyntaxException, IOException, JSONException {
        Wstl w = new Wstl();
        Action action = Action.builder()
                .rel("pet")
                .name("juno")
                .href(new URI("http://server.net/pets/juno"))
                .prompt("juno")
                .type(Action.Type.Safe)
                .action(Action.RequestType.Read)
                .build();
        w.getActions().add(action);

        final MockHttpOutputMessage output = new MockHttpOutputMessage();
        this.converter.writeInternal(w, output);

        final String json = output.getBodyAsString();

        DocumentContext documentContext = JsonPath.parse(json);
        JSONArray result = documentContext.read("$.links[?('pet' in @.rel)].href");
        String actual = result.get(0).toString();
        assertEquals("name is wrong", action.getHref().toString(), actual);

        result = documentContext.read("$.links[?('pet' in @.rel)].title");
        actual = result.get(0).toString();
        assertEquals("title is wrong", action.getPrompt(), actual);
    }

    @Test
    public void renderAllTopLevelSafeActionsAsLinks() throws URISyntaxException, IOException {
        Wstl w = new Wstl();
        Action dogLink = Action.builder()
                .rel("pet")
                .rel("dog")
                .name("juno")
                .href(new URI("http://server.net/pets/juno"))
                .prompt("juno")
                .type(Action.Type.Safe)
                .action(Action.RequestType.Read)
                .build();
        w.getActions().add(dogLink);

        Action catlink = Action.builder()
                .rel("pet")
                .rel("cat")
                .name("shadow")
                .href(new URI("http://server.net/pets/shadow"))
                .prompt("shadow")
                .type(Action.Type.Safe)
                .action(Action.RequestType.Read)
                .build();
        w.getActions().add(catlink);

        Action addAction = Action.builder()
                .rel("add")
                .name("add")
                .href(new URI("http://server.net/pets"))
                .prompt("add")
                .type(Action.Type.Unsafe)
                .action(Action.RequestType.Append)
                .build();
        w.getActions().add(addAction);

        final MockHttpOutputMessage output = new MockHttpOutputMessage();
        this.converter.writeInternal(w, output);

        final String json = output.getBodyAsString();
        DocumentContext documentContext = JsonPath.parse(json);
        JSONArray result = documentContext.read("$.links[?('dog' in @.rel)].title");
        String actual = result.get(0).toString();
        assertEquals("title is wrong", dogLink.getPrompt(), actual);

        result = documentContext.read("$.links[?('cat' in @.rel)].title");
        actual = result.get(0).toString();
        assertEquals("title is wrong", catlink.getPrompt(), actual);

        result = documentContext.read("$.links[?('add' in @.rel)]");
        assertEquals("no link should match", 0, result.size());
    }

    @Test
    public void canBuildLinkFromAction() throws URISyntaxException {
        Action petLink = Action.builder()
                .name("petLink")
                .rel("pet")
                .type(Action.Type.Safe)
                .href(new URI("http://localhost/pet/1"))
                .action(Action.RequestType.Read)
                .prompt("june")
                .description("my dog juno")
                .target("pet")
                .build();

        Link actualLink = SirenWstlHttpMessageConverter.buildLinkFromAction(petLink);

        assertThat("the uri is wrong", actualLink.getHref(), is(petLink.getHref().toString()));
        assertThat("the rel is wrong", Arrays.asList( actualLink.getRel()), hasItems("pet"));
        assertThat("the title is wrong", actualLink.getTitle(), is(petLink.getPrompt()));
    }

    @Test
    public void canBuildActionFromAction() throws URISyntaxException {
        Action addLink = Action.builder()
                .name("addLink")
                .rel("add")
                .type(Action.Type.Unsafe)
                .href(new URI("http://localhost/pet"))
                .action(Action.RequestType.Append)
                .prompt("add pet")
                .description("add a pet")
                .target("pet")
                .build();

        com.google.code.siren4j.component.Action actual = SirenWstlHttpMessageConverter.buildAction(addLink);

        assertThat("the uri is wrong", actual.getHref(), is(addLink.getHref().toString()));
        assertThat("the name is wrong", actual.getName(), is(addLink.getName()));
        assertThat("the title is wrong", actual.getTitle(), is(addLink.getPrompt()));
        assertThat("", actual.getMethod(), is(ActionImpl.Method.POST));

    }

    @Test
    public void anInputForAnActionRendersAsAField() throws URISyntaxException {
        Input petNameInput = Input.builder()
                .name("petname")
                .prompt("nane")
                .required(true)
                .type(Input.Type.Text)
                .build();

        Action addLink = Action.builder()
                .name("addLink")
                .rel("add")
                .type(Action.Type.Unsafe)
                .href(new URI("http://localhost/pet"))
                .action(Action.RequestType.Append)
                .prompt("add pet")
                .description("add a pet")
                .target("pet")
                .input(petNameInput)
                .build();

        com.google.code.siren4j.component.Action actual = SirenWstlHttpMessageConverter.buildAction(addLink);

        Field petNameField = actual.getFields().get(0);

        assertThat("field name is wrong", petNameField.getName(), is(petNameInput.getName()));
        assertThat("prompt is wrong", petNameField.getTitle(), is(petNameInput.getPrompt()));
        assertThat("", petNameField.isRequired(), is(petNameInput.isRequired()));

    }
}
