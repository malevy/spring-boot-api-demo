package net.malevy.hyperdemo.messageconverters;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import net.malevy.hyperdemo.support.westl.Datum;
import net.malevy.hyperdemo.support.westl.Wstl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.http.MockHttpOutputMessage;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SirenWstlHttpMessageConverter_singleItemTest {

    private String jsonResult;

    @Before
    public void Setup() throws IOException {


        Wstl w = new Wstl();
        Datum juno = new Datum("pet");
        juno.getProperties().put("name", "juno");
        juno.getProperties().put("breed", "mixed");
        w.addData(juno);

        MockHttpOutputMessage output = new MockHttpOutputMessage();
        SirenWstlHttpMessageConverter converter = new SirenWstlHttpMessageConverter();
        converter.writeInternal(w, output);

        jsonResult = output.getBodyAsString();
    }

    @Test
    public void thePropertiesAreRendered() {

        assertThat("name is wrong", JsonPath.read(jsonResult, "$.properties.name"), is("juno"));
        assertThat("breed is wrong", JsonPath.read(jsonResult, "$.properties.breed"), is("mixed"));
    }

    @Test(expected = PathNotFoundException.class)
    public void theRelIsRemoved() {
        JsonPath.read(jsonResult, "$.rel");
    }

}
