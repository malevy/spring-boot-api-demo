package net.malevy.hyperdemo;

import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.support.westl.Action;
import net.malevy.hyperdemo.support.westl.DataItem;
import net.malevy.hyperdemo.support.westl.Wstl;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.format.DateTimeFormatter;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public class WstlMapper {

    private final MvcUriComponentsBuilder uriBuilder;

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

    private DataItem taskToDataItem(Task t) {

        URI selfLink = this.uriBuilder
                .withMethodCall(on(TaskController.class).getTask(t.getId(), null))
                .build()
                .toUri();
        Action self = Action.builder()
                .rel("self")
                .name("self")
                .type(Action.Type.Safe)
                .action(Action.RequestType.Read)
                .href(selfLink)
                .build();

        DataItem item = new DataItem();
        if (StringUtils.hasText(t.getTitle())) item.getProperties().put(Task.Properties.title, t.getTitle());
        if (StringUtils.hasText(t.getDescription())) item.getProperties().put(Task.Properties.description, t.getDescription());
        item.getProperties().put(Task.Properties.importance, t.getImportance().toString());
        if (null != t.getDue()) item.getProperties().put(Task.Properties.due, t.getDue().format(DateTimeFormatter.ISO_DATE));
        if (null != t.getCompletedOn()) item.getProperties().put(Task.Properties.completedOn, t.getCompletedOn().format(DateTimeFormatter.ISO_DATE));

        item.getActions().add(self);

        return item;
    }
}
