package net.malevy.hyperdemo.messageconverters;

import com.theoryinpractise.halbuilder5.Link;
import com.theoryinpractise.halbuilder5.Links;
import com.theoryinpractise.halbuilder5.ResourceRepresentation;
import com.theoryinpractise.halbuilder5.json.JsonRepresentationReader;
import net.malevy.hyperdemo.support.westl.Action;
import net.malevy.hyperdemo.support.westl.Input;
import net.malevy.hyperdemo.support.westl.Wstl;
import okio.ByteString;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.http.MockHttpOutputMessage;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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

        ResourceRepresentation<ByteString> rep = getHalMessageFromOutputMessage(output);

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

        ResourceRepresentation<ByteString> rep = getHalMessageFromOutputMessage(output);

        Link petLink = rep.getLinksByRel("search").get(0);
        assertEquals("has the wrong href", action.getHref().toString() + "{?name}" , Links.getHref(petLink));
        Map<String, String> map = Links.getProperties(petLink).get().toJavaMap();
        assertTrue("link is not templated", Boolean.parseBoolean(map.get(WellKnown.LinkProperties.TEMPLATED)));
    }

    @Test
    public void whenSafeLinkWithMultipleInputs_ShouldBeRenderedAsSingleTemplate() throws URISyntaxException, IOException {
        Wstl w = new Wstl();

        Input petNameInput = Input.builder()
                .name("name")
                .prompt("pet name")
                .required(true)
                .type(Input.Type.Text)
                .build();
        Input petAgeInput = Input.builder()
                .name("age")
                .prompt("pet age")
                .required(false)
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
                .input(petAgeInput)
                .build();
        w.getActions().add(action);

        MockHttpOutputMessage output = new MockHttpOutputMessage();
        this.converter.writeInternal(w, output);

        ResourceRepresentation<ByteString> rep = getHalMessageFromOutputMessage(output);

        Link petLink = rep.getLinksByRel("search").get(0);
        assertEquals("has the wrong href", action.getHref().toString() + "{?name,age}" , Links.getHref(petLink));
    }

    private ResourceRepresentation<ByteString> getHalMessageFromOutputMessage(MockHttpOutputMessage output) {
        JsonRepresentationReader halMessageReader = JsonRepresentationReader.create();
        StringReader reader = new StringReader(output.getBodyAsString());
        return halMessageReader.read(reader);
    }

}
