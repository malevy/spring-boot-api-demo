package net.malevy.hyperdemo;

import net.malevy.hyperdemo.messageconverters.WellKnown;
import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.support.westl.Action;
import net.malevy.hyperdemo.support.westl.Datum;
import net.malevy.hyperdemo.support.westl.Wstl;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.format.DateTimeFormatter;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public class WstlMapper {

    private final MvcUriComponentsBuilder uriBuilder;

    public static class Actions {
        public static final String SELF = "self";
        public static final String DELETE = "delete-task";
    }

    public WstlMapper(UriComponentsBuilder uriComponentsBuilder) {
        this.uriBuilder = MvcUriComponentsBuilder.relativeTo(uriComponentsBuilder);
    }

    public Wstl FromTask(Task t) {

        Wstl root = new Wstl(){{
           setTitle(String.format("Task #{%s}", t.getId()));
           getData().add(taskToDataItem(t));
        }};

        return root;
    }

    private Datum taskToDataItem(Task t) {
        Action self = createSelfAction(t);
        Action delete = createDeleteAction(t);

        Datum item = new Datum(WellKnown.Rels.ITEM);
        if (StringUtils.hasText(t.getTitle())) item.getProperties().put(Task.Properties.title, t.getTitle());
        if (StringUtils.hasText(t.getDescription())) item.getProperties().put(Task.Properties.description, t.getDescription());
        item.getProperties().put(Task.Properties.importance, t.getImportance().toString());
        if (null != t.getDue()) item.getProperties().put(Task.Properties.due, t.getDue().format(DateTimeFormatter.ISO_DATE));
        if (null != t.getCompletedOn()) item.getProperties().put(Task.Properties.completedOn, t.getCompletedOn().format(DateTimeFormatter.ISO_DATE));

        item.getActions().add(self);
        item.getActions().add(delete);

        return item;
    }

    private Action createSelfAction(Task t) {
        String selfLink = this.uriBuilder
                .withMethodName(TaskController.class, "getTask", t.getId(), null)
                .toUriString();
        return Action.builder()
                .rel(WellKnown.Rels.SELF)
                .name(Actions.SELF)
                .type(Action.Type.Safe)
                .action(Action.RequestType.Read)
                .href(URI.create(selfLink))
                .build();
    }

    private Action createDeleteAction(Task t) {
        String deleteLink = this.uriBuilder
                .withMethodName(TaskController.class, "deleteTask", t.getId())
                .toUriString();
        return Action.builder()
                .rel(WellKnown.Rels.DELETE)
                .name(Actions.DELETE)
                .type(Action.Type.Unsafe)
                .action(Action.RequestType.Remove)
                .href(URI.create(deleteLink))
                .build();
    }

}
