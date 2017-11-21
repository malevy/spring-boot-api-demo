package net.malevy.hyperdemo.messageconverters;


import com.theoryinpractise.halbuilder5.Link;
import com.theoryinpractise.halbuilder5.Links;
import com.theoryinpractise.halbuilder5.ResourceRepresentation;
import com.theoryinpractise.halbuilder5.json.JsonRepresentationWriter;
import net.malevy.hyperdemo.support.westl.Action;
import net.malevy.hyperdemo.support.westl.Input;
import net.malevy.hyperdemo.support.westl.Wstl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * HttpMessageConverter to convert a Wstl document to a JSON Hypertext Application Language
 * document (HAL)
 * https://tools.ietf.org/html/draft-kelly-json-hal-08
 */
public class HalWstlHttpMessageConverter extends AbstractHttpMessageConverter<Wstl> {

    public final static MediaType halJson = new MediaType("application", "hal+json");

    @Autowired
    public HalWstlHttpMessageConverter() {
        super(halJson);
    }

    /**
     * Indicates whether the given class is supported by this converter.
     *
     * @param clazz the class to test for support
     * @return {@code true} if supported; {@code false} otherwise
     */
    @Override
    protected boolean supports(Class<?> clazz) {
        return Wstl.class.isAssignableFrom(clazz) ;
    }

    /**
     * Abstract template method that writes the actual body. Invoked from {@link #write}.
     *
     * @param wstl          the object to write to the output message
     * @param outputMessage the HTTP output message to write to
     * @throws IOException                     in case of I/O errors
     * @throws HttpMessageNotWritableException in case of conversion errors
     */
    @Override
    protected void writeInternal(Wstl wstl, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {


        List<Link> topLevelLinks = new ArrayList<>();

        // render top-level actions
        if (wstl.hasActions()) {
            List<Link> links = wstl.getActions().stream()
                    .filter(a -> !a.isSelf())
                    .map(this::buildLinkFrom)
                    .collect(Collectors.toList());
            topLevelLinks.addAll(links);
        }

        Map<String, Object> data = new HashMap<>();
        ResourceRepresentation<Map<String, Object>> rootRep = ResourceRepresentation.create(data)
                .withLinks(io.vavr.collection.List.ofAll(topLevelLinks));

        JsonRepresentationWriter writer = JsonRepresentationWriter.create();
        writer.print(rootRep).write(outputMessage.getBody());

    }

    private Link buildLinkFrom(Action action) {
        String rel = action.getRels().stream().findFirst().orElse("related");
        final String prompt = StringUtils.hasText(action.getPrompt()) ? action.getPrompt() : rel;
        String href = action.getHref().toString();

        boolean templated = false;
        List<Input> inputs = action.getInputs();

        if (action.hasInputs() && Action.Type.Safe.equals(action.getType())) {
            String allInputs = inputs.stream()
                    .map(Input::getName)
                    .collect(Collectors.joining(","));
            href = href + "{?" + allInputs +"}";
            templated = true;
        }

        Map<String, String> properties = new HashMap<>();
        properties.put(WellKnown.LinkProperties.TITLE, prompt);
        properties.put(WellKnown.LinkProperties.TEMPLATED, Boolean.toString(templated));
        return Links.create(rel, href, properties);
    }

    /**
     * Abstract template method that reads the actual object. Invoked from {@link #read}.
     *
     * @param clazz        the type of object to return
     * @param inputMessage the HTTP input message to read from
     * @return the converted object
     * @throws IOException                     in case of I/O errors
     * @throws HttpMessageNotReadableException in case of conversion errors
     */
    @Override
    protected Wstl readInternal(Class<? extends Wstl> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        // there is no support for reading reading a request into a Wstl instance
        return null;
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return false;
    }



}

