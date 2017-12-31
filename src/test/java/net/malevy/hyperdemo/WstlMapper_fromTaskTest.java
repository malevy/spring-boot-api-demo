package net.malevy.hyperdemo;

import net.malevy.hyperdemo.messageconverters.WellKnown;
import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.support.westl.Action;
import net.malevy.hyperdemo.support.westl.Datum;
import net.malevy.hyperdemo.support.westl.Wstl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class WstlMapper_fromTaskTest {

    private Wstl wstl;

    @Before
    public void whenATaskIsMappedToWstl() {

        WstlMapper mapper = new WstlMapper(UriComponentsBuilder.fromUriString("http://localhost"));

        Task t = new Task(42, "new one");
        t.setDescription("more stuff");
        t.setDue(LocalDate.of(2017, 11, 12));

        wstl = mapper.fromTask(t);
    }

    @Test
    public void thenThePropertiesAreSet() {

        Datum taskItem = wstl.getData().stream()
                .findFirst()
                .get();

        Map<String, String> properties = taskItem.getProperties();
        assertEquals("title is wrong","new one", properties.get(Task.Properties.title));
        assertEquals("description is wrong", "more stuff", properties.get(Task.Properties.description));
        assertEquals("importance is wrong", Task.Importance.NORMAL, Task.Importance.lookup(properties.get(Task.Properties.importance)).get());
        assertEquals("due date is wrong",
                LocalDate.of(2017, 11, 12),
                LocalDate.parse(properties.get(Task.Properties.due)));
    }

    @Test
    public void thenTheTaskAsASelfLink() {
        checkAction(WstlMapper.Actions.SELF, Action.Type.Safe, Action.RequestType.Read, "http://localhost/tasks/42");
    }

    @Test
    public void thenTheTaskHasDeleteLink() {
        checkAction(WstlMapper.Actions.DELETE, Action.Type.Unsafe, Action.RequestType.Remove, "http://localhost/tasks/42");
    }

    @Test
    public void thenTheTaskHasACompleteLink() {
        checkAction(WstlMapper.Actions.MARKCOMPLETE,
                Action.Type.Unsafe,
                Action.RequestType.Replace,
                "http://localhost/tasks/42/complete");
    }

    @Test
    public void thenTheDocumentHasAnAddLink() {
        Action addAction = wstl.getActions().stream()
                .filter(a -> WstlMapper.Actions.ADD.equalsIgnoreCase(a.getName()))
                .findFirst()
                .get();

        assertThat(addAction.getName(), is("add"));
        assertThat(addAction.getRels(), hasItem(WellKnown.Rels.ADD));
        assertThat(addAction.getType(), is(Action.Type.Unsafe));
        assertThat(addAction.getAction(), is(Action.RequestType.Append));
        assertThat(addAction.getHref().toString(), is("http://localhost/tasks/"));
        assertThat(addAction.hasInputs(), is(true));
    }

    private void checkAction(String action, Action.Type type, Action.RequestType requestType, String uri) {
        Datum taskItem = wstl.getData().stream()
                .findFirst()
                .get();

        Action actualAction = taskItem.getActions().stream()
                .filter(a -> action.equalsIgnoreCase(a.getName()))
                .findFirst()
                .get();

        assertEquals("type is wrong", type, actualAction.getType());
        assertEquals("action is wrong", requestType, actualAction.getAction());
        assertEquals("uri is wrong", uri, actualAction.getHref().toString());

    }

}
