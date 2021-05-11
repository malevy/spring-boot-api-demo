package net.malevy.hyperdemo.messageconverters;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import net.malevy.hyperdemo.support.westl.Content;
import net.malevy.hyperdemo.support.westl.Datum;
import net.malevy.hyperdemo.support.westl.Wstl;
import org.junit.jupiter.api.*;
import org.springframework.mock.http.MockHttpOutputMessage;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SirenWstlHttpMessageConverter_singleItemTest {

    private String jsonResult;
    private final String expectedContentText = "hello world";

    @BeforeEach
    public void Setup() throws IOException {


        Wstl w = new Wstl();
        Datum juno = new Datum("pet");
        juno.addProperty("name", "juno");
        juno.addProperty("breed", "mixed");
        w.addData(juno);
        w.setContent(new Content(){{
            setType(Type.Text);
            setText(expectedContentText);
        }});

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

    @Test()
    public void theRelIsRemoved() {

        assertThrows(PathNotFoundException.class, () -> JsonPath.read(jsonResult, "$.rel")) ;
    }

    @Test
    public void theContentIsRendered() {
        assertThat("the content is wrong",
                JsonPath.read(jsonResult, "$.properties.content"), is(expectedContentText));
    }

}
