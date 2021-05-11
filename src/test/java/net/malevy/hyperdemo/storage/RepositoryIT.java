package net.malevy.hyperdemo.storage;

import net.malevy.hyperdemo.HypermediaDemoApplication;
import net.malevy.hyperdemo.TaskRepository;
import net.malevy.hyperdemo.models.dataaccess.TaskDto;
import net.malevy.hyperdemo.models.domain.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDate;
import java.util.Optional;

@ActiveProfiles(profiles = {"h2"})
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {HypermediaDemoApplication.class})
@WebAppConfiguration
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
        Assertions.assertTrue(result.isPresent(), "task was not returned");
        TaskDto fetched = result.get();

        Assertions.assertEquals(dto.getTitle(), fetched.getTitle(),"title is wrong" );
        Assertions.assertEquals(dto.getDescription(), fetched.getDescription(),"description is wrong" );
        Assertions.assertEquals(dto.getDue(), fetched.getDue(), "due date is wrong");
        Assertions.assertNull(fetched.getCompletedOn(), "completed should not be set");
        Assertions.assertEquals(Task.Importance.NORMAL.toString(), fetched.getImportance(), "importance defaults to NORMAL");


    }

}
