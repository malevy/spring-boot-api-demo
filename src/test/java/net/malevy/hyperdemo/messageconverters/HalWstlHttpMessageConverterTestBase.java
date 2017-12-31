package net.malevy.hyperdemo.messageconverters;

import com.theoryinpractise.halbuilder5.ResourceRepresentation;
import com.theoryinpractise.halbuilder5.json.JsonRepresentationReader;
import org.junit.Before;
import org.springframework.mock.http.MockHttpOutputMessage;

import java.io.StringReader;
import java.util.HashMap;

public class HalWstlHttpMessageConverterTestBase {
    protected HalWstlHttpMessageConverter converter;

    @Before
    public void setup() {
        this.converter = new HalWstlHttpMessageConverter();
    }

    protected ResourceRepresentation<HashMap> getHalMessageFromOutputMessage(MockHttpOutputMessage output) {

        JsonRepresentationReader halMessageReader = JsonRepresentationReader.create();
        StringReader reader = new StringReader(output.getBodyAsString());

        return halMessageReader.read(reader, HashMap.class);
    }
}
