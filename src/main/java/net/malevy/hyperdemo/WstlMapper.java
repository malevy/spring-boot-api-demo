package net.malevy.hyperdemo;

import net.malevy.hyperdemo.messageconverters.WellKnown;
import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.support.westl.Action;
import net.malevy.hyperdemo.support.westl.Datum;
import net.malevy.hyperdemo.support.westl.Wstl;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class WstlMapper {

    private final MvcUriComponentsBuilder uriBuilder;

    public static class Actions {

        public static final String SELF = "self";
        public static final String DELETE = "delete-task";
        public static final String MARKCOMPLETE = "mark-complete";
        public static final String COLLECTION = "collection";
        public static final String NEXT = "next";
        public static final String PREVIOUS = "previous";
    }

    public WstlMapper(UriComponentsBuilder uriComponentsBuilder) {
        this.uriBuilder = MvcUriComponentsBuilder.relativeTo(uriComponentsBuilder);
    }

    public Wstl fromTask(Task t) {

        Wstl root = new Wstl(){{
           setTitle(String.format("Task #{%s}", t.getId()));
           getData().add(taskToDataItem(t));
        }};

        return root;
    }

    public Wstl fromPageOfTasks(Page<Task> pageOfTasks) {

        Assert.notNull(pageOfTasks, "must provide a page instance");

        Wstl root = new Wstl(){{
            setTitle("tasks");
        }};
        List<Datum> data = root.getData();

        root.getActions().add(this.createCollectionAction());
        root.getActions().add(this.createCollectionAction(WellKnown.Rels.SELF, Actions.SELF, pageOfTasks.getNumber(), pageOfTasks.getSize()));

        if (pageOfTasks.hasNext()) {
            root.getActions().add(this.createCollectionAction(WellKnown.Rels.NEXT, Actions.NEXT,
                    pageOfTasks.nextPageable().getPageNumber(),
                    pageOfTasks.nextPageable().getPageSize()));
        }

        if (pageOfTasks.hasPrevious()) {
            root.getActions().add(this.createCollectionAction(WellKnown.Rels.PREVIOUS, Actions.PREVIOUS,
                    pageOfTasks.previousPageable().getPageNumber(),
                    pageOfTasks.previousPageable().getPageSize()));
        }

        pageOfTasks.forEach(t -> data.add(taskToDataItem(t)));

        return root;
    }

    private Datum taskToDataItem(Task t) {

        Datum item = new Datum(WellKnown.Rels.ITEM);
        if (StringUtils.hasText(t.getTitle())) item.getProperties().put(Task.Properties.title, t.getTitle());
        if (StringUtils.hasText(t.getDescription())) item.getProperties().put(Task.Properties.description, t.getDescription());
        item.getProperties().put(Task.Properties.importance, t.getImportance().toString());
        if (null != t.getDue()) item.getProperties().put(Task.Properties.due, t.getDue().format(DateTimeFormatter.ISO_DATE));
        if (null != t.getCompletedOn()) item.getProperties().put(Task.Properties.completedOn, t.getCompletedOn().format(DateTimeFormatter.ISO_DATE));

        item.getActions().add(createTaskSelfAction(t));
        item.getActions().add(createTaskDeleteAction(t));
        if (! t.isComplete()) item.getActions().add(markTaskCompleteAction(t));

        return item;
    }

    private Action createCollectionAction() {
        String selfLink = this.uriBuilder
                .withMethodName(TaskController.class, "getTasks", null, null, null)
                .toUriString();
        return Action.builder()
                .rel(WellKnown.Rels.COLLECTION)
                .name(Actions.COLLECTION)
                .type(Action.Type.Safe)
                .action(Action.RequestType.Read)
                .href(URI.create(selfLink))
                .build();
    }

    private Action createCollectionAction(String rel, String name, int page, int pageSize) {
        String selfLink = this.uriBuilder
                .withMethodName(TaskController.class, "getTasks", null, null, null)
                .queryParam("page", page)
                .queryParam("size", pageSize)
                .toUriString();
        return Action.builder()
                .rel(rel)
                .name(name)
                .type(Action.Type.Safe)
                .action(Action.RequestType.Read)
                .href(URI.create(selfLink))
                .build();
    }

    private Action createTaskSelfAction(Task t) {
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

    private Action createTaskDeleteAction(Task t) {
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

    private Action markTaskCompleteAction(Task t) {
        String completeLink = this.uriBuilder
                .withMethodName(TaskController.class, "completeTask", t.getId(), null)
                .toUriString();
        return Action.builder()
                .rel(WellKnown.Rels.MARKCOMPLETE)
                .name(Actions.MARKCOMPLETE)
                .type(Action.Type.Unsafe)
                .action(Action.RequestType.Replace)
                .href(URI.create(completeLink))
                .build();
    }


}
