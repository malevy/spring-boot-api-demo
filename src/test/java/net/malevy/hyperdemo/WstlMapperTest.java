package net.malevy.hyperdemo;

import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.support.westl.Action;
import net.malevy.hyperdemo.support.westl.Datum;
import net.malevy.hyperdemo.support.westl.Wstl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.Map;

public class WstlMapperTest {


    private WstlMapper mapper;

    @Before
    public void setup() {

        mapper = new WstlMapper(UriComponentsBuilder.fromUriString("http://localhost"));
    }

    @Test
    public void buildFromCompleteTask() {

        Task t = new Task(42, "new one");
        t.setDescription("more stuff");
        t.setDue(LocalDate.of(2017, 11, 12));
        t.markComplete(LocalDate.of(2017, 11, 13));

        Wstl wstl = mapper.FromTask(t);
        Datum taskItem = wstl.getData().stream()
                .findFirst()
                .get();

        Map<String, String> properties = taskItem.getProperties();
        assertEquals("title is wrong",t.getTitle(), properties.get(Task.Properties.title));
        assertEquals("description is wrong", t.getDescription(), properties.get(Task.Properties.description));
        assertEquals("importance is wrong", Task.Importance.NORMAL, Task.Importance.lookup(properties.get(Task.Properties.importance)).get());
        assertEquals("due date is wrong", t.getDue(), LocalDate.parse(properties.get(Task.Properties.due)));
    }

    @Test
    public void whenBuilt_taskHasSelfLink() {

        Task t = new Task(42, "new one");

        Wstl wstl = mapper.FromTask(t);
        Datum taskItem = wstl.getData().stream()
                .findFirst()
                .get();

        Action selfAction = taskItem.getActions().stream()
                .filter(a -> "self".equalsIgnoreCase(a.getName()))
                .findFirst()
                .get();

        assertEquals("name is wrong","self", selfAction.getName());
        assertEquals("type is wrong", Action.Type.Safe, selfAction.getType());
        assertEquals("action is wrong", Action.RequestType.Read, selfAction.getAction());
        assertEquals("uri is wrong", "http://localhost/tasks/42", selfAction.getHref().toString());
    }


}
