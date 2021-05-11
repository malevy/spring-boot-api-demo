package net.malevy.hyperdemo;

import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.support.westl.Datum;
import net.malevy.hyperdemo.support.westl.Wstl;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriComponentsBuilder;

import static org.hamcrest.CoreMatchers.is;

import java.time.LocalDate;

public class WstlMapper_fromCompletedTask {

    private Wstl wstl;

    @BeforeEach
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

        MatcherAssert.assertThat(completeLinkIsPresnet, is(false));
    }


}
