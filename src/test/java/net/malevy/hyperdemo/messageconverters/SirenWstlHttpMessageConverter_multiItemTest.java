package net.malevy.hyperdemo.messageconverters;

import com.jayway.jsonpath.JsonPath;
import net.malevy.hyperdemo.support.westl.Action;
import net.malevy.hyperdemo.support.westl.Datum;
import net.malevy.hyperdemo.support.westl.Wstl;
import net.minidev.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.http.MockHttpOutputMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SirenWstlHttpMessageConverter_multiItemTest {

    private SirenWstlHttpMessageConverter converter;

    @Before
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
        juno.getProperties().put("name", "juno");
        juno.getProperties().put("species", "dog");
        juno.getActions().add(junolink);
        w.getData().add(juno);

        Action shadowlink = Action.builder()
                .rel(WellKnown.Rels.SELF)
                .name("shadow-self")
                .href(new URI("http://server.net/pets/shadow"))
                .prompt("shadow")
                .type(Action.Type.Safe)
                .action(Action.RequestType.Read)
                .build();
        Datum shadow = new Datum("pet");
        shadow.getProperties().put("name", "shadow");
        shadow.getProperties().put("species", "cat");
        shadow.getActions().add(shadowlink);
        w.getData().add(shadow);

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
