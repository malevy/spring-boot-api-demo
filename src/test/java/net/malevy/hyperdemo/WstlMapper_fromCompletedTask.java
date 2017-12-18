package net.malevy.hyperdemo;

import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.support.westl.Action;
import net.malevy.hyperdemo.support.westl.Datum;
import net.malevy.hyperdemo.support.westl.Wstl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.util.UriComponentsBuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.Map;

public class WstlMapper_fromCompletedTask {

    private Wstl wstl;

    @Before
    public void whenATaskIsMappedToWstl() {

        WstlMapper mapper = new WstlMapper(UriComponentsBuilder.fromUriString("http://localhost"));

        Task t = new Task(42, "new one");
        t.setDescription("more stuff");
        t.setDue(LocalDate.of(2017, 11, 12));
        t.markComplete(LocalDate.of(2017, 11, 12));

        wstl = mapper.fromTask(t);
    }

    @Test
    public void thenThereIsNoCompleteAction() {

        Datum taskItem = wstl.getData().stream()
                .findFirst()
                .get();

        final boolean completeLinkIsPresnet = taskItem.getActions().stream()
                .anyMatch(a -> WstlMapper.Actions.MARKCOMPLETE.equalsIgnoreCase(a.getName()));

        assertThat(completeLinkIsPresnet, is(false));
    }


}
