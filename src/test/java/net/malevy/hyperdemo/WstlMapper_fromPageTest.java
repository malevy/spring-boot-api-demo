package net.malevy.hyperdemo;

import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.support.westl.Action;
import net.malevy.hyperdemo.support.westl.Datum;
import net.malevy.hyperdemo.support.westl.Wstl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WstlMapper_fromPageTest {


    private WstlMapper mapper;

    @Before
    public void setup() {

        mapper = new WstlMapper(UriComponentsBuilder.fromUriString("http://localhost"));
    }

    @Test
    public void hasCollectionAction() {

        Page<Task> result = new PageImpl<Task>(Collections.emptyList());

        Wstl wstl = mapper.fromPageOfTasks(result);

        Action selfAction = wstl.getActions().stream()
                .filter(a -> WstlMapper.Actions.COLLECTION.equalsIgnoreCase(a.getName()))
                .findFirst()
                .get();

        assertEquals("type is wrong", Action.Type.Safe, selfAction.getType());
        assertEquals("action is wrong", Action.RequestType.Read, selfAction.getAction());
        assertEquals("uri is wrong", "http://localhost/tasks/", selfAction.getHref().toString());
    }

    @Test
    public void hasSelfCollectionAction() {

        Pageable pageable = new PageRequest(0,10);
        Page<Task> result = new PageImpl<>(Collections.emptyList(), pageable, 0);

        Wstl wstl = mapper.fromPageOfTasks(result);

        Action selfAction = wstl.getActions().stream()
                .filter(a -> WstlMapper.Actions.SELF.equalsIgnoreCase(a.getName()))
                .findFirst()
                .get();

        assertEquals("type is wrong", Action.Type.Safe, selfAction.getType());
        assertEquals("action is wrong", Action.RequestType.Read, selfAction.getAction());
        assertEquals("uri is wrong", "http://localhost/tasks/?page=0&size=10", selfAction.getHref().toString());
    }

    @Test
    public void hasNextCollectionAction() {

        Task t = new Task(42, "new one");
        Pageable pageable = new PageRequest(0,1);
        Page<Task> result = new PageImpl<>(Collections.singletonList(t), pageable, 10);

        Wstl wstl = mapper.fromPageOfTasks(result);

        Action selfAction = wstl.getActions().stream()
                .filter(a -> WstlMapper.Actions.NEXT.equalsIgnoreCase(a.getName()))
                .findFirst()
                .get();

        assertEquals("type is wrong", Action.Type.Safe, selfAction.getType());
        assertEquals("action is wrong", Action.RequestType.Read, selfAction.getAction());
        assertEquals("uri is wrong", "http://localhost/tasks/?page=1&size=1", selfAction.getHref().toString());
    }

    @Test
    public void hasPreviousCollectionAction() {

        Task t = new Task(42, "new one");
        Pageable pageable = new PageRequest(1,1);
        Page<Task> result = new PageImpl<>(Collections.singletonList(t), pageable, 10);

        Wstl wstl = mapper.fromPageOfTasks(result);

        Action selfAction = wstl.getActions().stream()
                .filter(a -> WstlMapper.Actions.PREVIOUS.equalsIgnoreCase(a.getName()))
                .findFirst()
                .get();

        assertEquals("type is wrong", Action.Type.Safe, selfAction.getType());
        assertEquals("action is wrong", Action.RequestType.Read, selfAction.getAction());
        assertEquals("uri is wrong", "http://localhost/tasks/?page=0&size=1", selfAction.getHref().toString());
    }

    @Test
    public void whenTasksAreFound_theyAreIncluded() {

        Task t = new Task(42, "new one");
        Pageable pageable = new PageRequest(1,1);
        Page<Task> result = new PageImpl<>(Collections.singletonList(t), pageable, 10);

        Wstl wstl = mapper.fromPageOfTasks(result);

        assertEquals("the data collection should not be empty", 1, wstl.getData().size());
    }

    @Test
    public void whenNoTasksAreFound_theDataCollectionIsEmpty() {

        Pageable pageable = new PageRequest(1,1);
        Page<Task> result = new PageImpl<>(Collections.emptyList(), pageable, 0);

        Wstl wstl = mapper.fromPageOfTasks(result);

        assertEquals("the data collection should not be empty", 0, wstl.getData().size());
    }

}
