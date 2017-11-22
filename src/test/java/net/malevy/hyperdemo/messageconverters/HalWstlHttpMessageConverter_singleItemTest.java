package net.malevy.hyperdemo.messageconverters;

import com.theoryinpractise.halbuilder5.Link;
import com.theoryinpractise.halbuilder5.Links;
import com.theoryinpractise.halbuilder5.ResourceRepresentation;
import net.malevy.hyperdemo.support.westl.Action;
import net.malevy.hyperdemo.support.westl.Datum;
import net.malevy.hyperdemo.support.westl.Input;
import net.malevy.hyperdemo.support.westl.Wstl;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.http.MockHttpOutputMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

public class HalWstlHttpMessageConverter_singleItemTest extends HalWstlHttpMessageConverterTestBase {

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

        Datum di = new Datum(WellKnown.Rels.ITEM);
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

    @Test
    public void whenASingleDataItemExists_UseItsSelfLink() throws URISyntaxException, IOException {
        Wstl w = new Wstl();
        Action original = Action.builder()
                .rel(WellKnown.Rels.SELF)
                .name("noop")
                .href(new URI("http://server.net/noop/1"))
                .prompt("noop")
                .type(Action.Type.Safe)
                .action(Action.RequestType.Read)
                .build();
        w.getActions().add(original);

        Action action = Action.builder()
                .rel(WellKnown.Rels.SELF)
                .name("juno")
                .href(new URI("http://server.net/pets/1"))
                .prompt("Juno")
                .type(Action.Type.Safe)
                .action(Action.RequestType.Read)
                .build();
        Datum di = new Datum(WellKnown.Rels.ITEM);
        di.getActions().add(action);
        di.getProperties().put("foo","bar");
        di.getProperties().put("now","n' later");
        w.getData().add(di);

        MockHttpOutputMessage output = new MockHttpOutputMessage();
        this.converter.writeInternal(w, output);

        ResourceRepresentation<HashMap> rep = getHalMessageFromOutputMessage(output);
        Link petLink = rep.getLinksByRel(WellKnown.Rels.SELF).get(0);
        assertEquals("has the wrong self link", action.getHref().toString(), Links.getHref(petLink));

    }

    @Test
    public void whenASingleDataItemExists_CombineNonSelfActions() throws URISyntaxException, IOException {
        Wstl w = new Wstl();
        Action noop1 = Action.builder()
                .rel("noop1")
                .name("noop")
                .href(new URI("http://server.net/noop/1"))
                .prompt("noop")
                .type(Action.Type.Safe)
                .action(Action.RequestType.Read)
                .build();
        w.getActions().add(noop1);

        Action noop2 = Action.builder()
                .rel("noop2")
                .name("noop2")
                .href(new URI("http://server.net/noop/2"))
                .prompt("noop2")
                .type(Action.Type.Safe)
                .action(Action.RequestType.Read)
                .build();
        Datum di = new Datum(WellKnown.Rels.ITEM);
        di.getActions().add(noop2);
        w.getData().add(di);

        MockHttpOutputMessage output = new MockHttpOutputMessage();
        this.converter.writeInternal(w, output);

        ResourceRepresentation<HashMap> rep = getHalMessageFromOutputMessage(output);
        Link noop1Link = rep.getLinksByRel("noop1").get(0);
        assertEquals("noop1 was not found", noop1.getHref().toString(), Links.getHref(noop1Link));
        Link noop2Link = rep.getLinksByRel("noop2").get(0);
        assertEquals("noop2 was not found", noop2.getHref().toString(), Links.getHref(noop2Link));

    }


}
