package net.malevy.hyperdemo.messageconverters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.theoryinpractise.halbuilder5.Link;
import com.theoryinpractise.halbuilder5.Links;
import com.theoryinpractise.halbuilder5.ResourceRepresentation;
import com.theoryinpractise.halbuilder5.json.JsonRepresentationReader;
import net.malevy.hyperdemo.support.westl.Action;
import net.malevy.hyperdemo.support.westl.DataItem;
import net.malevy.hyperdemo.support.westl.Input;
import net.malevy.hyperdemo.support.westl.Wstl;
import okio.ByteString;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.GenericTypeResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.http.MockHttpOutputMessage;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

public class HalWstlHttpMessageConverter_singleItemTest {

    private HalWstlHttpMessageConverter converter;

    @Before
    public void setup() {
        this.converter = new HalWstlHttpMessageConverter();
    }

    @Test
    public void converterSupportsHal() {

        MediaType applicationHal = new MediaType("application", "hal+json");

        Optional<MediaType> compatibleMediaType = this.converter.getSupportedMediaTypes()
                .stream()
                .filter(applicationHal::isCompatibleWith)
                .findFirst();

        assertTrue("compatible media type is not present", compatibleMediaType.isPresent());
    }

    @Test
    public void canWriteHal() {
        boolean canWriteHal = this.converter.canWrite(Wstl.class, HalWstlHttpMessageConverter.halJson);

        assertTrue("the converter cannot write hal", canWriteHal);
    }

    @Test
    public void cannotReadHal() {
        boolean canReadHal = this.converter.canRead(Wstl.class, HalWstlHttpMessageConverter.halJson);

        assertFalse("the converter should not read hal", canReadHal);
    }

    @Test
    public void whenTopLevelActionsExist_TheyAreWrittenOut() throws URISyntaxException, IOException {
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

        MockHttpOutputMessage output = new MockHttpOutputMessage();
        this.converter.writeInternal(w, output);

        ResourceRepresentation<HashMap> rep = getHalMessageFromOutputMessage(output);

        Link petLink = rep.getLinksByRel("pet").get(0);
        assertEquals("has the wrong href", action.getHref().toString(), Links.getHref(petLink));
        Map<String, String> map = Links.getProperties(petLink).get().toJavaMap();
        assertEquals("title is wrong", action.getPrompt(), map.get(WellKnown.LinkProperties.TITLE));
        assertFalse("link is not templated", Boolean.parseBoolean(map.get(WellKnown.LinkProperties.TEMPLATED)));
    }

    @Test
    public void whenSafeLinkWithInputs_ShouldBeRenderedAsTemplatedLink() throws URISyntaxException, IOException {
        Wstl w = new Wstl();

        Input petNameInput = Input.builder()
                .name("name")
                .prompt("pet name")
                .required(true)
                .type(Input.Type.Text)
                .build();
        Action action = Action.builder()
                .rel("search")
                .name("search-pets")
                .href(new URI("http://server.net/pets"))
                .prompt("search pets")
                .type(Action.Type.Safe)
                .action(Action.RequestType.Read)
                .input(petNameInput)
                .build();
        w.getActions().add(action);

        MockHttpOutputMessage output = new MockHttpOutputMessage();
        this.converter.writeInternal(w, output);

        ResourceRepresentation<HashMap> rep = getHalMessageFromOutputMessage(output);

        Link petLink = rep.getLinksByRel("search").get(0);
        assertEquals("has the wrong href", action.getHref().toString() + "{?name}" , Links.getHref(petLink));
        Map<String, String> map = Links.getProperties(petLink).get().toJavaMap();
        assertTrue("link is not templated", Boolean.parseBoolean(map.get(WellKnown.LinkProperties.TEMPLATED)));
    }

    @Test
    public void whenASelfActionIsPresent_RenderItAsALink() throws URISyntaxException, IOException {
        Wstl w = new Wstl();

        Action action = Action.builder()
                .rel(WellKnown.Rels.SELF)
                .name("juno")
                .href(new URI("http://server.net/pets/1"))
                .prompt("Juno")
                .type(Action.Type.Safe)
                .action(Action.RequestType.Read)
                .build();
        w.getActions().add(action);

        MockHttpOutputMessage output = new MockHttpOutputMessage();
        this.converter.writeInternal(w, output);

        ResourceRepresentation<HashMap> rep = getHalMessageFromOutputMessage(output);

        Link petLink = rep.getLinksByRel(WellKnown.Rels.SELF).get(0);
        assertEquals("has the wrong href", action.getHref().toString(), Links.getHref(petLink));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void whenASingleDataItemExists_RenderItAtTheRoot() throws URISyntaxException, IOException {
        Wstl w = new Wstl();

        DataItem di = new DataItem();
        di.getProperties().put("foo","bar");
        di.getProperties().put("now","n' later");
        w.getData().add(di);

        MockHttpOutputMessage output = new MockHttpOutputMessage();
        this.converter.writeInternal(w, output);

        ResourceRepresentation<HashMap> rep = getHalMessageFromOutputMessage(output);

        Map<String, String> data = (HashMap<String,String>)rep.get();
        assertEquals("foo has the wrong value", di.getProperties().get("foo"), data.get("foo"));
        assertEquals("now has the wrong value", di.getProperties().get("now"), data.get("now"));

    }


    private ResourceRepresentation<HashMap> getHalMessageFromOutputMessage(MockHttpOutputMessage output) {

        JsonRepresentationReader halMessageReader = JsonRepresentationReader.create();
        StringReader reader = new StringReader(output.getBodyAsString());

        return halMessageReader.read(reader, HashMap.class);
    }


}
