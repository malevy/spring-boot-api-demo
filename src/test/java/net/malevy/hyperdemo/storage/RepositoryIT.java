package net.malevy.hyperdemo.storage;

import net.malevy.hyperdemo.HypermediaDemoApplication;
import net.malevy.hyperdemo.TaskRepository;
import net.malevy.hyperdemo.commands.CommandFactoryIT;
import net.malevy.hyperdemo.models.dataaccess.TaskDto;
import net.malevy.hyperdemo.models.domain.Task;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.*;

@ActiveProfiles(profiles = {"h2"})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HypermediaDemoApplication.class})
// just to verify that the config is correct
public class RepositoryIT {

    @Autowired
    TaskRepository repository;

    @Test
    public void canRoundTripATask() {

        TaskDto dto = new TaskDto(null, "round-trip-task",
                "some-description", null,
                LocalDate.of(2017, 11, 12),
                null);

        dto = repository.save(dto);

        Optional<TaskDto> result = repository.findById(dto.getId());
        assertTrue("task was not returned", result.isPresent());
        TaskDto fetched = result.get();

        assertEquals("title is wrong", dto.getTitle(), fetched.getTitle());
        assertEquals("description is wrong", dto.getDescription(), fetched.getDescription());
        assertEquals("due date is wrong", dto.getDue(), fetched.getDue());
        assertNull("completed should not be set", fetched.getCompletedOn());
        assertEquals("importance defaults to NORMAL", Task.Importance.NORMAL.toString(), fetched.getImportance());


    }

}
