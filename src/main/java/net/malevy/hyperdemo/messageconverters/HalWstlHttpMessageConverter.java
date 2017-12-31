package net.malevy.hyperdemo.messageconverters;

import com.theoryinpractise.halbuilder5.*;
import com.theoryinpractise.halbuilder5.json.JsonRepresentationWriter;
import net.malevy.hyperdemo.support.westl.Action;
import net.malevy.hyperdemo.support.westl.Datum;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * HttpMessageConverter to convert a Wstl document to a JSON Hypertext Application Language
 * document (HAL)
 * https://tools.ietf.org/html/draft-kelly-json-hal-08
 */
public class HalWstlHttpMessageConverter extends AbstractHttpMessageConverter<Wstl> {

    public final static MediaType halJson = new MediaType("application", "hal+json");

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

        // WARNING - ResourceRepresentation instances are immutable. All operations
        // return a new instance
        ResourceRepresentation<Map<String, String>> rootRep = hasSingleDataItem(wstl)
                ? this.renderFrom(wstl.getData().get(0))
                : ResourceRepresentation.create(new HashMap<>());

        // render top-level actions that should not be self links
        if (wstl.hasActions()) {
            List<Link> links = wstl.getActions().stream()
                    .filter(a -> !a.isSelf())
                    .map(this::buildLinkFrom)
                    .collect(Collectors.toList());

            // TODO - replace this loop with a call to withLinks() when a version > 5.1.1 is released
            // withLinks() has a bug where any existing link is not carried into the new instance.
            // the bug has been fixed with https://github.com/HalBuilder/halbuilder-core/commit/fae97a80e8e93808d2d377294676983336d9dd03
            // but I've not seen an updated release to Maven yet.
            // rootRep = rootRep.withLinks(io.vavr.collection.List.ofAll(topLevelLinks));
            for(Link link : links) {
                rootRep = rootRep.withLink(link);
            }

            if (rootRep.getLinkByRel(WellKnown.Rels.SELF).isEmpty()) {

                Optional<Link> selfLink = wstl.getActions().stream()
                        .filter(Action::isSelf)
                        .map(this::buildLinkFrom)
                        .limit(1)
                        .findFirst();

                if (selfLink.isPresent()) rootRep = rootRep.withLink(selfLink.get());
            }
        }

        if (!hasSingleDataItem(wstl)) {
            for(Datum item : wstl.getData()) {
                rootRep = rootRep.withRepresentation(item.getClassification(), renderFrom(item));
            }
        }

        JsonRepresentationWriter writer = JsonRepresentationWriter.create();
        writer.print(rootRep).write(outputMessage.getBody());

    }

    private ResourceRepresentation<Map<String, String>> renderFrom(Datum datum) {

        Map<String, String> data = datum.getProperties();

        List<Link> links = datum.getActions().stream()
                .filter(a -> !a.isSelf())
                .map(this::buildLinkFrom)
                .collect(Collectors.toList());

        Optional<String> selfLink = datum.getActions().stream()
                .filter(Action::isSelf)
                .map(this::buildLinkFrom)
                .map(Links::getHref)
                .findFirst();

        ResourceRepresentation<Map<String, String>> rep = selfLink
                .map(sl -> ResourceRepresentation.create(sl, data))
                .orElse(ResourceRepresentation.create(data));

        for(Link link : links) {
            rep = rep.withLink(link);
        }

        Rel rel = Rels.singleton((StringUtils.hasText(datum.getClassification()))
                ? datum.getClassification()
                : WellKnown.Rels.ITEM);

        rep = rep.withRel(rel);

        return rep;
    }

    private boolean hasSingleDataItem(final Wstl wstl) {
        return wstl.hasData() && wstl.getData().size() == 1;
    }


    private Link buildLinkFrom(Action action) {
        String rel = action.getRels().stream().findFirst().orElse("related");
        final String prompt = StringUtils.hasText(action.getPrompt()) ? action.getPrompt() : rel;
        String href = action.getHref().toString();

        boolean templated = false;
        List<Input> inputs = action.getInputs();

        if (action.hasInputs() && Action.Type.Safe.equals(action.getType())) {

            // add the inputs as templates. See RFC6570
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

