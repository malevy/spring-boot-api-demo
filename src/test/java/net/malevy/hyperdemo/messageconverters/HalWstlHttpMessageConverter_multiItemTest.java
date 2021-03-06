package net.malevy.hyperdemo.messageconverters;

import com.theoryinpractise.halbuilder5.ResourceRepresentation;
import io.vavr.collection.Traversable;
import net.malevy.hyperdemo.support.westl.Datum;
import net.malevy.hyperdemo.support.westl.Wstl;
import org.junit.jupiter.api.*;
import org.springframework.mock.http.MockHttpOutputMessage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import static org.springframework.test.util.AssertionErrors.assertEquals;

public class HalWstlHttpMessageConverter_multiItemTest extends HalWstlHttpMessageConverterTestBase {


    @Test
    @SuppressWarnings("unchecked")
    public void renderAsEmbedded() throws URISyntaxException, IOException {
        Wstl w = new Wstl();

        Datum di1 = new Datum(WellKnown.Rels.ITEM);
        di1.addProperty("foo","bar");
        w.addData(di1);

        Datum di2 = new Datum(WellKnown.Rels.ITEM);
        di2.addProperty("now","n' later");
        w.addData(di2);

        MockHttpOutputMessage output = new MockHttpOutputMessage();
        this.converter.writeInternal(w, output);

        ResourceRepresentation<HashMap> rep = getHalMessageFromOutputMessage(output);
        Traversable<ResourceRepresentation<?>> items = rep.getResourcesByRel("item");

        assertEquals("should be two", 2, items.length());
    }



}
