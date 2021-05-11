package net.malevy.hyperdemo.messageconverters;

import com.jayway.jsonpath.JsonPath;
import net.malevy.hyperdemo.support.westl.Action;
import net.malevy.hyperdemo.support.westl.Datum;
import net.malevy.hyperdemo.support.westl.Wstl;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.*;
import org.springframework.mock.http.MockHttpOutputMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class SirenWstlHttpMessageConverter_multiItemTest {

    private SirenWstlHttpMessageConverter converter;

    @BeforeEach
    public void Setup() {
        converter = new SirenWstlHttpMessageConverter();
    }

    @Test
    public void whenTheWstlContainsAMultipleItems_renderThemAsEntities() throws IOException, URISyntaxException {

        Wstl w = new Wstl();
        Action junolink = Action.builder()
                .rel(WellKnown.Rels.SELF)
                .name("juno-self")
                .href(new URI("http://server.net/pets/juno"))
                .prompt("juno")
                .type(Action.Type.Safe)
                .action(Action.RequestType.Read)
                .build();
        Datum juno = new Datum("pet");
        juno.addProperty("name", "juno");
        juno.addProperty("species", "dog");
        juno.addAction(junolink);
        w.addData(juno);

        Action shadowlink = Action.builder()
                .rel(WellKnown.Rels.SELF)
                .name("shadow-self")
                .href(new URI("http://server.net/pets/shadow"))
                .prompt("shadow")
                .type(Action.Type.Safe)
                .action(Action.RequestType.Read)
                .build();
        Datum shadow = new Datum("pet");
        shadow.addProperty("name", "shadow");
        shadow.addProperty("species", "cat");
        shadow.addAction(shadowlink);
        w.addData(shadow);

        MockHttpOutputMessage output = new MockHttpOutputMessage();
        this.converter.writeInternal(w, output);

        final String json = output.getBodyAsString();
        System.out.println(json);

        JSONArray junoEntity = JsonPath.read(json, "$.entities[?(@.properties.name == 'juno')]");
        assertThat("juno is missing", junoEntity.size(), is(1));

        JSONArray shadowEntity = JsonPath.read(json, "$.entities[?(@.properties.name == 'shadow')]");
        assertThat("shadow is missing", shadowEntity.size(), is(1));

    }


}
