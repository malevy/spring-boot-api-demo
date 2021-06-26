package net.malevy.hyperdemo;

import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.support.westl.Action;
import net.malevy.hyperdemo.support.westl.Wstl;
import org.junit.jupiter.api.*;
import org.springframework.data.domain.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

public class WstlMapper_fromPageTest {


    private WstlMapper mapper;

    @BeforeEach
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

        Assertions.assertEquals(Action.Type.Safe, selfAction.getType(), "type is wrong");
        Assertions.assertEquals(Action.RequestType.Read, selfAction.getAction(), "action is wrong");
        Assertions.assertEquals("http://localhost/tasks/", selfAction.getHref().toString(), "uri is wrong");
    }

    @Test
    public void hasSelfCollectionAction() {

        Sort sort;
        Pageable pageable = PageRequest.of(0,10);
        Page<Task> result = new PageImpl<>(Collections.emptyList(), pageable, 0);

        Wstl wstl = mapper.fromPageOfTasks(result);

        Action selfAction = wstl.getActions().stream()
                .filter(a -> WstlMapper.Actions.SELF.equalsIgnoreCase(a.getName()))
                .findFirst()
                .get();

        Assertions.assertEquals(Action.Type.Safe, selfAction.getType(), "type is wrong");
        Assertions.assertEquals(Action.RequestType.Read, selfAction.getAction(), "action is wrong");
        Assertions.assertEquals("http://localhost/tasks/?page=0&size=10", selfAction.getHref().toString(), "uri is wrong");
    }

    @Test
    public void hasNextCollectionAction() {

        Task t = new Task(42, "new one", "jack");
        Sort sort;
        Pageable pageable = PageRequest.of(0,1 );
        Page<Task> result = new PageImpl<>(Collections.singletonList(t), pageable, 10);

        Wstl wstl = mapper.fromPageOfTasks(result);

        Action selfAction = wstl.getActions().stream()
                .filter(a -> WstlMapper.Actions.NEXT.equalsIgnoreCase(a.getName()))
                .findFirst()
                .get();

        Assertions.assertEquals(Action.Type.Safe, selfAction.getType(), "type is wrong");
        Assertions.assertEquals(Action.RequestType.Read, selfAction.getAction(), "action is wrong");
        Assertions.assertEquals("http://localhost/tasks/?page=1&size=1", selfAction.getHref().toString(), "uri is wrong");
    }

    @Test
    public void hasPreviousCollectionAction() {

        Task t = new Task(42, "new one", "jack");
        Pageable pageable = PageRequest.of(1, 1);
        Page<Task> result = new PageImpl<>(Collections.singletonList(t), pageable, 10);

        Wstl wstl = mapper.fromPageOfTasks(result);

        Action selfAction = wstl.getActions().stream()
                .filter(a -> WstlMapper.Actions.PREVIOUS.equalsIgnoreCase(a.getName()))
                .findFirst()
                .get();

        Assertions.assertEquals(Action.Type.Safe, selfAction.getType(), "type is wrong");
        Assertions.assertEquals(Action.RequestType.Read, selfAction.getAction(), "action is wrong");
        Assertions.assertEquals("http://localhost/tasks/?page=0&size=1", selfAction.getHref().toString(), "uri is wrong");
    }

    @Test
    public void whenTasksAreFound_theyAreIncluded() {

        Task t = new Task(42, "new one", "jack");
        Pageable pageable = PageRequest.of(1, 1);
        Page<Task> result = new PageImpl<>(Collections.singletonList(t), pageable, 10);

        Wstl wstl = mapper.fromPageOfTasks(result);

        Assertions.assertEquals(1, wstl.getData().size(), "the data collection should not be empty");
    }

    @Test
    public void whenNoTasksAreFound_theDataCollectionIsEmpty() {

        Pageable pageable = PageRequest.of(1, 1);
        Page<Task> result = new PageImpl<>(Collections.emptyList(), pageable, 0);

        Wstl wstl = mapper.fromPageOfTasks(result);

        Assertions.assertEquals(0, wstl.getData().size(), "the data collection should not be empty");
    }

}
