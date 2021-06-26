package net.malevy.hyperdemo;

import net.malevy.hyperdemo.messageconverters.WellKnown;
import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.support.westl.*;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.format.DateTimeFormatter;

public class WstlMapper {

    private final MvcUriComponentsBuilder uriBuilder;

    public static class Actions {

        public static final String SELF = "self";
        public static final String DELETE = "delete-task";
        public static final String MARKCOMPLETE = "mark-complete";
        public static final String COLLECTION = "collection";
        public static final String NEXT = "next";
        public static final String PREVIOUS = "previous";
        public static final String ADD = "add";
    }

    public WstlMapper(UriComponentsBuilder uriComponentsBuilder) {
        this.uriBuilder = MvcUriComponentsBuilder.relativeTo(uriComponentsBuilder);
    }

    public Wstl fromTask(Task t) {

        Wstl root = createDocument();
        root.setTitle(String.format("Task #%s", t.getId()));
        root.addData(taskToDataItem(t));

        return root;
    }

    public Wstl fromPageOfTasks(Page<Task> pageOfTasks) {

        Assert.notNull(pageOfTasks, "must provide a page instance");

        Wstl root = createDocument();
        root.setTitle("tasks");

        root.addAction(this.createCollectionAction());
        root.addAction(this.createCollectionAction(WellKnown.Rels.SELF, Actions.SELF, pageOfTasks.getNumber(), pageOfTasks.getSize()));

        if (pageOfTasks.hasNext()) {
            root.addAction(this.createCollectionAction(WellKnown.Rels.NEXT, Actions.NEXT,
                    pageOfTasks.nextPageable().getPageNumber(),
                    pageOfTasks.nextPageable().getPageSize()));
        }

        if (pageOfTasks.hasPrevious()) {
            root.addAction(this.createCollectionAction(WellKnown.Rels.PREVIOUS, Actions.PREVIOUS,
                    pageOfTasks.previousPageable().getPageNumber(),
                    pageOfTasks.previousPageable().getPageSize()));
        }

        pageOfTasks.forEach(t -> root.addData(taskToDataItem(t)));

        return root;
    }

    private Wstl createDocument() {
        Wstl root = new Wstl();
        root.addAction(addTaskAction());

        return root;
    }

    private Datum taskToDataItem(Task t) {

        Datum item = new Datum(WellKnown.Rels.ITEM);
        if (StringUtils.hasText(t.getTitle())) item.addProperty(Task.Properties.title, t.getTitle());
        if (StringUtils.hasText(t.getDescription())) item.addProperty(Task.Properties.description, t.getDescription());
        item.addProperty(Task.Properties.importance, t.getImportance().toString());
        if (null != t.getDue()) item.addProperty(Task.Properties.due, t.getDue().format(DateTimeFormatter.ISO_DATE));
        if (null != t.getCompletedOn()) item.addProperty(Task.Properties.completedOn, t.getCompletedOn().format(DateTimeFormatter.ISO_DATE));
        if (StringUtils.hasText(t.getOwner())) item.addProperty(Task.Properties.owner, t.getOwner());

        item.addAction(createTaskSelfAction(t));
        if ( t.canDelete() ) item.addAction(createTaskDeleteAction(t));
        if ( t.canComplete()) item.addAction(markTaskCompleteAction(t));

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
                .withMethodName(TaskController.class, "getTask", t.getId(), null, null)
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
                .withMethodName(TaskController.class, "deleteTask", t.getId(), null)
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
                .withMethodName(TaskController.class, "completeTask", t.getId(), null, null)
                .toUriString();
        return Action.builder()
                .rel(WellKnown.Rels.MARKCOMPLETE)
                .name(Actions.MARKCOMPLETE)
                .type(Action.Type.Unsafe)
                .action(Action.RequestType.Replace)
                .href(URI.create(completeLink))
                .build();
    }

    private Action addTaskAction() {
        final String addLink = this.uriBuilder
                .withMethodName(TaskController.class, "addTask", null, null, null)
                .toUriString();

        final Input titleInput = Input.builder()
                .name("title")
                .prompt("title")
                .required(true)
                .type(Input.Type.Text)
                .build();

        final Input descriptionInput = Input.builder()
                .name("description")
                .prompt("description")
                .required(false)
                .type(Input.Type.Text)
                .build();

        final Input importanceInput = Input.builder()
                .name("importance")
                .prompt("importance")
                .required(false)
                .type(Input.Type.Select)
                .suggest(SuggestItem.from(Task.Importance.NORMAL.toString()))
                .suggest(SuggestItem.from(Task.Importance.HIGH.toString()))
                .suggest(SuggestItem.from(Task.Importance.LOW.toString()))
                .build();

        final Input dueInput = Input.builder()
                .name("due")
                .prompt("due date")
                .required(false)
                .pattern("yyyy-mm-dd")
                .type(Input.Type.Text)
                .build();

        return Action.builder()
                .rel(WellKnown.Rels.ADD)
                .name(Actions.ADD)
                .type(Action.Type.Unsafe)
                .action(Action.RequestType.Append)
                .href(URI.create(addLink))
                .input(titleInput)
                .input(descriptionInput)
                .input(importanceInput)
                .input(dueInput)
                .build();

    }

}
