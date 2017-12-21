package net.malevy.hyperdemo.messageconverters;

import com.google.code.siren4j.component.Entity;
import com.google.code.siren4j.component.Field;
import com.google.code.siren4j.component.Link;
import com.google.code.siren4j.component.builder.ActionBuilder;
import com.google.code.siren4j.component.builder.EntityBuilder;
import com.google.code.siren4j.component.builder.FieldBuilder;
import com.google.code.siren4j.component.builder.LinkBuilder;
import com.google.code.siren4j.component.impl.ActionImpl;
import com.google.code.siren4j.meta.FieldType;
import net.malevy.hyperdemo.support.westl.Action;
import net.malevy.hyperdemo.support.westl.Datum;
import net.malevy.hyperdemo.support.westl.Input;
import net.malevy.hyperdemo.support.westl.Wstl;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * HttpMessageConverter to convert a Wstl document to a Collection+JSON document (CJ)
 * http://amundsen.com/media-types/collection/format/
 */
public class SirenWstlHttpMessageConverter extends AbstractHttpMessageConverter<Wstl> {

    public final static MediaType SIREN = new MediaType("application", "vnd.siren+json");

    /**
     * Construct an {@code AbstractHttpMessageConverter} with one supported media type.
     */
    public SirenWstlHttpMessageConverter() {
        super(SIREN);
    }

    /**
     * Indicates whether the given class is supported by this converter.
     *    public final static MediaType halJson = new MediaType("application", "hal+json");

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

        /**
         *    List<Item> items = new ArrayList<>();
         *    items.add(Item.create(COLLECTION_URI.resolve("item/1"), Arrays.asList(Property.value("one", Option.of("One"), Value.of(1))), Collections.<Link>emptyList()));
         *    Json.JObject collection = Collection.builder(COLLECTION_URI).addItems(items).build().asJson();
         */

        List<Link> rootLinks = wstl.getActions().stream()
                .filter(a -> a.isSafe() && !a.hasInputs())
                .map(SirenWstlHttpMessageConverter::buildLinkFromAction)
                .collect(Collectors.toList());

        List<com.google.code.siren4j.component.Action> rootActions = wstl.getActions().stream()
                .filter(a -> !a.isSafe() || a.hasInputs())
                .map(SirenWstlHttpMessageConverter::buildAction)
                .collect(Collectors.toList());

        EntityBuilder entityBuilder = EntityBuilder.createEntityBuilder();

        if (hasSingleDataItem(wstl)) {
            Datum item = wstl.getData().get(0);
            entityBuilder = apply(entityBuilder, item);
            entityBuilder.setRelationship(null); // remove rel
        } else {

            List<Entity> entities = wstl.getData().stream()
                    .map(this::renderFrom)
                    .collect(Collectors.toList());

            entityBuilder = entityBuilder.addSubEntities(entities);
        }

        entityBuilder = entityBuilder
                .addLinks(rootLinks)
                .addActions(rootActions);

        if (wstl.HasContent()) {
            entityBuilder = entityBuilder.addProperty("content", wstl.getContent().getText());
        }

        Entity rep = entityBuilder.build();

        try (final PrintWriter writer = new PrintWriter(outputMessage.getBody())) {
            writer.write(rep.toString());
        }
    }

    private Entity renderFrom(Datum datum) {

        return apply(EntityBuilder.createEntityBuilder(), datum).build();
    }

    private EntityBuilder apply(EntityBuilder builder, Datum datum) {

        String clazz = StringUtils.hasText(datum.getClassification())
                ? datum.getClassification()
                : WellKnown.Rels.ITEM;

        List<Link> links = datum.getActions().stream()
                .filter(a -> a.isSafe() && !a.hasInputs())
                .map(SirenWstlHttpMessageConverter::buildLinkFromAction)
                .collect(Collectors.toList());

        List<com.google.code.siren4j.component.Action> actions = datum.getActions().stream()
                .filter(a -> !a.isSafe() || a.hasInputs())
                .map(SirenWstlHttpMessageConverter::buildAction)
                .collect(Collectors.toList());

        return builder.setComponentClass(clazz)
                .setRelationship(clazz)
                .addProperties(convertMap(datum.getProperties()))
                .addLinks(links)
                .addActions(actions);

    }

    private boolean hasSingleDataItem(Wstl wstl) {
        return wstl.hasData() && wstl.getData().size() == 1;
    }

    private Map<String, Object> convertMap(Map<String, String> original) {

        Map<String, Object> result = new HashMap<>(original.size());
        original.forEach(result::put);

        return result;

    }

    public static Link buildLinkFromAction(Action action) {

        final String[] rels = action.getRels().toArray(new String[action.getRels().size()]);
        return LinkBuilder.createLinkBuilder()
                .setHref(action.getHref().toString())
                .setRelationship(rels)
                .setTitle(action.getPrompt())
                .setComponentClass(action.getTarget())
                .build();
    }

    public static com.google.code.siren4j.component.Action buildAction(Action action) {

        List<Field> fields = action.getInputs().stream()
                .map(SirenWstlHttpMessageConverter::buildField)
                .collect(Collectors.toList());

        return ActionBuilder.createActionBuilder()
                .setName(action.getName())
                .setTitle(action.getPrompt())
                .setMethod(mapMethod(action.getAction()))
                .setHref(action.getHref().toString())
                .addFields(fields)
                .build();

    }

    private static Field buildField(Input f) {

        return FieldBuilder.createFieldBuilder()
                .setName(f.getName())
                .setTitle(f.getPrompt())
                .setValue(f.getValue())
                .setRequired(f.isRequired())
                .setPattern(f.getPattern())
                .setType(FieldType.TEXT)
                .build();
    }


    private static ActionImpl.Method mapMethod(Action.RequestType action) {

        ActionImpl.Method method=null;
        switch (action) {
            case Append:
                method = ActionImpl.Method.POST;
                break;
            case Read:
                method = ActionImpl.Method.GET;
                break;
            case Remove:
                method = ActionImpl.Method.DELETE;
                break;
            case Replace:
                method = ActionImpl.Method.PUT;
                break;
            case Partial:
                method = ActionImpl.Method.PATCH;
                break;
            default:
                throw new IllegalArgumentException(String.format("unrecognized request type %s", action) );
        }

        return method;

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
        // there is no support for reading CJ references
        return null;
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return false;
    }

}
