package net.malevy.hyperdemo;

import net.malevy.hyperdemo.messageconverters.WellKnown;
import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.support.westl.Action;
import net.malevy.hyperdemo.support.westl.Datum;
import net.malevy.hyperdemo.support.westl.Wstl;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;

public class WstlMapper_fromTaskTest {

    private Wstl wstl;

    @BeforeEach
    public void whenATaskIsMappedToWstl() {

        WstlMapper mapper = new WstlMapper(UriComponentsBuilder.fromUriString("http://localhost"));

        Task t = new Task(42, "new one", "jack");
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
        Assertions.assertEquals("new one", properties.get(Task.Properties.title), "title is wrong");
        Assertions.assertEquals("more stuff", properties.get(Task.Properties.description), "description is wrong");
        Assertions.assertEquals(Task.Importance.NORMAL, Task.Importance.lookup(properties.get(Task.Properties.importance)).get(), "importance is wrong");
        Assertions.assertEquals(
                LocalDate.of(2017, 11, 12),
                LocalDate.parse(properties.get(Task.Properties.due)), "due date is wrong");

        Assertions.assertEquals("jack", properties.get(Task.Properties.owner), "owner is wrong");

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

        MatcherAssert.assertThat(addAction.getName(), is("add"));
        MatcherAssert.assertThat(addAction.getRels(), hasItem(WellKnown.Rels.ADD));
        MatcherAssert.assertThat(addAction.getType(), is(Action.Type.Unsafe));
        MatcherAssert.assertThat(addAction.getAction(), is(Action.RequestType.Append));
        MatcherAssert.assertThat(addAction.getHref().toString(), is("http://localhost/tasks/"));
        MatcherAssert.assertThat(addAction.hasInputs(), is(true));
    }

    private void checkAction(String action, Action.Type type, Action.RequestType requestType, String uri) {
        Datum taskItem = wstl.getData().stream()
                .findFirst()
                .get();

        Action actualAction = taskItem.getActions().stream()
                .filter(a -> action.equalsIgnoreCase(a.getName()))
                .findFirst()
                .get();

        Assertions.assertEquals( type, actualAction.getType(),"type is wrong");
        Assertions.assertEquals( requestType, actualAction.getAction(),"action is wrong");
        Assertions.assertEquals( uri, actualAction.getHref().toString(),"uri is wrong");

    }

}
